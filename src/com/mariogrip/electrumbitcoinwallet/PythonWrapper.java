package com.mariogrip.electrumbitcoinwallet;

/**
 * Created by mariogrip on 02.08.14.
 */
public class PythonWrapper {

    // Declare native method (and make it public to expose it directly)
    public static native int start();
    public static native int end();

    // Load library
    static {
        //System.loadLibrary("python2.7");
        System.loadLibrary("pyjni");
    }
}
