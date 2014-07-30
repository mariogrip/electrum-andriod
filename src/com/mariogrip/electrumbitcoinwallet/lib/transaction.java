package com.mariogrip.electrumbitcoinwallet.lib;

/**
 * Created by mariogrip on 30.07.14.
 */
public class transaction {

    protected String getTx_hash(){
        return "";
    }

    protected String Transaction(String raw){
        deserialize(raw);
        return null;
    }

    private void deserialize(String raw){

    }
}
