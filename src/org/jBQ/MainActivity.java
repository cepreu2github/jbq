package org.jBQ;

import javax.microedition.midlet.MIDletStateChangeException;

import com.sun.lwuit.Display;
import com.sun.lwuit.io.Storage;
import com.sun.lwuit.impl.android.LWUITActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends LWUITActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Platform.sharedPreferences = getPreferences(0);
        Display.init(this);
        Storage.init(this);
        
        Display.getInstance().callSerially(new Runnable(){

            @Override
            public void run() {
            	jBQ midlet = new jBQ();
            	midlet.startApp();
            }
        });
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}	
	
}
