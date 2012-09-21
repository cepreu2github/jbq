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
import com.sun.lwuit.Display;
import java.io.InputStream;
import org.albite.io.decoders.AlbiteStreamReader;
import java.util.Vector;
import com.sun.lwuit.Dialog;
import java13.lang.Character;

//class with useful functions, which can be used everywhere from program, but not so big to have special class with this function
public class Util {

    private static final char FSSEP = FileSystemStorage.getInstance().getFileSystemSeparator();

    //try to find correct case for FileName in case sensitive filesystem, by comparing FileName in lower case with list of files in directory
    //in low case
    public static String GetFileNameInProperCase(String InFileName) {
        if (InFileName.startsWith("jar://"))
            return InFileName.toLowerCase();
        else {
			try {
				if (FileSystemStorage.getInstance().exists(InFileName))
					return InFileName;
			} catch (Throwable ex) {
            }
            //find actual file name
            String Directory = InFileName.substring(0, InFileName.lastIndexOf(FSSEP));
            String FileNameInLowCase = InFileName.substring(InFileName.lastIndexOf(FSSEP) + 1, InFileName.length()).toLowerCase();
            //get list of files in directory, where this file must be found
            String[] FilesList = null;
            try {
                FilesList = FileSystemStorage.getInstance().listFiles(Directory);
            } catch (Throwable ex) {
                return InFileName;
            }
            //compare file name in lower case with every file name at directory in lower case
            for (int i = 0; i < FilesList.length; i++)
                if (FilesList[i].toLowerCase().equals(FileNameInLowCase))
                    return Directory + FileSystemStorage.getInstance().getFileSystemSeparator() + FilesList[i];
            return InFileName;
        }
    }

    //abstraction for open file with JSR-75 or standard java API (if file located in jar-file)
    public static InputStream getResourceAsStream(String filePath) throws IncorrectFilePathException, Throwable {
        //its for files located at jar-file
        if (filePath.startsWith("jar://")) {
            //try given filename, lower case and upperscase. Because jar-filesystem is case-sensitive, but does not provide any chance to list directory
            InputStream returnValue = Display.getInstance().getResourceAsStream(Util.instance().getClass(), filePath.substring(6));
            if (returnValue == null)
                Dialog.show("error", "Can't open file " + filePath + ". Maybe it is problem with case of file name, because opening in .jar is case sensitive.", "ok", null);
            return returnValue;
            //its for JSR-75
        } else if (filePath.startsWith("file://"))
            try {
                return FileSystemStorage.getInstance().openInputStream(filePath);
            } catch (Throwable exception) {
                throw exception;
            }
        else
            throw new IncorrectFilePathException("Incorrect protocol specified");
    }

    //read one line from input stream
    public static String readLine(AlbiteStreamReader reader) {
        //test whether the end of file has been reached. If so, return null.
        int readChar = reader.read();
        if (readChar == -1)
            return null;
        StringBuffer string = new StringBuffer("");
        //read until end of file or new line
        while (readChar != -1 && readChar != '\n') {
            //append the read character to the string. Some operating systems
            //such as Microsoft Windows prepend newline character ('\n') with
            //carriage return ('\r'). This is part of the newline character
            //and therefore an exception that should not be appended to the
            //string.
            if (readChar != '\r')
                string.append((char) readChar);
            //read the next character
            readChar = reader.read();
        }
        return string.toString();
    }

    //java.lang.String.split(...) replacement. I already hate J2ME. Such simple things does not exist...
    //if n = -1, string will be splitted according to the number of separator occurences
    public static String[] splitString(String original, String separator, int n) {
        Vector nodes = new Vector();
        //parse nodes into vector
        int index = original.indexOf(separator);
        n--;
        while (index >= 0 && n != 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
            n--;
        }
        //get the last node
        nodes.addElement(original);
        //create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0)
            for (int i = 0; i < nodes.size(); i++)
                result[i] = (String) nodes.elementAt(i);
        return result;
    }

    //print information about exception
    public static void showException(Throwable exception) {
        exception.printStackTrace();
        Dialog.show("Exception", exception.getMessage(), "OK", null);
        Display.getInstance().exitApplication();
    }

    public static boolean characterIsDivisor(char character) {
        return !(Character.isLetter(character) || Character.isDigit(character));
    }

    //return array of tags and words inside this tags
    public static String[] splitToTagsAndWords(String text) {
        Vector sequence = new Vector();
        String currentElement = "";
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == '<') {
                if (!currentElement.equals(""))
                    sequence.addElement(currentElement);
                currentElement = "<";
            } else if (characterIsDivisor(text.charAt(i)) && !currentElement.equals("") && currentElement.charAt(0) != '<') {
                if (!currentElement.equals(""))
                    sequence.addElement(currentElement);
                currentElement = "";
                currentElement += text.charAt(i);
            } else if (text.charAt(i) == '>') {
                currentElement += '>';
                if (!currentElement.equals(""))
                    sequence.addElement(currentElement);
                currentElement = "";
            } else
                currentElement += text.charAt(i);
        if (!currentElement.equals(""))
            sequence.addElement(currentElement);
        String[] result = new String[sequence.size()];
        for (int i = 0; i < sequence.size(); i++)
            result[i] = (String) sequence.elementAt(i);
        return result;
    }

    public static String streamToString(InputStream inputStream) {
        AlbiteStreamReader stream = new AlbiteStreamReader(inputStream, "utf-8");
        StringBuffer strBuf = new StringBuffer();
        try {
            int value = stream.read();
            while (value != -1) {
                strBuf.append((char) value);
                value = stream.read();
            }
        } catch (Throwable exception) {
            showException(exception);
        }
        return strBuf.toString();
    }

    //analogue of String.toLowerCase but with support with any locale represented
    //in unicode encoding. Support is come by porting some part of Character class
    //from GNU Classpath project version 0.93
    public static String toLowerCase(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            result.append(Character.toLowerCase(str.charAt(i)));
        }            
        return result.toString();
    }

	public static void show(String message) {
		Dialog.show("info", message, "OK", null);
	}
	
    public static Util instance() {
        return instance;
    }
    private static Util instance = new Util();
}

