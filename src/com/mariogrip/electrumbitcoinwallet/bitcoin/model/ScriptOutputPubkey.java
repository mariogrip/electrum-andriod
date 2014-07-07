package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.io.Serializable;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HashUtils;

public class ScriptOutputPubkey extends ScriptOutput implements Serializable {
   private static final long serialVersionUID = 1L;

   private byte[] _publicKeyBytes;

   protected ScriptOutputPubkey(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
      _publicKeyBytes = chunks[0];
   }

   protected static boolean isScriptOutputPubkey(byte[][] chunks) {
      if (chunks.length != 2) {
         return false;
      }
      if (!Script.isOP(chunks[1], OP_CHECKSIG)) {
         return false;
      }
      return true;
   }

   /**
    * Get the public key bytes that this output is for.
    * 
    * @return The public key bytes that this output is for.
    */
   public byte[] getPublicKeyBytes() {
      return _publicKeyBytes;
   }

   @Override
   public Address getAddress(NetworkParameters network) {
      byte[] addressBytes = HashUtils.addressHash(getPublicKeyBytes());
      return Address.fromStandardBytes(addressBytes, network);
   }

}
