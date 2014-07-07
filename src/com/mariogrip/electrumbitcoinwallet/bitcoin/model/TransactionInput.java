package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import com.mariogrip.electrumbitcoinwallet.bitcoin.model.Script.ScriptParsingException;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteWriter;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.Sha256Hash;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader.InsufficientBytesException;

public class TransactionInput {

   public static class TransactionInputParsingException extends Exception {
      private static final long serialVersionUID = 1L;

      public TransactionInputParsingException(byte[] script) {
         super("Unable to parse transaction input: " + HexUtils.toHex(script));
      }

      public TransactionInputParsingException(String message) {
         super(message);
      }
   }

   private static final int NO_SEQUENCE = -1;

   public OutPoint outPoint;
   public ScriptInput script;
   public int sequence;

   public static TransactionInput fromByteReader(ByteReader reader) throws TransactionInputParsingException {
      try {
         Sha256Hash outPointHash = reader.getSha256Hash(true);
         int outPointIndex = reader.getIntLE();
         int scriptSize = (int) reader.getCompactInt();
         byte[] script = reader.getBytes(scriptSize);
         int sequence = (int) reader.getIntLE();
         OutPoint outPoint = new OutPoint(outPointHash, outPointIndex);
         ScriptInput inscript;
         if (outPointHash.equals(Sha256Hash.ZERO_HASH)) {
            // Coinbase scripts are special as they can contain anything that
            // does not parse
            inscript = new ScriptInputCoinbase(script);
         } else {
            try {
               inscript = ScriptInput.fromScriptBytes(script);
            } catch (ScriptParsingException e) {
               throw new TransactionInputParsingException(e.getMessage());
            }
         }
         return new TransactionInput(outPoint, inscript, sequence);
      } catch (InsufficientBytesException e) {
         throw new TransactionInputParsingException("Unable to parse transaction input: " + e.getMessage());
      }
   }

   public TransactionInput(OutPoint outPoint, ScriptInput script, int sequence) {
      this.outPoint = outPoint;
      this.script = script;
      this.sequence = sequence;
   }

   public TransactionInput(OutPoint outPoint, ScriptInput script) {
      this(outPoint, script, NO_SEQUENCE);
   }

   public ScriptInput getScript() {
      return script;
   }

   public void toByteWriter(ByteWriter writer) {
      writer.putSha256Hash(outPoint.hash, true);
      writer.putIntLE(outPoint.index);
      byte[] script = getScript().getScriptBytes();
      writer.putCompactInt(script.length);
      writer.putBytes(script);
      writer.putIntLE(sequence);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("outpoint: ").append(outPoint.hash).append(':').append(outPoint.index);
      sb.append(" scriptSize: ").append(script.getScriptBytes().length);
      return sb.toString();
   }

   @Override
   public int hashCode() {
      return outPoint.hash.hashCode() + outPoint.index;
   }

   @Override
   public boolean equals(Object other) {
      if (other == this) {
         return true;
      }
      if (!(other instanceof TransactionInput)) {
         return false;
      }
      TransactionInput otherInput = (TransactionInput) other;
      return outPoint.equals(otherInput.outPoint);
   }

}
