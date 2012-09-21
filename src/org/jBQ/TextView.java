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

import com.sun.lwuit.html.HTMLComponent;
import com.sun.lwuit.Command;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.Display;
import com.sun.lwuit.Dialog;

//main class to view text and access to any other program functions
public class TextView extends BaseDialog {

    HTMLComponent HTMLView;
    private Command exitCommand;
    private Command helpCommand;
    private Command gotoCommand;
    private Command moduleCommand;
    private Command historyCommand;
    private Command aboutCommand;
    private Command settingsCommand;
    private Command searchCommand;
    private Command backCommand;
    private Reference currentReference = null;

    private TextView() {
        //create form
        super(Settings.tr("bible"));
        form.setScrollable(false);
        HTMLView = new HTMLComponent(new jBQDocumentRequestHandler());
        HTMLView.setHTMLCallback(new jBQHTMLCallback());
        form.addComponent(HTMLView);
        //add commands
        backCommand = super.createCommand("back");
        gotoCommand = super.createCommand("goto");
        exitCommand = super.createCommand("exit");
        helpCommand = super.createCommand("help");
        moduleCommand = super.createCommand("module");
        historyCommand = super.createCommand("history");
        aboutCommand = super.createCommand("about");
        settingsCommand = super.createCommand("settings");
        searchCommand = super.createCommand("search");
        form.addCommandListener(this);
        form.addKeyListener(Platform.keyPageDownCode, this);
        form.addKeyListener(Platform.keyPageUpCode, this);
    }

    //show text by given Reference
    public static void show(Reference ref) {
        if (ref == null) {
            show();
            return;
        }
        History.addElement(ref);
        //set right Title
        instance().currentReference = ref;
        instance().form.setTitle(ref.toHumanReadableString());
        //get chapter text
        jBQDocumentRequestHandler.setReference(ref);
        String textURL = Modules.getByName(ref.getModule()).entryFileName(ref.getEntry());
        instance().HTMLView.setPage(textURL + TextModule.anchorPrefix + Integer.toString(ref.getVerse()));
        jBQDocumentRequestHandler.setReference(null);
        //determine possibility to use search
        Module module = Modules.getByName(ref.getModule());
        if (module.getType() != Module.Types.DICTIONARY)
            instance().form.addCommand(instance().searchCommand);
        else
            instance().form.removeCommand(instance().searchCommand);
        //show form
        instance().form.show();
    }

    public static void showSearchResults(String text, boolean isCaseSensitive, int from, int to) {
        //set right Title
        instance().form.setTitle("searchResults");
        //set text to search results
        TextModule module = (TextModule) Modules.getByName(instance().currentReference.getModule());
        instance().HTMLView.setBodyText(module.search(text, isCaseSensitive, from, to), "UTF-8");
        //set possibility to use search
        instance().form.addCommand(instance().searchCommand);
        //show form
        instance().form.show();
    }

    //used for show TextView again after canceling exit from some dialogs
    public static void show() {
        instance().form.showBack();
    }

    //actualize information in Title to what user opened now
    public static void updateReference(int verse) {
        instance().currentReference.setVerse(verse);
        instance().form.setTitle(instance().currentReference.toHumanReadableString());
    }
    
    public static void updateReference(Reference reference) {
        instance().currentReference = reference;
        instance().form.setTitle(reference.toHumanReadableString());
    }

    public static void showHelp() {
        instance().HTMLView.setPage("jar:///help_" + Settings.getCurrentLocaleName() + ".htm");
        instance().form.show();
        instance().form.setTitle("help");
    }

    public static void showAbout() {
        instance().HTMLView.setPage("jar:///about_" + Settings.getCurrentLocaleName() + ".htm");
        instance().form.show();
        instance().form.setTitle("about");
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getKeyEvent() == Platform.keyPageDownCode)
            HTMLView.scrollPages(1, true);
        else if (event.getKeyEvent() == Platform.keyPageUpCode)
            HTMLView.scrollPages(-1, true);
        else if (event.getCommand() == backCommand) {
            if (History.size() >= 2)
                show(History.elementAt(History.size() - 2));
        } else if (event.getCommand() == gotoCommand) {
            Module currentModule = Modules.getByName(currentReference.getModule());
            if (currentModule.getType() != Module.Types.DICTIONARY)
                TextSelect.show(currentReference);
            else
                DictionaryView.show((DictionaryModule) currentModule);
        } else if (event.getCommand() == moduleCommand)
            ModuleChoose.show(currentReference);
        else if (event.getCommand() == historyCommand)
            History.show();
        else if (event.getCommand() == settingsCommand)
            Settings.show(currentReference);
        else if (event.getCommand() == searchCommand)
            Search.show((TextModule) Modules.getByName(currentReference.getModule()));
        else if (event.getCommand() == helpCommand)
            showHelp();
        else if (event.getCommand() == aboutCommand)
            showAbout();
        else if (event.getCommand() == exitCommand) {
            History.save();
            Display.getInstance().exitApplication();
        }
    }

    private static TextView instance() {
        return instance;
    }
    private static TextView instance = new TextView();
}
