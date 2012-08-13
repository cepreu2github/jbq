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

import javax.microedition.midlet.*;

import com.sun.lwuit.Display;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;

public class jBQ extends MIDlet {

    public void destroyApp(boolean d) {
        notifyDestroyed();
    }

    public void pauseApp() {
    }

    public void startApp() {
        Display.getInstance().setDefaultVirtualKeyboard(null);
        Display.init(this);
        //load theme
        try {
            //set the theme
            if (Display.getInstance().hasNativeTheme())
                Display.getInstance().installNativeTheme();
            else {
                Resources theme = Resources.open("/TipsterTheme.res");
                UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
            }
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        //load translation
        UIManager.getInstance().setResourceBundle(Settings.getCurrentLocale());
        //get last history entry
        Reference r = null;
        if (History.size() != 0)
            r = History.lastElement();
        //if history is empty, try to locate any module and open it
        if (r == null) {
            //if no modules, open help article about installing modules
            String firstModuleName = null;
            try {
                firstModuleName = (String) Modules.names(Module.Types.BIBLE).firstElement();
            } catch (Throwable exception) {
				Util.showException(exception);
            }
            if (firstModuleName != null) {
                Module firstModule = Modules.getByName(firstModuleName);
                r = new Reference(firstModuleName, ((TextModule) firstModule).firstElementName(), 1, 1);
            } else {
                TextView.showHelp();
                return;
            }
        }
        //show TextView
        TextView.show(r);
    }
}
