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
import com.sun.lwuit.Label;
import com.sun.lwuit.TextAreaPatch;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.list.ListModel;
import java.util.Vector;

//interface to specify search and initiate it
public class Search extends BaseDialog {

    private Command cancelCommand;
    private Command searchCommand;
    private CheckBox isCaseSensitiveCheckbox;
    private Label whatFindLabel;
    private TextAreaPatch keywordsField;
    private Label fromLabel;
    private ComboBox fromComboBox;
    private Label toLabel;
    private ComboBox toComboBox;

    public Search() {
        super(Settings.tr("search"));
        form.addCommandListener(this);
        //controls
        whatFindLabel = createLabel("whatFind");
        keywordsField = new TextAreaPatch("");
        form.addComponent(keywordsField);
        isCaseSensitiveCheckbox = new CheckBox("isCaseSensitiveSearch");
        isCaseSensitiveCheckbox.setSelected(true);
        form.addComponent(isCaseSensitiveCheckbox);
        fromLabel = createLabel("from");
        fromComboBox = createComboBox();
        toLabel = createLabel("to");
        toComboBox = createComboBox();
        //commands
        cancelCommand = super.createCommand("cancel");
        searchCommand = super.createCommand("search");
    }

    private static void clearComboBox(ComboBox box) {
        ListModel listModel = box.getModel();
        int size = listModel.getSize();
        for (int i = 0; i < size; i++)
            listModel.removeItem(0);
        box.setModel(listModel);
    }

    public static void show(TextModule module) {
        clearComboBox(instance().fromComboBox);
        clearComboBox(instance().toComboBox);
        Vector bookNames = module.bookNames();
        //we provide possibility to choose from which book of module user wants to find to which book
        for(int i = 0; i < bookNames.size(); i++) {
            instance().fromComboBox.addItem(bookNames.elementAt(i));
            instance().toComboBox.addItem(bookNames.elementAt(i));
        }
        instance().form.show();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == cancelCommand)
            TextView.show();
        if (event.getCommand() == searchCommand) {
            final boolean isCaseSensitive = isCaseSensitiveCheckbox.isSelected();
            final String keywordsString = keywordsField.getText().trim();
            if (keywordsString.length() < 3)
                Dialog.show("error", "searchTextTooSmall", "ok", null);
            else {
                LoadingScreen.show(Settings.tr("searching"));
                new Thread() {
                    public void run() {
                        TextView.showSearchResults(keywordsString, isCaseSensitive, fromComboBox.getSelectedIndex(), toComboBox.getSelectedIndex());
                    }
                }.start();
            }
        }
    }

    private static Search instance() {
        return instance;
    }
    private static Search instance = new Search();
}
