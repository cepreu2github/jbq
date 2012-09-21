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

import com.sun.lwuit.layouts.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.Command;
import com.sun.lwuit.Tabs;
import com.sun.lwuit.Container;
import com.sun.lwuit.Button;
import java.util.Vector;

//dialog for select module
public class ModuleChoose extends BaseDialog {

    private Tabs tabPanel;
    private Command cancelCommand;
    private Reference curRef;

    public ModuleChoose() {
        //create form
        super(Settings.tr("selectModule"));
        //placing tabs with buttons at form
        tabPanel = new Tabs();
        addButtonGroup(Module.Types.BIBLE, "bibles");
        addButtonGroup(Module.Types.COMMENTARY, "commentaries");
        addButtonGroup(Module.Types.BOOK, "books");
        addButtonGroup(Module.Types.DICTIONARY, "dictionaries");
        form.addComponent(tabPanel);
        //add commands
        cancelCommand = super.createCommand("cancel");
        form.addCommandListener(this);
    }

    //add tab for special module type at tabPanel
    private void addButtonGroup(int modulesType, String caption) {
        Container tab = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        //add buttons with all modules of this type at new tab
        Vector names = Modules.names(modulesType);
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.elementAt(i);
            String readableName = Modules.getByName(name).getFullName();
            Command command = new Command(readableName);
            command.putClientProperty("moduleName", name);
            tab.addComponent(new Button(command));
        }
        tabPanel.addTab(caption, tab);
    }

    //used to show this dialog    
    public static void show(Reference reference) {
        instance().curRef = reference;
        instance().form.show();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == cancelCommand) {
            TextView.show();
            return;
        }
        String newModuleName = (String) event.getCommand().getClientProperty("moduleName");
        Module newModule = Modules.getByName(newModuleName);
        if (newModule.getType() == Module.Types.DICTIONARY)
            DictionaryView.show((DictionaryModule) newModule);
        //in English Bible Genesis full name is Genesis, but in Russian is Бытие, but there also exist couple of small names
        //and some of them similar for similar book in any module. For example information about Gen. abbreviation exist
        //in all modules. So we need to find similar book not by full name, but by list of abbreviations, but it is harder
        //because it is comparison of every first list item with every second list item
        else if (Modules.getByName(curRef.getModule()).getType() != Module.Types.DICTIONARY) {
            String newBookName = Modules.findSimilarBook(curRef.getModule(), curRef.getEntry(), newModuleName);
            int chapterNumber = curRef.getChapter();
            if (chapterNumber == 0 && ((TextModule) newModule).getFirstChapterNumber() != 0)
                chapterNumber = 1;
            TextView.show(new Reference(newModuleName, newBookName, chapterNumber, curRef.getVerse()));
        //if we returning from view of dictionary to view of Bible or Book it is reasonable to open last
        //bible or book reference
        } else {
            Reference previousText = History.lastNonDictionaryElement();
            if (previousText != null) {
                String newBookName = Modules.findSimilarBook(previousText.getModule(), previousText.getEntry(), newModuleName);
                TextView.show(new Reference(newModuleName, newBookName, previousText.getChapter(), previousText.getVerse()));
            } else
                TextView.show(new Reference(newModuleName, ((TextModule) newModule).firstElementName(), 1, 1));
        }
    }

    private static ModuleChoose instance() {
        return instance;
    }
    private static ModuleChoose instance = new ModuleChoose();
}
