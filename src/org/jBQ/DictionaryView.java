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

import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.TextAreaPatch;
import com.sun.lwuit.Button;
import java.util.Vector;

// class for work with dictionary. Provides possibility to choose dictionary article by typing word or words prefix
public class DictionaryView extends BaseDialog {

    private Command cancelCommand;
    private TextAreaPatch wordField;
    private DictionaryModule currentDictionary;

    public DictionaryView() {
        //create form
        super("dictionary");
        //add commands
        cancelCommand = super.createCommand("cancel");
        form.addCommandListener(this);
        wordField = new TextAreaPatch();
        wordField.addActionListener(this);
    }

    private void updateButtons() {
        form.removeAll();
        form.addComponent(instance().wordField);
        //get list of possible words
        Vector possibilities = currentDictionary.getPossibilities(wordField.getText());
        //place buttons at form
        if (possibilities != null)
            for (int i = 0; i < possibilities.size(); i++)
                form.addComponent(new Button(new Command((String) possibilities.elementAt(i))));
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == null) {
            updateButtons();
            return;
        }
        if (event.getCommand() == cancelCommand) {
            TextView.show();
            return;
        }
        TextView.show(new Reference(currentDictionary.getName(), event.getCommand().getCommandName(), 1, 1));
    }

    public static void show(DictionaryModule dictionary) {
        instance().form.setTitle(dictionary.getFullName());
        instance().currentDictionary = dictionary;
        //add components
        instance().updateButtons();
        instance().form.show();
    }

    private static DictionaryView instance() {
        return instance;
    }
    private static DictionaryView instance = new DictionaryView();
}
