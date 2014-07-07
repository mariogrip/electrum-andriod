package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import com.mariogrip.electrumbitcoinwallet.bitcoin.model.Script.ScriptParsingException;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteWriter;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader.InsufficientBytesException;

public class TransactionOutput {

   public static class TransactionOutputParsingException extends Exception {
      private static final long serialVersionUID = 1L;

      public TransactionOutputParsingException(byte[] script) {
         super("Unable to parse transaction output: " + HexUtils.toHex(script));
      }
      public TransactionOutputParsingException(String message) {
         super(message);
      }
   }

	public long value;
	public ScriptOutput script;

	public static TransactionOutput fromByteReader(ByteReader reader) throws TransactionOutputParsingException {
	   try {
		long value = reader.getLongLE();
		int scriptSize = (int) reader.getCompactInt();
		byte[] scriptBytes = reader.getBytes(scriptSize);
		ScriptOutput script;
		try {
			script = ScriptOutput.fromScriptBytes(scriptBytes);
		} catch (ScriptParsingException e) {
			throw new TransactionOutputParsingException(scriptBytes);
		}
		return new TransactionOutput(value, script);
	   } catch(InsufficientBytesException e){
         throw new TransactionOutputParsingException("Unable to parse transaction output: "+e.getMessage());
	   }
	}

	public TransactionOutput(long value, ScriptOutput script) {
		this.value = value;
		this.script = script;
	}

	public byte[] toBytes() {
		ByteWriter writer = new ByteWriter(1024);
		toByteWriter(writer);
		return writer.toBytes();
	}

	public void toByteWriter(ByteWriter writer) {
		writer.putLongLE(value);
		byte[] scriptBytes = script.getScriptBytes();
		writer.putCompactInt(scriptBytes.length);
		writer.putBytes(scriptBytes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("value: ").append(value).append(" script: ")
				.append(script.dump());
		return sb.toString();
	}

}
