package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.io.Serializable;

public class ScriptOutputMultisig extends ScriptOutput implements Serializable {
   private static final long serialVersionUID = 1L;

   protected ScriptOutputMultisig(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
      _multisigAddressBytes = chunks[1];
   }

   private byte[] _multisigAddressBytes;

   protected static boolean isScriptOutputMultisig(byte[][] chunks) {
      if (chunks.length != 3) {
         return false;
      }
      if (!Script.isOP(chunks[0], OP_HASH160)) {
         return false;
      }
      if (chunks[1].length != 20) {
         return false;
      }
      if (!Script.isOP(chunks[2], OP_EQUAL)) {
         return false;
      }
      return true;
   }

   public ScriptOutputMultisig(byte[] addressBytes) {
      super(scriptEncodeChunks(new byte[][] { { (byte) OP_HASH160 }, addressBytes, { (byte) OP_EQUAL } }));
      _multisigAddressBytes = addressBytes;
   }

   /**
    * Get the raw multisig address that this output is for.
    * 
    * @return The raw multisig address that this output is for.
    */
   public byte[] getMultisigAddressBytes() {
      return _multisigAddressBytes;
   }

   @Override
   public Address getAddress(NetworkParameters network) {
      byte[] addressBytes = getMultisigAddressBytes();
      return Address.fromMultisigBytes(addressBytes, network);
   }

}
