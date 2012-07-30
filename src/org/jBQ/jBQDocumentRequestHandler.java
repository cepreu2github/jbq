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

import com.sun.lwuit.html.DefaultDocumentRequestHandler;
import com.sun.lwuit.html.DocumentInfo;
import java.io.ByteArrayInputStream;

//class for parsing and opening custom urls "jBQ:///*", which used to represent texts of modules in program
public class jBQDocumentRequestHandler extends DefaultDocumentRequestHandler {

    private static Reference referenceToOpen;

    public static void setReference(Reference reference) {
        referenceToOpen = reference;
    }

    public void resourceRequestedAsync(DocumentInfo docInfo, final IOCallback callback) {
        docInfo.setEncoding("utf8");
        String url = docInfo.getUrl();
        //if url contains anchors, trim they
        int hash = url.indexOf('#');
        if (hash != -1)
            url = url.substring(0, hash);
        String text = null;
        if (referenceToOpen != null) {
            text = Modules.getText(referenceToOpen);
            if (text == null)
                text = Settings.tr("textNotFoundInModule");
        } else if (url.indexOf(".htm") != -1)
            try {
                text = Util.streamToString(Util.getResourceAsStream(Util.GetFileNameInProperCase(url)));
            } catch (Throwable exception) {
                Util.showException(exception);
            }
        else
            //for any other type of urls we use just return InputStream
            try {
                callback.streamReady(Util.getResourceAsStream(Util.GetFileNameInProperCase(url)), docInfo);
                return;
            } catch (Throwable exception) {
                Util.showException(exception);
            }
        //default behavior for module texts and htm-files
        text = "<html> <head> </head> <body>" + text + "</body> <html>";
        try {
            callback.streamReady(new ByteArrayInputStream(text.getBytes("UTF-8")), docInfo);
            return;
            //ByteArrayInputStream can throw encoding exception, but we always know, that our encoding is UTF-8, so catch does nothing
        } catch (Throwable exception) {
        }
        return;
    }
}
