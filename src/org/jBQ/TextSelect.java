/**
 * Distribution License:
 * jBQ is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 of higher as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 */

package org.jBQ;

import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.Command;
import com.sun.lwuit.Button;
import com.sun.lwuit.layouts.*;
import java.util.Vector;

//main class to select text in current module
public class TextSelect extends BaseDialog {

    private Command cancelCommand;
    private TextModule currentModule;
    private String chosenBook;

    public TextSelect() {
        //create form
        super(Settings.tr("selectPassage"));
        form.setLayout(new FlowLayout());
        //add commands
        cancelCommand = super.createCommand("cancel");
        form.addCommandListener(this);
    }

    //used to show this dialog    
    public static void show(Reference reference) {
        if (reference == null)
            return;
        //clear from previous usage
        instance().form.removeAll();
        instance().chosenBook = null;
        //get module information
        instance().currentModule = (TextModule) Modules.getByName(reference.getModule());
        Vector bookList = instance().currentModule.bookNames();
        //show book choice
        for (int i = 0; i < bookList.size(); i++) {
            Button newButton = new Button(new Command((String) bookList.elementAt(i)));
            instance().form.addComponent(newButton);
        }
        instance().form.animateLayout(0); //for flow layout to show scrollbar
        instance().form.show();
    }

    //if user selected Book, than we make buttons to select chapter    
    private void askChapter() {
        form.removeAll();
        int chaptersCount = instance().currentModule.chaptersCountInBook(chosenBook);
        int firstChapterNumber = instance().currentModule.getFirstChapterNumber();
        for (int i = firstChapterNumber; i <= chaptersCount + (firstChapterNumber - 1); i++) {
            Button newButton = new Button(new Command(Integer.toString(i)));
            form.addComponent(newButton);
        }
        form.revalidate();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == cancelCommand) {
            TextView.show();
            return;
        }
        //if we still chosing book
        if (chosenBook == null) {
            chosenBook = event.getCommand().getCommandName();
            askChapter();
            //all we need to show text, we know
        } else
            TextView.show(new Reference(currentModule.getName(), chosenBook, Integer.parseInt(event.getCommand().getCommandName()), 1));
    }

    private static TextSelect instance() {
        return instance;
    }
    private static TextSelect instance = new TextSelect();
}
