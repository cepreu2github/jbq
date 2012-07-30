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

import javax.microedition.lcdui.Canvas;
import java.io.*;
import javax.microedition.rms.*;

public class Platform {

    public static final int keyPageDownCode = Canvas.KEY_NUM8;
    public static final int keyPageUpCode = Canvas.KEY_NUM2;

    //get system locale name
    public static String getSystemLocale() {
        return Util.splitString(System.getProperty("microedition.locale"), "-", 2)[0];
    }

    //get some record from RMS by his String key
    public static String getSettingsKey(String section, String key) {
        try {
            RecordStore store = RecordStore.openRecordStore(section, true);
            //find record and get data
            int recordId = rmsIdByKey(store, key);
            if (recordId != -1) {
                byte[] record = store.getRecord(recordId);
                //unpack data
                ByteArrayInputStream bais = new ByteArrayInputStream(record);
                DataInputStream dis = new DataInputStream(bais);
                String possibleKey = dis.readUTF();
                return dis.readUTF();
            }
            store.closeRecordStore();
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        return null;
    }

    //get record id by String key
    private static int rmsIdByKey(RecordStore store, String key) {
        try {
            for (RecordEnumeration re = store.enumerateRecords(null, null, false); re.hasNextElement();) {
                int returnId = re.nextRecordId();
                byte[] record = store.getRecord(returnId);
                //unpack data
                ByteArrayInputStream bais = new ByteArrayInputStream(record);
                DataInputStream dis = new DataInputStream(bais);
                String possibleKey = dis.readUTF();
                if (possibleKey.equals(key))
                    return returnId;
            }
        } catch (Throwable exception) {
            Util.showException(exception);
        }
        return -1;
    }

    //add some record to RMS with String key
    public static void addSettingsKey(String section, String key, String data) {
        try {
            RecordStore store = RecordStore.openRecordStore(section, true);
            //pack data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(key);
            dos.writeUTF(data);
            byte[] record = baos.toByteArray();
            //write new or update old record
            int oldRecordId = rmsIdByKey(store, key);
            if (oldRecordId != -1)
                store.setRecord(oldRecordId, record, 0, record.length);
            else
                store.addRecord(record, 0, record.length);
            store.closeRecordStore();
        } catch (Throwable exception) {
            Util.showException(exception);
        }
    }
}
