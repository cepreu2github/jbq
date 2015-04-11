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

//class for J2ME platform to access specific functions of this platform
package org.jBQ;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Locale;

public class Platform {

	public static SharedPreferences sharedPreferences;
	public static final char FSSEP = '/';
	
    //get system locale name
    public static String getSystemLocale() {
    	return Locale.getDefault().getLanguage();
    }

    //get some record from RMS by his String key
    public static String getSettingsKey(String key) {
    	return sharedPreferences.getString(key, null);
    }

    //add some record to RMS with String key
    public static void addSettingsKey(String key, String data) {
    	Editor editor = sharedPreferences.edit().putString(key, data);
    	editor.commit();
    }
}
