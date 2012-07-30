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

//class, which used to react, when come events occured in HTMLComponent, which used in TextView
public class jBQHTMLCallback extends DefaultHTMLCallback {

    //if user clicks our custom type of hyperlink, we must display reference of opened text in title of form
    public boolean linkClicked(HTMLComponent htmlC, java.lang.String url) {
        if (url.startsWith("jBQ:///")) {
            Reference reference = new Reference(url);
            TextView.show(reference);
            return false;
        } else if (url.indexOf(TextModule.anchorPrefix) != -1)
            TextView.updateReference(Integer.parseInt(Util.splitString(url, TextModule.anchorPrefix, 2)[1]));
        return super.linkClicked(htmlC, url);
    }
}
