#!/usr/bin/python
# -*- coding: utf-8 -*-

from HTMLParser import HTMLParser, HTMLParseError

class StripHTMLParser(HTMLParser):
    result = ''
    isScriprefPassage = False
    isScripref = False

    def handle_starttag(self, tag, attrs):
        dattrs = dict(attrs)
        if tag == "scripref" and dattrs.has_key("passage"):
            self.result += '<a href="' + dattrs["passage"] + '">' + dattrs["passage"] +'</a>'
            self.isScriprefPassage = True
        elif tag == "scripref" :
            self.isScripref = True
            self.result += ' '
        elif tag == "br":
            self.result += '<' + tag + '>'
    
    def handle_startendtag(self, tag, attrs):
        pass
    
    def handle_endtag(self, tag):
        self.result += ' '
        self.isScriprefPassage = False
        self.isScripref = False
    
    def handle_data(self, data):
        if self.isScripref:
            refs = data.split(";")
            result = ''
            book = refs[0].split(' ')[0]
            for ref in refs:
                ref = ref.strip()
                if ' ' in ref:
                    book = ref.split(' ')[0]
                else:
                    ref = book + ' ' + ref
                result += '<a href="' + ref + '">' + ref +'</a>; '
            self.result += result
        elif self.isScriprefPassage:
            pass
        else:
            self.result += data

def strip_some_tags(html):
    parser = StripHTMLParser()
    parser.feed(html)
    result = parser.result
    parser.close()
    return result

inputFile = file('tsk.xml', 'r')
xml = inputFile.read()
books = xml.split('<div type="book" osisID="')
books = books[1:]
for book in books:
    bookName = book[0:10].split('"')[0]
    bookFile = file(bookName + ".html", 'w')
    print "*********************** CONVERTING BOOK:", bookName
    chapters = book.split('<chapter osisID="')
    chapters = chapters[1:]
    chapterNumber = 1
    for chapter in chapters:
        bookFile.write("<h1> Chapter " + str(chapterNumber) + "</h1>\n")
        verses = chapter.split('<verse osisID="')
        verses = verses[1:]
        verseCounter = 1
        for verse in verses:
            verse = verse.split('>',1)[1]
            verse = verse.replace("<br />", "<br>\n")
            verse = strip_some_tags(verse)
            verse = "\n<p>" + str(verseCounter) + " " + verse + "</p>"
            bookFile.write(verse)
            verseCounter += 1
        chapterNumber += 1
