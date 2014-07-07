package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.io.Serializable;

public class ScriptOutputStandard extends ScriptOutput implements Serializable {
   private static final long serialVersionUID = 1L;

   private byte[] _addressBytes;

   protected ScriptOutputStandard(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
      _addressBytes = chunks[2];
   }

   protected static boolean isScriptOutputStandard(byte[][] chunks) {
      if (chunks.length != 5 && chunks.length != 6) {
         return false;
      }
      if (!Script.isOP(chunks[0], OP_DUP)) {
         return false;
      }
      if (!Script.isOP(chunks[1], OP_HASH160)) {
         return false;
      }
      if (chunks[2].length != 20) {
         return false;
      }
      if (!Script.isOP(chunks[3], OP_EQUALVERIFY)) {
         return false;
      }
      if (!Script.isOP(chunks[4], OP_CHECKSIG)) {
         return false;
      }
      if (chunks.length == 6 && !Script.isOP(chunks[5], OP_NOP)) {
         // Variant that has a NOP at the end
         return false;
      }
      return true;
   }

   public ScriptOutputStandard(byte[] addressBytes) {
      super(scriptEncodeChunks(new byte[][] { { (byte) OP_DUP }, { (byte) OP_HASH160 }, addressBytes,
            { (byte) OP_EQUALVERIFY }, { (byte) OP_CHECKSIG } }));
      _addressBytes = addressBytes;
   }

   /**
    * Get the address that this output is for.
    * 
    * @return The address that this output is for.
    */
   public byte[] getAddressBytes() {
      return _addressBytes;
   }

   @Override
   public Address getAddress(NetworkParameters network) {
      return Address.fromStandardBytes(getAddressBytes(), network);
   }

}
