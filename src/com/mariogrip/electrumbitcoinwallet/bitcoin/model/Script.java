package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.util.HashMap;
import java.util.Map;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader.InsufficientBytesException;

public abstract class Script {

   public static class ScriptParsingException extends Exception {
      private static final long serialVersionUID = 1L;

      public ScriptParsingException(byte[] script) {
         super("Unable to parse script: " + HexUtils.toHex(script));
      }

      public ScriptParsingException(String message) {
         super(message);
      }

   }

   public static final int OP_FALSE = 0;
   public static final int OP_PUSHDATA1 = 76;
   public static final int OP_PUSHDATA2 = 77;
   public static final int OP_PUSHDATA4 = 78;
   public static final int OP_1NEGATE = 79;
   public static final int OP_TRUE = 81;
   public static final int OP_2 = 82;
   public static final int OP_3 = 83;
   public static final int OP_NOP = 97;
   public static final int OP_IF = 99;
   public static final int OP_VERIFY = 105;
   public static final int OP_IFDUP = 115;
   public static final int OP_DEPTH = 116;
   public static final int OP_DROP = 117;
   public static final int OP_DUP = 118;
   public static final int OP_EQUAL = 135;
   public static final int OP_EQUALVERIFY = 136;
   public static final int OP_MIN = 163;
   public static final int OP_SHA256 = 168;
   public static final int OP_HASH160 = 169;
   public static final int OP_CHECKSIG = 172;
   public static final int OP_CHECKSIGVERIFY = 173;
   public static final int OP_CHECKMULTISIG = 174;
   public static final int OP_CHECKMULTISIGVERIFY = 175;
   public static final int OP_NOP1 = 176;
   public static final int OP_NOP2 = 177;

   public static final Map<Integer, String> OP_CODE_MAP;
   static {
      OP_CODE_MAP = new HashMap<Integer, String>();
      OP_CODE_MAP.put(OP_FALSE, "OP_FALSE");
      OP_CODE_MAP.put(OP_PUSHDATA1, "OP_PUSHDATA1");
      OP_CODE_MAP.put(OP_PUSHDATA2, "OP_PUSHDATA2");
      OP_CODE_MAP.put(OP_PUSHDATA4, "OP_PUSHDATA4");
      OP_CODE_MAP.put(OP_1NEGATE, "OP_1NEGATE");
      OP_CODE_MAP.put(OP_TRUE, "OP_TRUE");
      OP_CODE_MAP.put(OP_2, "OP_2");
      OP_CODE_MAP.put(OP_3, "OP_3");
      OP_CODE_MAP.put(OP_NOP, "OP_NOP");
      OP_CODE_MAP.put(OP_IF, "OP_IF");
      OP_CODE_MAP.put(OP_VERIFY, "OP_VERIFY");
      OP_CODE_MAP.put(OP_IFDUP, "OP_IFDUP");
      OP_CODE_MAP.put(OP_DEPTH, "OP_DEPTH");
      OP_CODE_MAP.put(OP_DROP, "OP_DROP");
      OP_CODE_MAP.put(OP_DUP, "OP_DUP");
      OP_CODE_MAP.put(OP_EQUAL, "OP_EQUAL");
      OP_CODE_MAP.put(OP_EQUALVERIFY, "OP_EQUALVERIFY");
      OP_CODE_MAP.put(OP_MIN, "OP_MIN");
      OP_CODE_MAP.put(OP_SHA256, "OP_SHA256");
      OP_CODE_MAP.put(OP_HASH160, "OP_HASH160");
      OP_CODE_MAP.put(OP_CHECKSIG, "OP_CHECKSIG");
      OP_CODE_MAP.put(OP_CHECKSIGVERIFY, "OP_CHECKSIGVERIFY");
      OP_CODE_MAP.put(OP_CHECKMULTISIG, "OP_CHECKMULTISIG");
      OP_CODE_MAP.put(OP_CHECKMULTISIGVERIFY, "OP_CHECKMULTISIGVERIFY");
      OP_CODE_MAP.put(OP_NOP1, "OP_NOP1");
      OP_CODE_MAP.put(OP_NOP2, "OP_NOP2");
   }
   // protected byte[][] _chunks;
   protected byte[] _scriptBytes;
   private boolean _isCoinbase;

   // protected Script(byte[][] chunks) {
   // _chunks = chunks;
   // _isCoinbase = false;
   // }

   protected Script(byte[] scriptBytes, boolean isCoinBase) {
      // We handle coinbase scripts in a special way, as anything can be
      // stored in them, also stuff that does not parse
      // _chunks = new byte[][] { scriptBytes };
      _scriptBytes = scriptBytes;
      _isCoinbase = isCoinBase;
   }

   public boolean isCoinBase() {
      return _isCoinbase;
   }

   protected static final boolean isOP(byte[] chunk, int op) {
      return chunk.length == 1 && (((int) chunk[0]) & 0xFF) == op;
   }

   protected static final byte[][] chunksFromScriptBytes(byte[] script) throws ScriptParsingException {
      try {
         ByteReader reader = new ByteReader(script);
         int numChunks = countChuks(reader);
         if (numChunks == -1) {
            throw new ScriptParsingException(script);
         }
         byte[][] chunks = new byte[numChunks][];
         int index = 0;
         reader.reset();
         while (reader.available() > 0) {

            // Get opcode
            int opcode = reader.get();
            if (opcode >= 0xF0) {
               opcode = (opcode << 8) | reader.get();
            }

            if (opcode > 0 && opcode < OP_PUSHDATA1) {
               chunks[index++] = reader.getBytes(opcode);
            } else if (opcode == OP_PUSHDATA1) {
               int size = ((int) reader.get()) & 0xFF;
               chunks[index++] = reader.getBytes(size);
            } else if (opcode == OP_PUSHDATA2) {
               int size = reader.getShortLE();
               chunks[index++] = reader.getBytes(size);
            } else if (opcode == OP_PUSHDATA4) {
               // We do not support chunks this big
               throw new ScriptParsingException(script);
            } else {
               chunks[index++] = new byte[] { (byte) opcode };
            }
         }
         return chunks;
      } catch (InsufficientBytesException e) {
         throw new ScriptParsingException(script);
      }
   }

   private static int countChuks(ByteReader reader) throws InsufficientBytesException {
      int chunks = 0;
      while (reader.available() > 0) {

         // Get opcode
         int opcode = reader.get();
         if (opcode >= 0xF0) {
            opcode = (opcode << 8) | reader.get();
         }

         if (opcode > 0 && opcode < OP_PUSHDATA1) {
            chunks++;
            reader.skip(opcode);
         } else if (opcode == OP_PUSHDATA1) {
            int size = ((int) reader.get()) & 0xFF;
            chunks++;
            reader.skip(size);
         } else if (opcode == OP_PUSHDATA2) {
            int size = reader.getShortLE();
            chunks++;
            reader.skip(size);
         } else if (opcode == OP_PUSHDATA4) {
            // We do not support chunks this big
            return -1;
         } else {
            chunks++;
         }
      }
      return chunks;
   }

   // private int calculateByteSize() {
   // int size = 0;
   // for (byte[] chunk : _chunks) {
   // if (chunk.length == 1) {
   // size++;
   // } else if (chunk.length < OP_PUSHDATA1) {
   // size += 1 + chunk.length;
   // } else if (chunk.length < 256) {
   // size += 1 + 1 + chunk.length;
   // } else if (chunk.length < 65536) {
   // size += 1 + 1 + 1 + chunk.length;
   // } else {
   // throw new RuntimeException("Chunks larger than 65536 not implemented");
   // }
   // }
   // return size;
   // }

   public String dump(int maxLen) {
      String s = dump();
      if (s.length() > maxLen) {
         if (maxLen > 3) {
            s = s.substring(0, maxLen - 3) + "...";
         } else {
            s = s.substring(0, maxLen);
         }
      }
      return s;

   }

   public String dump() {
      if (_isCoinbase) {
         // coinbase scripts often cannot be parsed, hex dump them instead
         return HexUtils.toHex(_scriptBytes);
      }
      StringBuilder sb = new StringBuilder();
      byte[][] chunks;
      try {
         chunks = chunksFromScriptBytes(_scriptBytes);
      } catch (ScriptParsingException e) {
         return "Invalid script";
      }
      for (byte[] chunk : chunks) {
         if (chunk.length == 1) {
            int opCode = ((int) chunk[0]) & 0xFF;
            String opCodeString = OP_CODE_MAP.get(opCode);
            if (opCodeString == null) {
               sb.append(opCode);
            } else {
               sb.append(opCodeString);
            }
         } else {
            sb.append(HexUtils.toHex(chunk));
         }
         sb.append(' ');
      }
      return sb.toString();
   }

   /**
    * Get the script as an array of bytes
    * 
    * @return The script as an array of bytes
    */
   public byte[] getScriptBytes() {
      return _scriptBytes;
      // if (_isCoinbase) {
      // return _scriptBytes;
      // }
      // byte[] buf = new byte[calculateByteSize()];
      // int index = 0;
      // for (byte[] chunk : _chunks) {
      // if (chunk.length == 1) {
      // buf[index++] = chunk[0];
      // } else if (chunk.length < OP_PUSHDATA1) {
      // buf[index++] = (byte) (0xFF & chunk.length);
      // System.arraycopy(chunk, 0, buf, index, chunk.length);
      // index += chunk.length;
      // } else if (chunk.length < 256) {
      // buf[index++] = (byte) (0xFF & OP_PUSHDATA1);
      // buf[index++] = (byte) (0xFF & chunk.length);
      // System.arraycopy(chunk, 0, buf, index, chunk.length);
      // index += chunk.length;
      // } else if (chunk.length < 65536) {
      // buf[index++] = (byte) (0xFF & OP_PUSHDATA2);
      // buf[index++] = (byte) (0xFF & chunk.length);
      // buf[index++] = (byte) (0xFF & (chunk.length >> 8));
      // System.arraycopy(chunk, 0, buf, index, chunk.length);
      // index += chunk.length;
      // } else {
      // throw new RuntimeException("Chunks larger than 65536 not implemented");
      // }
      // }
      // return buf;
   }

   protected static final byte[] scriptEncodeChunks(byte[][] chunks) {
      byte[] buf = new byte[calculateByteSize(chunks)];
      int index = 0;
      for (byte[] chunk : chunks) {
         if (chunk.length == 1) {
            buf[index++] = chunk[0];
         } else if (chunk.length < OP_PUSHDATA1) {
            buf[index++] = (byte) (0xFF & chunk.length);
            System.arraycopy(chunk, 0, buf, index, chunk.length);
            index += chunk.length;
         } else if (chunk.length < 256) {
            buf[index++] = (byte) (0xFF & OP_PUSHDATA1);
            buf[index++] = (byte) (0xFF & chunk.length);
            System.arraycopy(chunk, 0, buf, index, chunk.length);
            index += chunk.length;
         } else if (chunk.length < 65536) {
            buf[index++] = (byte) (0xFF & OP_PUSHDATA2);
            buf[index++] = (byte) (0xFF & chunk.length);
            buf[index++] = (byte) (0xFF & (chunk.length >> 8));
            System.arraycopy(chunk, 0, buf, index, chunk.length);
            index += chunk.length;
         } else {
            throw new RuntimeException("Chunks larger than 65536 not implemented");
         }
      }
      return buf;
   }

   private static final int calculateByteSize(byte[][] chunks) {
      int size = 0;
      for (byte[] chunk : chunks) {
         if (chunk.length == 1) {
            size++;
         } else if (chunk.length < OP_PUSHDATA1) {
            size += 1 + chunk.length;
         } else if (chunk.length < 256) {
            size += 1 + 1 + chunk.length;
         } else if (chunk.length < 65536) {
            size += 1 + 1 + 1 + chunk.length;
         } else {
            throw new RuntimeException("Chunks larger than 65536 not implemented");
         }
      }
      return size;
   }

}
