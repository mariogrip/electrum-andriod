package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto;

import com.mariogrip.electrumbitcoinwallet.bitcoin.model.NetworkParameters;

/**
 * Allows exporting of private keys in base58 format also known as SIPA format.
 */
public interface KeyExporter {
   /**
    * Get the private key as a base-58 encoded key.
    * 
    * @param network
    *           The network parameters to use
    * @return The private key as a base-58 encoded key.
    */
   String getBase58EncodedPrivateKey(NetworkParameters network);

   /**
    * Get the private key as an array of bytes.
    * 
    * @return The bytes of the private key.
    */
   byte[] getPrivateKeyBytes();
}
