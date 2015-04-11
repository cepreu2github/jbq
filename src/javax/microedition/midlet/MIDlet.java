package javax.microedition.midlet;

import android.content.Intent;
import android.net.Uri;
import com.sun.lwuit.impl.android.LWUITActivity;

public abstract class MIDlet {

    public final void notifyDestroyed() {
        try {
            if (LWUITActivity.currentActivity != null) {
                LWUITActivity.currentActivity.finish();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public final void notifyPaused() {
        // hmmm...
    }

    // wrapper methods used by activity to be able to access protected methods.
    public final void doStartApp() {
        this.startApp();
    }

    public final void doPauseApp() throws MIDletStateChangeException {
        this.pauseApp();
    }

    public final void doDestroyApp(boolean arg0) throws MIDletStateChangeException {
        this.destroyApp(arg0);
    }

    public final boolean platformRequest(String url) throws ConnectionNotFoundException {
        String action;
        if (url.startsWith("tel:")) {
            action = Intent.ACTION_DIAL;
        } else {
            action = Intent.ACTION_DEFAULT;
        }
        Intent intent = new Intent(action, Uri.parse(url));
        LWUITActivity.currentActivity.startActivity(intent);
        /**
         * return:
         * "true if the MIDlet suite MUST first exit before the content can be fetched. "
         *  by default our process and activity stays alive.
         */
        return false;
    }

    public final String getAppProperty(String key){
        /**
         * todo.
         */
        if(key == null){
            throw new NullPointerException("null key");
        }
        return null;
    }

    // important midlet methods.
    protected abstract void startApp();

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean arg0)
            throws MIDletStateChangeException;
}
