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
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.TextAreaPatch;
import com.sun.lwuit.Label;
import com.sun.lwuit.util.Resources;
import com.sun.lwuit.ComboBox;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

//class for keeping settings and responsible for translation service
public class Settings extends BaseDialog {

    Reference currentReference;
    private String modulesPath;
    private boolean isNewLineAfterVerse;
    private boolean isStrongsEnabled;
    private Command cancelCommand;
    private Command saveCommand;
    private CheckBox newLineAfterVerseCheck;
    private CheckBox strongsEnabledCheck;
    private Label modulesPathLabel;
    private TextAreaPatch modulesPathField;
    private Label languageLabel;
    private ComboBox localesComboBox;
    private Hashtable currentLocale;
    private String currentLocaleName;
    private Vector availableLocales;
    private Resources languagesResource = null;
    private boolean isControlsCreated = false;

    public Settings() {
        super("Settings");
        form.addCommandListener(this);
        //try to read current locale from settings or determine it automatically
        currentLocaleName = Platform.getSettingsKey("Settings", "currentLocale");
        getAvailableLocales();
        if (currentLocaleName == null) {
            //determine locale
            String systemLocale = Platform.getSystemLocale();
            for (int i = 0; i < availableLocales.size(); i++) {
                String testLocale = (String) availableLocales.elementAt(i);
                if (testLocale.equals(systemLocale))
                    currentLocaleName = testLocale;                
            }
            //default locale
            if (currentLocaleName == null)
                currentLocaleName = "en";
        }
        currentLocale = languagesResource.getL10N("Lang", currentLocaleName);
        //get settings from RMS or INI
        modulesPath = Platform.getSettingsKey("Settings", "modulesPath");
        if (modulesPath == null)
            modulesPath = "bqmodules";
        isNewLineAfterVerse = getBooleanFromSettings("isNewLineAfterVerse");
        isStrongsEnabled = getBooleanFromSettings("isStrongsEnabled");
    }

    private void createControls() {
        form.setTitle(tr("settings"));
        //controls
        newLineAfterVerseCheck = createCheckBox("newLineAfterVerse");
        strongsEnabledCheck = createCheckBox("strongsEnabled");
        modulesPathLabel = createLabel("modulesPath");
        modulesPathField = new TextAreaPatch("");
        form.addComponent(modulesPathField);
        languageLabel = createLabel("language");
        localesComboBox = createComboBox();
        for (int i = 0; i < availableLocales.size(); i++) {
            String locale = (String) availableLocales.elementAt(i);
            localesComboBox.addItem(languagesResource.getL10N("Lang", locale).get("languageName"));
            if (locale.equals(currentLocale))
                localesComboBox.setSelectedIndex(i);
        }
        //add commands
        cancelCommand = super.createCommand("cancel");
        saveCommand = super.createCommand("save");
        isControlsCreated = true;
    }

    public static String getCurrentLocaleName() {
        return instance().currentLocaleName;
    }

    public static Hashtable getCurrentLocale() {
        return instance().currentLocale;
    }
    
    //get translation for this string
    public static String tr(String id) {
        Object result = instance().currentLocale.get(id);
        if (result == null)
            return id;
        else
            return (String) result;
    }

    private void getAvailableLocales() {
        try {
            languagesResource = Resources.open("/languages.res");
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        Enumeration availableLocalesEnumeration = languagesResource.listL10NLocales("Lang");
        availableLocales = new Vector();
        while (availableLocalesEnumeration.hasMoreElements())
            availableLocales.addElement(availableLocalesEnumeration.nextElement());
    }

    private boolean getBooleanFromSettings(String name) {
        String resultString = Platform.getSettingsKey("Settings", name);
        if (resultString == null || resultString.equals("true"))
            return true;
        return false;
    }

    public static void show(Reference reference) {
        if (!instance().isControlsCreated)
            instance().createControls();
        instance().currentReference = reference;
        //fill controls
        instance().newLineAfterVerseCheck.setSelected(instance().isNewLineAfterVerse);
        instance().strongsEnabledCheck.setSelected(instance().isStrongsEnabled);
        instance().modulesPathField.setText(instance().modulesPath);
        for (int i = 0; i < instance().availableLocales.size(); i++)
            if (((String) instance().availableLocales.elementAt(i)).equals(instance().currentLocaleName))
                instance().localesComboBox.setSelectedIndex(i);
        instance().form.show();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getCommand() == cancelCommand)
            TextView.show();
        if (event.getCommand() == saveCommand) {
            //get settings from controls
            instance().isNewLineAfterVerse = instance().newLineAfterVerseCheck.isSelected();
            instance().isStrongsEnabled = instance().strongsEnabledCheck.isSelected();
            instance().modulesPath = instance().modulesPathField.getText();
            instance().currentLocaleName = (String) instance().availableLocales.elementAt(instance().localesComboBox.getSelectedIndex());
            //save settings
            Platform.addSettingsKey("Settings", "modulesPath", instance().modulesPath);
            writeBooleanToSettings("isNewLineAfterVerse", isNewLineAfterVerse);
            writeBooleanToSettings("isStrongsEnabled", isStrongsEnabled);
            Platform.addSettingsKey("Settings", "currentLocale", instance().currentLocaleName);
            //close form
            TextView.show(instance().currentReference);
        }
    }

    private void writeBooleanToSettings(String name, boolean value) {
        if (value)
            Platform.addSettingsKey("Settings", name, "true");
        else
            Platform.addSettingsKey("Settings", name, "false");
    }

    //used for "Verse starts from new line" option
    static String getVerseDivisor() {
        if (instance().isNewLineAfterVerse)
            return "<br>";
        else
            return " ";
    }

    static boolean isStrongsEnabled() {
        return instance().isStrongsEnabled;
    }

    static String getModulesPath() {
        return instance().modulesPath;
    }

    private static Settings instance() {
        return instance;
    }
    private static Settings instance = new Settings();
}
