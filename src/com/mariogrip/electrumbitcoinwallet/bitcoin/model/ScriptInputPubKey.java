package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader.InsufficientBytesException;

public class ScriptInputPubKey extends ScriptInput {

   private byte[] _signature;

   protected ScriptInputPubKey(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
      _signature = chunks[0];
   }

   protected static boolean isScriptInputPubKey(byte[][] chunks) throws ScriptParsingException {
      try {
         if (chunks.length != 1) {
            return false;
         }

         // Verify that the chunk contains two DER encoded BigIntegers
         ByteReader reader = new ByteReader(chunks[0]);

         // Read tag, must be 0x30
         if ((((int) reader.get()) & 0xFF) != 0x30) {
            return false;
         }

         // Read total length as a byte, standard inputs never get longer than
         // this
         int length = ((int) reader.get()) & 0xFF;
         if (reader.available() < length) {
            return false;
         }

         // Read first type, must be 0x02
         if ((((int) reader.get()) & 0xFF) != 0x02) {
            return false;
         }

         // Read first length
         int length1 = ((int) reader.get()) & 0xFF;
         if (reader.available() < length1) {
            return false;
         }
         reader.skip(length1);

         // Read second type, must be 0x02
         if ((((int) reader.get()) & 0xFF) != 0x02) {
            return false;
         }

         // Read second length
         int length2 = ((int) reader.get()) & 0xFF;
         if (reader.available() < length2) {
            return false;
         }
         reader.skip(length2);

         // Make sure that we have 0x01 at the end
         if (reader.available() != 1) {
            return false;
         }
         if ((((int) reader.get()) & 0xFF) != 0x01) {
            return false;
         }

         return true;
      } catch (InsufficientBytesException e) {
         throw new ScriptParsingException("Unable to parse " + ScriptInputPubKey.class.getSimpleName());
      }
   }

   /**
    * Get the signature of this input.
    */
   public byte[] getSignature() {
      return _signature;
   }

}
