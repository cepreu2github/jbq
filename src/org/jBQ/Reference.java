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

//class, representing reference to some text in BibleQuote module
public class Reference {

    private String module;
    private String entry;
    private int chapter;
    private int verse;

    public Reference(String module, String entry, int chapter, int verse) {
        this.module = module;
        this.entry = entry;
        this.chapter = chapter;
        this.verse = verse;
    }

    //to get Reference from URLs like "jBQ:///King James Version/Бытие/1#jBQ_verse_1"    
    public Reference(String url) {
        url = url.substring(7);
        String[] parts = Util.splitString(url, "/", 3);
        module = parts[0];
        entry = parts[1];
        String[] numbers = Util.splitString(parts[2], TextModule.anchorPrefix, 2);
        chapter = Integer.parseInt(numbers[0]);
        verse = Integer.parseInt(numbers[1]);
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }

    public String getModule() {
        return module;
    }

    public String getEntry() {
        return entry;
    }

    public int getChapter() {
        return chapter;
    }

    public int getVerse() {
        return verse;
    }

    //String representation of Reference
    public String toString() {
        return "jBQ:///" + module + "/" + entry + "/" + Integer.toString(chapter) + TextModule.anchorPrefix + Integer.toString(verse);
    }

    //used in GUI part to show for user information about current passage
    public String toHumanReadableString() {
        Module moduleObject = Modules.getByName(module);
        String result = moduleObject.getShortName() + ":" + entry;
        if (moduleObject.getType() != Module.Types.DICTIONARY)
            result += " " + Integer.toString(chapter) + ":" + Integer.toString(verse);
        return result;
    }
}
