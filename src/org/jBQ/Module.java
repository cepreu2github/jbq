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

import com.sun.lwuit.io.FileSystemStorage;

//represents one BibleQuote module and provides some functions to work with
public abstract class Module {

    public class Types {

        public static final int ANY = 0;
        public static final int COMMENTARY = 1;
        public static final int BIBLE = 2;
        public static final int BOOK = 3;
        public static final int DICTIONARY = 4;
    }
    protected String encoding = "cp1251";
    protected String name = null;
    protected String shortName;
    protected String fullName;
    protected int type = Types.BOOK;
    protected static final char FSSEP = FileSystemStorage.getInstance().getFileSystemSeparator();

    public String getShortName() {
        return shortName;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public String getFullName() {
        return fullName;
    }

    public abstract String entryFileName(String entryName);

    //main function of this class 
    public abstract String getText(Reference reference);
}

