package com.mariogrip.electrumbitcoinwallet.lib;

/**
 * Created by mariogrip on 31.07.14.
 *
 * This is not in use, just to make this like python version....
 */
public class version {
    String ELECTRUM_VERSION_Android = "1.9.8";  // version of the client package
    String PROTOCOL_VERSION = "0.9";    // protocol version requested
    String NEW_SEED_VERSION = "7";        // bip32 wallets
    String OLD_SEED_VERSION = "4";       // old electrum deterministic generation
    String SEED_PREFIX      = "01";    // the hash of the mnemonic seed must begin with this
}
