package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HashUtils;

public class ScriptOutputMsg extends ScriptOutput implements Serializable {
   private static final long serialVersionUID = 1L;

   private byte[] _messageBytes;
   private byte[] _publicKeyBytes;

   protected ScriptOutputMsg(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
      _messageBytes = chunks[0];
      _publicKeyBytes = chunks[2];
   }

   protected static boolean isScriptOutputMsg(byte[][] chunks) {
      if (chunks.length != 4) {
         return false;
      }
      if (!Script.isOP(chunks[1], OP_DROP)) {
         return false;
      }
      if (!Script.isOP(chunks[3], OP_CHECKSIG)) {
         return false;
      }
      return true;
   }

   /**
    * Get the bytes for the message contained in this output.
    * 
    * @return The message bytes of this output.
    */
   public byte[] getMessageBytes() {
      return _messageBytes;
   }

   public String getMessage() {
      try {
         return new String(getMessageBytes(), "US-ASCII");
      } catch (UnsupportedEncodingException e) {
         return "";
      }
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
