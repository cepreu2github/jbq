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

import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.Form;
import com.sun.lwuit.Command;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Label;
import com.sun.lwuit.Image;
import com.sun.lwuit.layouts.*;

//class for providing base Dialog services to keep similar code in one place
public abstract class BaseDialog implements ActionListener {

    protected Form form;
    protected static String backIconPath = "/icons/edit-undo.png";
    protected static String saveIconPath = "/icons/document-save.png";
    protected static String searchIconPath = "/icons/system-search.png";
    
    public BaseDialog(String title) {
        form = new Form(title);
        form.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
    }

    //below some methods to quickly create standard GUI components and place them at form
    protected Command createCommand(String name, String iconPath) {
        Command command = null;
        try{
        	command = new Command(name, Image.createImage(iconPath));
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        form.addCommand(command);
        return command;
    }

    protected CheckBox createCheckBox(String text) {
        CheckBox result = new CheckBox(text);
        form.addComponent(result);
        return result;
    }

    protected Label createLabel(String text) {
        Label label = new Label(text);
        form.addComponent(label);
        return label;
    }
    
    protected ComboBox createComboBox() {
        ComboBox result = new ComboBox();
        form.addComponent(result);
        return result;
    }

    public void actionPerformed(ActionEvent event) {
    }
}
