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

import com.sun.lwuit.html.DefaultHTMLCallback;
import com.sun.lwuit.html.HTMLComponent;
import java.util.Vector;

//class, which used to react, when come events occured in HTMLComponent, which used in TextView
public class jBQHTMLCallback extends DefaultHTMLCallback {

    //if user clicks our custom type of hyperlink, we must display reference of opened text in title of form
    //if it another book and chapter, then we also must load another text
    public boolean linkClicked(HTMLComponent htmlC, java.lang.String url) {
        if (url.startsWith("jBQ:///")) {
            Reference reference = new Reference(url);
            TextView.show(reference);
            return false;
        } else if (url.indexOf(TextModule.anchorPrefix) != -1)
            TextView.updateReference(Integer.parseInt(Util.splitString(url, TextModule.anchorPrefix, 2)[1]));
        // this kind of references: Ex 7:10, Ex 7.10, Ex 7
        else if (url.indexOf(' ') != -1) {
            Reference reference = null;
            //too many places, where url may be incorrect, so in case of any error, we refuse to consider this link as
            //bible reference
            try {
                //get all available information from url itself
                String[] parts = Util.splitString(url, "/", -1);
                parts = Util.splitString(parts[parts.length - 1], " ", 2); //split Book abbreviation and Chapter:Verse pair
                String bookAbbreviation = parts[0];
                int chapter = -1;
                int verse = 1;
                //url may be full with verse number included, or may be with chapter number only
                String[] numbersText = null;
                if(parts[1].indexOf(':') != -1)
                    numbersText = Util.splitString(parts[1], ":", 2);
                else if(parts[1].indexOf('.') != -1)
                    numbersText = Util.splitString(parts[1], ".", 2);
                else {
                    numbersText = new String[1];
                    numbersText[0] = parts[1];
                }
                //urls may end with any symbol, so we just consider all numeric symbols as verse or chapter number before
                //we meet any non-numeric symbol
                //delete all symbols after last number
                String lastNumberText = "";
                char[] digits = (new String("0123456789")).toCharArray();
                for (int i = 0; i < numbersText[numbersText.length - 1].length(); i++) {
                    boolean digitFound = false;
                    char currentCharacter = numbersText[numbersText.length - 1].charAt(i);
                    for (int j = 0; j < digits.length; j++)
                        if (currentCharacter == digits[j]) {
                            lastNumberText += currentCharacter;
                            digitFound = true;
                            break;
                        }
                    if (!digitFound)
                        break;                
                }
                numbersText[numbersText.length - 1] = lastNumberText;
                // determine chapter and verse
                if (numbersText.length > 1) {
                    chapter = Integer.parseInt(numbersText[0].trim());
                    verse = Integer.parseInt(numbersText[1].trim());
                } else
                    chapter = Integer.parseInt(numbersText[0].trim());
                // find bible to open
                Reference previousBibleReference = History.lastBibleElement();
                TextModule module = (TextModule) Modules.getByName(previousBibleReference.getModule());
                // find full chapter name by abbreviation
                String bookName = null;
                Vector bookNames = module.bookNames();
                for (int i = 0; i < bookNames.size(); i++) {
                    String[] bookShortNames = module.getBookShortNames((String) bookNames.elementAt(i));
                    for (int j = 0; j < bookShortNames.length; j++)
                        if (bookAbbreviation.equals(bookShortNames[j])) {
                            bookName = (String) bookNames.elementAt(i);
                            break;
                        }
                    if (bookName != null)
                        break;
                }
                // create reference
                reference = new Reference(module.getName(), bookName, chapter, verse);
                
            } catch (Throwable exception) {}
            if (reference != null) {
                TextView.show(reference);
                return false;
            }
        }
        return super.linkClicked(htmlC, url);
    }
}
