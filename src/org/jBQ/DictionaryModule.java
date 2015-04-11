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

import java.io.InputStream;
import org.albite.io.decoders.AlbiteStreamReader;
import java.util.Vector;
import java.util.Hashtable;
import com.sun.lwuit.Dialog;

// module, which represents Dictionary

public class DictionaryModule extends Module {

    private int prefixLength = 0; //we use Hashtable, containing lists, where prefix of words used as a key for such words.
                                  //Size of prefix determinig by number of words in dictionary, which we know, so here we
                                  //replace Trie with more simple structure
    private Hashtable dictionary;
    private String articleFileName;
    private AlbiteStreamReader idxStreamEncoded;
    private String pathToIDX;

    public DictionaryModule(String pathToIDX) {
        //show message at LoadingScreen
        LoadingScreen.print(Settings.tr("loadModule"));
        LoadingScreen.print(pathToIDX);        
        type = Types.DICTIONARY;
        this.pathToIDX = pathToIDX;
        //shortName is name of file
        name = pathToIDX.substring(pathToIDX.lastIndexOf(FSSEP) + 1, pathToIDX.length() - 4).toLowerCase();
        shortName = name;
        articleFileName = Util.GetFileNameInProperCase(pathToIDX.substring(0, pathToIDX.length() - 4) + ".htm");
        reload();
    }
    
    public void reload() {
        dictionary = null;
        encoding = Settings.getDictionariesEncoding();
        //open stream for .idx file
        try {
        	InputStream idxStream = Util.getResourceAsStream(pathToIDX);
        	idxStreamEncoded = new AlbiteStreamReader(idxStream, encoding);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //read name from idx
        fullName = Util.readLine(idxStreamEncoded);
    }

    private void readIndex() {
        //read entire .idx to memory and calculate number of lines
        Vector lines = new Vector();
        String line = Util.readLine(idxStreamEncoded);
        while (line != null) {
            if (!line.equals(""))
                lines.addElement(line);
            line = Util.readLine(idxStreamEncoded);
        }
        lines.addElement("0"); //place last offset to simplify algorithm of parsing lines
        lines.addElement("0");
        //calculate prefix length to make about 100 entries in one Vector to minimize enumeration and save memory
        double n = lines.size() / 1000.0;
        while (n >= 1.0) {
            n = n / 3.0;
            prefixLength++;
        }
        //create dictionary index in memory
        dictionary = new Hashtable();
        for (int i = 0; i < lines.size() - 2; i += 2) {
            try {
                placeWord((String) lines.elementAt(i), (String) lines.elementAt(i + 1), (String) lines.elementAt(i + 3));
            } catch (Throwable exception) {
                Dialog.show("Error", "Can't read dictionary. Possibly due to incorrect dictionary encoding settigns.", "OK", null);
                break;
            }
        }
    }

    //place information about word in index, so we can quickly find by word prefix all words which use this prefix
    //and by word, where in ArticleFile located article for this word
    private void placeWord(String word, String beginOffsetString, String endOffsetString) {
        word = Util.toLowerCase(word); //case insensitive dictionary
        Record record = new Record(word, Integer.parseInt(beginOffsetString.trim()), Integer.parseInt(endOffsetString.trim()));
        String prefix = word;
        if (record.word.length() > prefixLength)
            prefix = record.word.substring(0, prefixLength);
        if (dictionary.containsKey(prefix))
            ((Vector) dictionary.get(prefix)).addElement(record);
        else {
            //create Vector and add our word
            Vector prefixWords = new Vector();
            prefixWords.addElement(record);
            dictionary.put(prefix, prefixWords);
        }
    }

    //get words with this prefix from Hashtable
    private Vector getPrefixWords(String word) {
        String prefix = word;
        if (word.length() > prefixLength)
            prefix = word.substring(0, prefixLength);
        return (Vector) dictionary.get(prefix);
    }

    private Record recordByWord(String word) {
        word = Util.toLowerCase(word); //case insensitive dictionary
        Vector words = getPrefixWords(word);
        if (words == null)
            return null;
        for (int i = 0; i < words.size(); i++)
            if (((Record) words.elementAt(i)).word.equals(word))
                return (Record) words.elementAt(i);
        return null;
    }

    private String textByRecord(Record record) {
        //open stream for file with articles text
    	AlbiteStreamReader articleStreamEncoded = null;
        try {
        	InputStream articleStream = Util.getResourceAsStream(articleFileName);
        	articleStreamEncoded = new AlbiteStreamReader(articleStream, encoding);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //locate article
        try {
            articleStreamEncoded.skip(record.beginOffset);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //read article
        char[] articleText = new char[record.endOffset - record.beginOffset];
        try {
            articleStreamEncoded.read(articleText);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        return new String(articleText);
    }

    public String getText(Reference reference) {
        if (dictionary == null)
            readIndex();
        Record record = recordByWord(reference.getEntry());
        if (record != null)
            return textByRecord(record);
        return null;
    }

    public String entryFileName(String entryName) {
        return articleFileName;
    }

    //get words, which begins with this prefix, also makes text comparsion, because user can type more text, than our
    //prefix length in index storing structure
    public Vector getPossibilities(String word) {
        if (dictionary == null)
            readIndex();
        Vector returnValue = new Vector();
        word = Util.toLowerCase(word); //case insensitive dictionary
        Vector words = getPrefixWords(word);
        //find words with this text
        if (words != null)
            for (int i = 0; i < words.size(); i++) {
                String possibleWord = ((Record) words.elementAt(i)).word;
                if (possibleWord.indexOf(word) != -1)
                    returnValue.addElement(possibleWord);
            }
        return returnValue;
    }

    //used for represent one word in index of all dictionary words
    private class Record {

        public int beginOffset;
        public int endOffset;
        public String word;

        public Record(String word, int beginOffset, int endOffset) {
            this.word = word;
            this.beginOffset = beginOffset;
            this.endOffset = endOffset;
        }
    }
}

