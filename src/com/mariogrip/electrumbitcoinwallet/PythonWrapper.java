package com.mariogrip.electrumbitcoinwallet;

/**
 * Created by mariogrip on 02.08.14.
 */
public class PythonWrapper {

    // Declare native method (and make it public to expose it directly)
    public static native int start(String datapath, String pyhome);
    public static native int end();
    public static native String quarry(String cmd);

    // Load library
    static {
        System.loadLibrary("python2.7");
        System.loadLibrary("pyjni");
    }
}
