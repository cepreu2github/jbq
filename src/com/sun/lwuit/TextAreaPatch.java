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
 
package com.sun.lwuit;

public class TextAreaPatch extends TextArea {

    public TextAreaPatch() {
        super();
    }
    
    public TextAreaPatch(int rows, int columns) {
        super(rows, columns);
    }
    
    public TextAreaPatch(int rows, int columns, int constraint) {
        super(rows, columns, constraint);
    }
    
    public TextAreaPatch(java.lang.String text) {
        super(text);
    }
    
    public TextAreaPatch(java.lang.String text, int maxSize) {
        super(text, maxSize);
    }
    
    public TextAreaPatch(java.lang.String text, int rows, int columns) {
        super(text, rows, columns);
    }
    
    public TextAreaPatch(java.lang.String text, int rows, int columns, int constraint) {
        super(text, rows, columns, constraint);
    }
    
    public void pointerReleased(int x, int y) {
        // prevent a drag operation from going into edit mode
        if (isDragActivated()) {
            super.pointerReleased(x, y);
        } else {
            if (isEditable() && isEnabled() && !isCellRenderer()) {
                Display.getInstance().editString(this, getMaxSize(), getConstraint(), getText());
            }
        }
    }
}
