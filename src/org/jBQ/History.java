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

import java.util.Vector;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.Command;
import com.sun.lwuit.Button;

//class for keeping history of opened texts
public class History extends BaseDialog {

    private Vector history;
    private Command cancelCommand;
    private Command[] goCommands;
    private static final int maxSize = 20;

    private History() {
        super("history");
        history = new Vector();
        //reading history from RMS or INI
        for (int i = 0; i < maxSize; i++) {
            String url = Platform.getSettingsKey("History", "Record" + Integer.toString(i));
            if (url != null) {
                Reference reference = new Reference(url);
                if (Modules.getByName(reference.getModule()) != null)
                    history.addElement(reference);
            }
        }
        //add commands
        goCommands = new Command[maxSize];
        cancelCommand = super.createCommand("cancel");
        form.addCommandListener(this);
    }

    public static void save() {
        for (int i = 0; i < instance().history.size(); i++)
            Platform.addSettingsKey("History", "Record" + Integer.toString(i), ((Reference) instance().history.elementAt(i)).toString());
    }

    public static void addElement(Reference element) {
        if (instance().history.size() >= maxSize)
            instance().history.removeElementAt(0);
        instance().history.addElement(element);
    }

    public static Reference lastElement() {
        return (Reference) instance().history.lastElement();
    }

    public static Reference lastNonDictionaryElement() {
        for (int i = instance().history.size() - 1; i > -1; i--) {
            Reference element = (Reference) instance().history.elementAt(i);
            if (Modules.getByName(element.getModule()).getType() != Module.Types.DICTIONARY)
                return element;
        }
        return null;
    }

    public static int size() {
        return instance().history.size();
    }

    public static void show() {
        instance().form.removeAll();
        for (int i = instance().history.size() - 1; i > -1; i--) {
            instance().goCommands[i] = new Command(((Reference) instance().history.elementAt(i)).toHumanReadableString());
            Button newButton = new Button(instance().goCommands[i]);
            instance().form.addComponent(newButton);
        }
        instance().form.show();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == cancelCommand) {
            TextView.show();
            return;
        }
        for (int i = instance().history.size() - 1; i > -1; i--)
            if (event.getCommand() == goCommands[i])
                TextView.show((Reference) history.elementAt(i));
    }

    private static History instance() {
        return instance;
    }
    private static History instance = new History();
}
