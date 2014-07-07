package com.mariogrip.electrumbitcoinwallet.lib;

/**
 * Created by mariogrip on 05.07.14.
 */
public class MakeWallet extends wallet {
    static String seed_en;
    static String use_encryption;
    static bitcoin bitcoin = new bitcoin();

    public static void MakeWallet() {
        try {
            add_seed("tessstSeeeld353343", "mypass");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void add_seed(String seed, String password) throws Exception {
        if (!password.isEmpty()){
            seed_en = bitcoin.pw_encode(seed, password);
            use_encryption =  "true";
        }else{
            seed_en = seed;
            use_encryption = "false";
        }
        put("seed", seed_en, true);
        put("seed_version", "1", true); //TODO: seed version
        put("use_encryption", use_encryption, true);
        //TODO: Ceate Master Key

    }
}
