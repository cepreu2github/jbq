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

public class TextModule extends Module {

    public static final String anchorPrefix = "#jBQ_verse_";
    //some properties of BibleQuote module
    private String chapterSign;
    private String verseSign;
    private int firstChapterNumber = 1;
    private boolean hasStrongs = false;
    private Vector books;
    private final String[] hebrewBooks = {"Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "Samuel", "2Samuel", "Kings", "2Kings", "Chron", "2Chron", "Ezra", "Nehemiah", "Esther", "Job", "Psalm", "Proverbs", "Ecclesia", "Song", "Isaiah", "Jeremiah", "Lament", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi"};
    private final String[] greekBooks = {"Matthew", "Mark", "Luke", "John", "Acts", "James", "1Peter", "2Peter", "1John", "2John", "3John", "Jude", "Romans", "1Corinthians", "2Corinthians", "Galatians", "Ephesian", "Philippians", "Colossians", "1Thessalonians", "2Thessalonians", "1Timothy", "2Timothy", "Titus", "Philemon", "Hebrews", "Revelation"};
    private final String hebrewStrongsDictionary = "hebrew";
    private final String greekStrongsDictionary = "greek";
    private final int bufferSize = 350000;

    private class Testaments {

        public static final int OLD = 0;
        public static final int NEW = 1;
        public static final int UNKNOWN = 2;
    }

    public TextModule(String pathToINI) {
        //show message at LoadingScreen
        LoadingScreen.print(Settings.tr("loadModule"));
        LoadingScreen.print(pathToINI);
        //determine Commentaries in path, because it is sign of commentary module
        char Separator = FSSEP;
        if (pathToINI.startsWith("jar"))
            Separator = '/';
        String Directory = pathToINI.substring(0, pathToINI.lastIndexOf(Separator));
        name = Directory.substring(Directory.lastIndexOf(Separator) + 1, Directory.length()).toLowerCase();
        Directory = Directory.substring(0, Directory.lastIndexOf(Separator));
        Directory = Directory.substring(Directory.lastIndexOf(Separator) + 1, Directory.length());
        if (Directory.toLowerCase().equals("commentaries"))
            type = Types.COMMENTARY;
        //read INI settings
        if (!readINISettings(pathToINI))
            //maybe we tried read with incorrect encoding, so we try with another
            readINISettings(pathToINI);
    }

    public String firstElementName() {
        return ((Book) books.firstElement()).fullName;
    }

    public int chaptersCountInBook(String name) {
        Book book = bookByName(name);
        if (book == null)
            return 0;
        return book.chapterQuantity;
    }

    public String entryFileName(String bookName) {
        Book book = bookByName(bookName);
        if (book == null)
            return null;
        return book.file;
    }

    private Book bookByName(String name) {
        for (int i = 0; i < books.size(); i++)
            if (((Book) books.elementAt(i)).fullName.equals(name))
                return ((Book) books.elementAt(i));
        return null;
    }

    public String[] getBookShortNames(String name) {
        Book book = bookByName(name);
        if (book == null)
            return null;
        return book.shortNames;
    }

    //return Vector with names of books in this module
    public Vector bookNames() {
        Vector result = new Vector();
        for (int i = 0; i < books.size(); i++)
            result.addElement(((Book) books.elementAt(i)).fullName);
        return result;
    }

    private AlbiteStreamReader openBookFile(Book book) {
        InputStream bookStream = null;
        try {
            bookStream = Util.getResourceAsStream(Util.GetFileNameInProperCase(book.file));
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        return new AlbiteStreamReader(bookStream, encoding);
    }

    // read one chapter from file and return it as text
    private String readChapter(Book book, int chapterNumber) {
        //create index if necessary
        if (book.chapterOffsets == null)
            findChapterOffsets(book);
        if (chapterNumber > book.chapterQuantity - 1)
            return null;
        //open stream for file with text
        AlbiteStreamReader bookStreamEncoded = openBookFile(book);
        //locate chapter
        try {
            bookStreamEncoded.skip(book.chapterOffsets[chapterNumber]);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //read chapter
        char[] chapterText = new char[book.chapterOffsets[chapterNumber + 1] - book.chapterOffsets[chapterNumber] - chapterSign.length()];
        try {
            bookStreamEncoded.read(chapterText);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        return new String(chapterText);
    }

    // find chapter offsets to speed up opening of needed chapter
    private void findChapterOffsets(Book book) {
        //open file
        AlbiteStreamReader bookStreamEncoded = openBookFile(book);
        //preparations to enter main cycle of method
        book.chapterOffsets = new int[book.chapterQuantity + 1];
        char[] buffer = new char[bufferSize];
        String blockOfText = "";
        for (int i = 1; i < chapterSign.length() ; i++)
            blockOfText += " ";
        int amountOfReaded = -1;
        //read first block
        try {
            amountOfReaded = bookStreamEncoded.read(buffer);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //while file not ended
        int filePosition = 0;
        int currentChapter = 0;
        while (amountOfReaded > 0) {
            blockOfText += new String(buffer);
            int chapterSignPositon = blockOfText.indexOf(chapterSign);
            //for every chapterSign in text
            while (chapterSignPositon != -1) {
                if (currentChapter >= book.chapterQuantity)
                    break;
                //add chapter to chapterOffsets
                book.chapterOffsets[currentChapter] = filePosition + chapterSignPositon + 1;
                currentChapter++;
                chapterSignPositon = blockOfText.indexOf(chapterSign, chapterSignPositon + chapterSign.length());
            }
            //read next block
            filePosition += amountOfReaded;
            blockOfText = blockOfText.substring(blockOfText.length() - chapterSign.length() + 1);
            try {
                amountOfReaded = bookStreamEncoded.read(buffer);
            } catch (Throwable exception) {
                Util.showException(exception);
            }
        }
        book.chapterOffsets[currentChapter] = filePosition;
        if (currentChapter < book.chapterQuantity)
            book.chapterQuantity = currentChapter;
    }

    //main function of this class
    public String getText(Reference reference) {
        //find appropriate book
        Book book = bookByName(reference.getEntry());
        if (book == null)
            return null;
        //determine is hebrew or greek for Strong numbers
        int testament = Testaments.UNKNOWN;
        for (int i = 0; i < hebrewBooks.length; i++)
            for (int j = 0; j < book.shortNames.length; j++)
                if (hebrewBooks[i].equals(book.shortNames[j])) {
                    testament = Testaments.OLD;
                    break;
                }
        if (testament == Testaments.UNKNOWN)
            for (int i = 0; i < greekBooks.length; i++)
                for (int j = 0; j < book.shortNames.length; j++)
                    if (greekBooks[i].equals(book.shortNames[j])) {
                        testament = Testaments.NEW;
                        break;
                    }
        //open book file for reading
        AlbiteStreamReader bookStreamEncoded = openBookFile(book);
        //read chapter to String
        String chapterText = readChapter(book, reference.getChapter() - firstChapterNumber);
        if (chapterText == null)
            return null;
        chapterText = makeSpecialFormatting(chapterText, testament);
        //determine possibility to go at next chapter
        if (chaptersCountInBook(reference.getEntry()) + (getFirstChapterNumber() - 1) > reference.getChapter())
            chapterText += "<BR> <P ALIGN=\"center\"> <B> ________ ________ ________ </B> </P> <A HREF=\"jBQ:///" + name + "/" + reference.getEntry() + "/" + Integer.toString(reference.getChapter() + 1) + TextModule.anchorPrefix + "1\">" + Settings.tr("toNextChapter") + " </A>"; 
        return chapterText;
    }

    //search for some words in module
    //experimental more lowlevel search, created with hope to speedup search process
    //but java itself to slow, so we almost have not any profit from use of such functions
    //another approach used in old search, where we believe, that java.lang.Sring.indexOf it fast function and use it
    //as frequently as it possible
    public String newsearch(String searchText, boolean isCaseSensitive, int from, int to) {
        //swap first book with end book if needed
        if(from > to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        //lowercase search text
        if (!isCaseSensitive)
            searchText = Util.toLowerCase(searchText);
        //store all useful data as char[] to speedup access
        StringBuffer result = new StringBuffer();
        String[] searchWordsString = Util.splitString(searchText, " ", -1);
        char[][] searchWords = new char[searchWordsString.length][];
        for(int i = 0; i < searchWords.length; i++)
            searchWords[i] = searchWordsString[i].toCharArray();
        char[] verseSign = this.verseSign.toCharArray();
        char[] chapterSign = this.chapterSign.toCharArray();
        //for every book in module
        char[] text = new char[bufferSize];
        LoadingScreen.print(Settings.tr("lookingAt"));
        for (int bookn = from; bookn <= to; bookn++) {
            //open book file for reading
            Book book = (Book) books.elementAt(bookn);
            LoadingScreen.print(book.fullName);
            AlbiteStreamReader bookStreamEncoded = openBookFile(book);
            //reset counters
            int wasReaded = bufferSize;
            int wasRemains = 0;
            int latestVersePosition = -1;
            int chapterNumber = -1;
            int verseNumber = -1;
            //read file until the end
            while(wasReaded > 0) {
                wasReaded = bookStreamEncoded.read(text, wasRemains, bufferSize - wasRemains);
                //System.out.println(wasReaded);
                //System.out.println(wasRemains + wasReaded);
                int actualBufferSize = wasRemains + wasReaded;
                for(int pos = 0; pos < actualBufferSize; pos++) {
                    //check for verse sign
                    if (chapterNumber >= 0) {
                        boolean verseSignFound = true;
                        for (int vpos = 0; vpos < verseSign.length; vpos++)
                            if (!(pos + vpos < actualBufferSize && text[pos + vpos] == verseSign[vpos])) {
                                verseSignFound = false;
                                break;
                            }
                        if (verseSignFound) {
                            //try to find in verse
                            boolean allWordsFound = true;
                            for (int wordn = 0; wordn < searchWords.length; wordn++) {
                                boolean wordFoundInVerse = false;
                                for (int vpos = latestVersePosition; vpos < pos; vpos++) {
                                    boolean wordFound = true;
                                    for (int wpos = 0; wpos < searchWords[wordn].length; wpos++)
                                         if (!(vpos + wpos < pos && text[vpos + wpos] == searchWords[wordn][wpos])) {
                                            wordFound = false;
                                            break;
                                         }
                                    if (wordFound) {
                                        wordFoundInVerse = true;
                                        break;
                                    }
                                }
                                if (!wordFoundInVerse) {
                                    allWordsFound = false;
                                    break;
                                }
                            }
                            if (allWordsFound) {
                                String verse = new String(text, latestVersePosition, pos - latestVersePosition);
                                System.out.println(verse);
                            }
                            latestVersePosition = pos + verseSign.length;
                            pos += verseSign.length;
                            verseNumber++;
                        }
                    }
                    //check for chapter sign
                    boolean chapterSignFound = true;
                    for (int cpos = 0; cpos < chapterSign.length; cpos++)
                        if (!(pos + cpos < actualBufferSize && text[pos + cpos] == chapterSign[cpos])) {
                            chapterSignFound = false;
                            break;
                        }
                    if (chapterSignFound) {
                        latestVersePosition = pos + chapterSign.length;
                        verseNumber = -1;
                        pos += chapterSign.length;
                        chapterNumber++;
                    }
                }
                if (latestVersePosition < 0)
                    wasRemains = 0;
                else {
                    wasRemains = actualBufferSize - latestVersePosition;
                    latestVersePosition = 0;
                    for (int i = 0; i < wasRemains; i++){
                        text[i] = text[actualBufferSize - wasRemains + i];
                        //if(wasReaded == 0)
                            //System.out.print(text[i]);
                    }
                }
            }
        }
        return "";
    }

    //search for some words in module
    public String search(String text, boolean isCaseSensitive, int from, int to) {
        if(from > to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        if (!isCaseSensitive)
            text = Util.toLowerCase(text);
        StringBuffer result = new StringBuffer();
        String[] searchWords = Util.splitString(text, " ", -1);
        //find biggest word of search query to speedup search later
        String biggestWord = "";
        for (int i = 0; i < searchWords.length; i++)
            if (searchWords[i].length() > biggestWord.length())
                biggestWord = searchWords[i];
        //maybe we must lowercase verse sign
        String appropriateCaseVerseSign;
        if (!isCaseSensitive)
            appropriateCaseVerseSign = Util.toLowerCase(verseSign);
        else
            appropriateCaseVerseSign = verseSign;
        // for every book in module
        LoadingScreen.print(Settings.tr("lookingAt"));
        for (int i = from; i <= to; i++) {
            // open book file for reading
            Book book = (Book) books.elementAt(i);
            //show message at LoadingScreen
            LoadingScreen.print(book.fullName);
            //create index if necessary
            if (book.chapterOffsets == null)
                findChapterOffsets(book);
            AlbiteStreamReader bookStreamEncoded = openBookFile(book);
            //chapter by chapter
            try {
                bookStreamEncoded.skip(book.chapterOffsets[0]);
            } catch (Throwable exception) {
                Util.showException(exception);
            }
            for (int chapterNumber = 0; chapterNumber < book.chapterQuantity; chapterNumber++) {
                //read chapter
                if (chapterNumber > book.chapterQuantity)
                    break;
                char[] chapterBuffer = new char[book.chapterOffsets[chapterNumber + 1] - book.chapterOffsets[chapterNumber] - chapterSign.length()];
                try {
                    bookStreamEncoded.read(chapterBuffer);
                    bookStreamEncoded.skip(chapterSign.length());
                } catch (Throwable exception) {
                   Util.showException(exception);
                }
                String chapterText = new String(chapterBuffer);
                //before verse by verse search, check that whole chapter contain biggest word of search query
                if (!isCaseSensitive)
                    chapterText = Util.toLowerCase(chapterText);
                boolean biggestWordFound = false;
                if (chapterText.indexOf(biggestWord) != -1)
                    biggestWordFound = true;
                //verse by verse
                if (biggestWordFound) {
                    String[] verses = Util.splitString(chapterText, appropriateCaseVerseSign, -1);
                    for (int j = 1; j < verses.length; j++) {
                        String lowerCaseVerse;
                        lowerCaseVerse = verses[j];
                        boolean verseContainWords = true;
                        // if verse contains all words, add it to result
                        for (int k = 0; k < searchWords.length; k++)
                            if (lowerCaseVerse.indexOf(searchWords[k]) == -1) {
                                verseContainWords = false;
                                break;
                            }
                        if (verseContainWords) {
                            int verseNumber = findVerseNumber(verses[j], j);                        
                            Reference newReference = new Reference(name, book.fullName, chapterNumber + firstChapterNumber, verseNumber);
                            result.append("<a href=\"").append(newReference.toString()).append("\">").append(newReference.toHumanReadableString()).append("</a> <br>").append(verses[j]).append("<br>");
                            if (result.length() > 65536) {
                                result.append(Settings.tr("tooManySearchResults"));
                                return result.toString();
                            }
                        }
                    }
                }
            }
        }
        return result.toString();
    }

    private int findVerseNumber(String verse, int supposedNumber) {
        String[] parts = Util.splitToTagsAndWords(verse);
        for (int j = 0; j < parts.length; j++)
            if (parts[j].charAt(0) != '<')
                try {
                    int result = Integer.parseInt(parts[j]);
                    return result;
                } catch (Throwable exception) {}
        return supposedNumber;
    }

    //makes verse numbers and strong numbers as hyperlinks
    private String makeSpecialFormatting(String chapter, int testament) {
        String[] verses = Util.splitString(chapter, verseSign, -1);
        for (int i = 1; i < verses.length; i++) {
            String[] parts = Util.splitToTagsAndWords(verses[i]);
            verses[i] = "";
            //try to find number. Verses using format [number][spacebar][verse text]
            boolean isVerseNumberNotParsed = true;
            for (int j = 0; j < parts.length; j++)
                //makes verse numbers as links with anchors, so we can adress verses individually with url
                if (isVerseNumberNotParsed && parts[j].charAt(0) != '<') {
                    isVerseNumberNotParsed = false;
                    try {
                        Integer.parseInt(parts[j]);
                        verses[i] += " <BIG> <A NAME=\"" + anchorPrefix.substring(1) + parts[j].trim() + "\" HREF=\"" + anchorPrefix + parts[j] + "\"> " + parts[j] + " </A> </BIG>";
                    } catch (Throwable exception) {
                        verses[i] += parts[j];
                    }
                    //makes strong numbers
                } else if (hasStrongs)
                    try {
                        Integer.parseInt(parts[j].trim());
                        if (Settings.isStrongsEnabled()) {
                            //check for dictionary module existing
                            String dictionaryName = null;
                            if (testament == Testaments.OLD)
                                dictionaryName = hebrewStrongsDictionary;
                            else if (testament == Testaments.NEW)
                                dictionaryName = greekStrongsDictionary;
                            if (dictionaryName != null && Modules.getByName(dictionaryName) != null) {
                                String number = parts[j].trim();
                                while (number.length() < 5)
                                    number = "0" + number;
                                //create hyperlink
                                verses[i] += "<a href=\"jBQ:///" + dictionaryName + "/" + number + "/1" + TextModule.anchorPrefix + "1\">" + parts[j] + "</a>";
                            } else
                                verses[i] += parts[j];
                        }
                    } catch (Throwable exception) {
                        verses[i] += parts[j];
                    }
                else
                    verses[i] += parts[j];
        }
        //collect verses in chapter
        String result = "";
        for (int i = 0; i < verses.length; i++)
            result = result + Settings.getVerseDivisor() + verses[i];
        return result;
    }

    public int getFirstChapterNumber() {
        return firstChapterNumber;
    }

    private boolean readINISettings(String PathToINI) {
		//open stream for INI file
        InputStream INIStream = null;
        try {
            INIStream = Util.getResourceAsStream(PathToINI);
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //read settings from INI
        AlbiteStreamReader INIStreamEncoded = new AlbiteStreamReader(INIStream, encoding);
        //parse general settings
        String line = Util.readLine(INIStreamEncoded);
        while (line != null) {
			if (line.startsWith("BibleName"))
                fullName = Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("BibleShortName"))
                shortName = Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("StrongNumbers"))
                if (Util.splitString(line, "=", 2)[1].trim().equals("Y"))
                    hasStrongs = true;
            if (line.startsWith("ChapterSign"))
                chapterSign = Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("VerseSign"))
                verseSign = Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("DefaultEncoding")) {
                String actualINIEncoding = Util.splitString(line, "=", 2)[1].trim();
                if (!encoding.equals(actualINIEncoding)) {
                    encoding = actualINIEncoding;
                    return false;
                }
            }
            if (line.startsWith("Bible") && type != Types.COMMENTARY)
                if (Util.splitString(line, "=", 2)[1].trim().equals("Y"))
                    type = Types.BIBLE;
            if (line.startsWith("ChapterZero"))
                if (Util.splitString(line, "=", 2)[1].trim().equals("Y"))
                    firstChapterNumber = 0;
            if (line.startsWith("BookQty"))
                break;
            line = Util.readLine(INIStreamEncoded);
        }
        //add books of module
        books = new Vector();
        String file = null, bookfullName = null;
        String[] shortNames = null;
        int chapterQuantity = 0;
        while (line != null) {
            if (line.startsWith("PathName"))
                file = PathToINI.substring(0, PathToINI.lastIndexOf(FSSEP) + 1) + Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("FullName"))
                bookfullName = Util.splitString(line, "=", 2)[1].trim();
            if (line.startsWith("ShortName"))
                shortNames = Util.splitString(Util.splitString(line, "=", 2)[1].trim(), " ", -1);
            if (line.startsWith("ChapterQty")) {
                chapterQuantity = Integer.parseInt(Util.splitString(line, "=", 2)[1].trim());
                books.addElement(new Book(file, bookfullName, shortNames, chapterQuantity));
            }
            line = Util.readLine(INIStreamEncoded);
        }
        return true;
}

    private class Book {

        private String file;
        private String fullName;
        private String[] shortNames;
        private int chapterQuantity;
        private int[] chapterOffsets = null;

        public Book(String file, String fullName, String[] shortNames, int chapterQuantity) {
            this.file = file;
            this.fullName = fullName;
            this.shortNames = shortNames;
            this.chapterQuantity = chapterQuantity;
        }
    }
}
