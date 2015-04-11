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
import com.sun.lwuit.io.FileSystemStorage;
import org.albite.io.decoders.AlbiteStreamReader;

//Vector of module, but with some service functions
public class Modules {

    private static final char FSSEP = Util.FSSEP;
    private final String MidletModulesPath = "modules/";
    private final String MidletModulesList = "modules/list.txt";
    private final String LocalStorageModulesPath = Settings.getModulesPath();
    Vector modules;

    public static boolean isEmpty() {
        return instance().modules.isEmpty();
    }

    public static Module getByName(String name) {
        for (int i = 0; i < instance().modules.size(); i++)
            if (((Module) instance().modules.elementAt(i)).getName().equals(name))
                return (Module) instance().modules.elementAt(i);
        return null;
    }

    //this method try to find book in second module, which have one of short names similar with one of short names of specified book of first module
    //examle for book - Genesis:
    //KJV: FullName = Genesis, ShortName = Ge. Ge Gen. Gen Gn. Gn Genesis
    //RST: FullName = Бытие, ShortName = Быт. Быт Бт. Бт Бытие Ge. Ge Gen. Gen Gn. Gn Genesis
    public static String findSimilarBook(String moduleName, String bookName, String secondModuleName) {
        //get from module short names of book
        String[] shortNamesArray = ((TextModule) getByName(moduleName)).getBookShortNames(bookName);
        if (shortNamesArray == null)
            return bookName;
        String shortNames = "";
        for (int i = 0; i < shortNamesArray.length; i++)
            shortNames = shortNames + " " + shortNamesArray[i];
        TextModule secondModule = (TextModule) getByName(secondModuleName);
        //for every book of second module get short names and compare every short name with evere short name of interesting book
        Vector bookNames = secondModule.bookNames();
        for (int i = 0; i < bookNames.size(); i++) {
            String[] candidateShortNames = secondModule.getBookShortNames((String) bookNames.elementAt(i));
            for (int j = 0; j < candidateShortNames.length; j++)
                if (shortNames.indexOf(candidateShortNames[j]) != -1 && candidateShortNames[j].length() != 0)
                    return (String) bookNames.elementAt(i);
        }
        return bookName;
    }

    //vector of modules names
    public static Vector names(int type) {
        Vector result = new Vector();
        for (int i = 0; i < instance().modules.size(); i++) {
            Module module = (Module) instance().modules.elementAt(i);
            if (type == Module.Types.ANY || module.getType() == type)
                result.addElement(module.getName());
        }
        return result;
    }

    //just quick access to module function GetText for GUI parts of program
    public static String getText(Reference r) {
        Module module = getByName(r.getModule());
        if (module == null)
            return null;
        return module.getText(r);
    }

    private Modules() {
        modules = new Vector();
        //locate modules at jar file
        AlbiteStreamReader modulesListStream = null;
        try {
            modulesListStream = new AlbiteStreamReader(Util.getResourceAsStream("jar:///" + MidletModulesList), "utf-8");
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        String line = Util.readLine(modulesListStream);
        while (line != null) {
            if (!line.equals(""))
                if (line.substring(line.length() - 4).toLowerCase().equals(".idx"))
                    modules.addElement(new DictionaryModule(Util.GetFileNameInProperCase("jar:///" + MidletModulesPath + line)));
                else
                    modules.addElement(new TextModule(Util.GetFileNameInProperCase("jar:///" + MidletModulesPath + line)));
            line = Util.readLine(modulesListStream);
        }
        //locate modules at phone memory and memory card via JSR-75
        FileSystemStorage FSSI = FileSystemStorage.getInstance();
        String[] Roots;
        try {
            Roots = FSSI.getRoots();
        } catch (Throwable ex) {
            Roots = new String[0];
        }
        //list of possible modules locations
        Vector locations = new Vector();
        Vector dictionariesLocations = new Vector();
        for (int i = 0; i < Roots.length; i++) {
            locations.addElement(Roots[i] + LocalStorageModulesPath);
            locations.addElement(Util.GetFileNameInProperCase(Roots[i] + LocalStorageModulesPath + FSSEP + "Commentaries"));
            dictionariesLocations.addElement(Util.GetFileNameInProperCase(Roots[i] + LocalStorageModulesPath + FSSEP + "Dictionaries"));
            dictionariesLocations.addElement(Util.GetFileNameInProperCase(Roots[i] + LocalStorageModulesPath + FSSEP + "Strongs"));
        }
        //find all modules at this locations
        for (int i = 0; i < locations.size(); i++) {
            String[] directoryList = null;
            try {
                directoryList = FSSI.listFiles((String) locations.elementAt(i));
            } catch (Throwable ex) {
            }
            //check every folder in modules directory for BIBLEQT.INI and create module object
            if (directoryList != null)
                for (int j = 0; j < directoryList.length; j++) {
                    directoryList[j] = (String) locations.elementAt(i) + FSSEP + directoryList[j];
                    if (FSSI.isDirectory(directoryList[j]) && FSSI.exists(Util.GetFileNameInProperCase(directoryList[j] + FSSEP + "BIBLEQT.INI")))
                        modules.addElement(new TextModule(Util.GetFileNameInProperCase(directoryList[j] + FSSEP + "BIBLEQT.INI")));
                }
        }
        //find all dictionaries
        for (int i = 0; i < dictionariesLocations.size(); i++) {
            String[] filesList = null;
            try {
                filesList = FSSI.listFiles((String) dictionariesLocations.elementAt(i));
            } catch (Throwable ex) {
            }
            //check every folder in modules directory for BIBLEQT.INI and create module object
            if (filesList != null)
                for (int j = 0; j < filesList.length; j++) {
                    filesList[j] = (String) dictionariesLocations.elementAt(i) + FSSEP + filesList[j];
                    if (filesList[j].substring(filesList[j].length() - 4).toLowerCase().equals(".idx"))
                        modules.addElement(new DictionaryModule(filesList[j]));
                }
        }
    }

    //used to reload all dictionary modules by Settings module in case of dictionary default encoding changes
    public static void reloadDictionaries() {
        for (int i = 0; i < instance().modules.size(); i++) {
            Module module = (Module) instance().modules.elementAt(i);
            if (module.getType() == Module.Types.DICTIONARY)
                ((DictionaryModule) module).reload();
        }        
    }

    private static Modules instance() {
        return instance;
    }
    private static Modules instance = new Modules();
}
