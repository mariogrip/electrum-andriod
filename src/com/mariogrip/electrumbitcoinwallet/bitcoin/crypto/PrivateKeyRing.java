package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto;

import java.util.HashMap;
import java.util.Map;

import com.mariogrip.electrumbitcoinwallet.bitcoin.model.NetworkParameters;

public class PrivateKeyRing extends PublicKeyRing {

   private Map<PublicKey, PrivateKey> _privateKeys;

   public PrivateKeyRing() {
      _privateKeys = new HashMap<PublicKey, PrivateKey>();
   }

   /**
    * Add a private key to the key ring.
    */
   public void addPrivateKey(PrivateKey key, NetworkParameters network) {
      _privateKeys.put(key.getPublicKey(), key);
      addPublicKey(key.getPublicKey(), network);
   }

   /**
    * Find a Bitcoin signer by public key
    */
   public BitcoinSigner findSignerByPublicKey(PublicKey publicKey) {
      return _privateKeys.get(publicKey);
   }

   /**
    * Find a KeyExporter by public key
    */
   public KeyExporter findKeyExporterByPublicKey(PublicKey publicKey) {
      PrivateKey key = _privateKeys.get(publicKey);
      if (key instanceof KeyExporter) {
         return (KeyExporter) key;
      }
      return null;
   }

}
