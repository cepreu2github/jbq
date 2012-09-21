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

import com.sun.lwuit.Label;
import com.sun.lwuit.Container;
import com.sun.lwuit.layouts.*;

public class LoadingScreen extends BaseDialog {

    private Container log;
    private Label endLabel;

	public LoadingScreen() {
	    super(Settings.tr("loading"));
	    form.setScrollable(false);
	    log = new Container(new BoxLayout(BoxLayout.Y_AXIS));
	    log.setScrollable(true);
	    log.setSmoothScrolling(false);
	    form.addComponent(log);
	}

    public static void show(String caption) {
        instance().log.removeAll();
        //due to bug at LWUIT sometimes bounds of new Label does not have time to calculate ant then scrolling does not work
        //to workaround this we create this endLabel. It creating at begin at his bound always have enough time to calculate.
        instance().endLabel = new Label("_");
        instance().log.addComponent(instance().endLabel);
        instance().form.setTitle(caption);
        instance().form.show();
    }

    public static void print(String message) {
        Label l = new Label(message);
        instance().log.addComponent(instance().log.getComponentCount() - 1, l);
        instance().form.show();
        instance().form.scrollComponentToVisible(instance().endLabel);
    }

    private static LoadingScreen instance() {
        return instance;
    }
    private static LoadingScreen instance = new LoadingScreen();

}
