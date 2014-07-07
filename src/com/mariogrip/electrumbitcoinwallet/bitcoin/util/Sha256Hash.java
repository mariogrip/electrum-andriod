package com.mariogrip.electrumbitcoinwallet.bitcoin.util;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Sha256Hash implements Serializable {
   private static final long serialVersionUID = 1L;

   public static final Sha256Hash ZERO_HASH = new Sha256Hash();
   public static final int HASH_LENGTH = 32;

   final private byte[] _bytes;
   private int _hash;

   private Sha256Hash() {
      this._bytes = new byte[32];
      _hash = -1;
   }

   public Sha256Hash(byte[] bytes) {
      this._bytes = bytes;
      _hash = -1;
   }

   public Sha256Hash(byte[] bytes, boolean reverse) {
      if (reverse) {
         this._bytes = BitUtils.reverseBytes(bytes);
      } else {
         this._bytes = bytes;

      }
      _hash = -1;
   }

   public Sha256Hash(byte[] bytes, int offset, boolean reverse) {
      _bytes = new byte[32];
      if (reverse) {
         // Copy 32 byte hash from offset and reverse byte order
         for (int i = 0; i < _bytes.length; i++) {
            _bytes[i] = bytes[offset + 32 - 1 - i];
         }
      } else {
         System.arraycopy(bytes, offset, _bytes, 0, 32);
      }
      _hash = -1;
   }

   public Sha256Hash(ByteBuffer buf, boolean reverse) {
      byte[] bytes = new byte[32];
      buf.get(bytes, 0, 32);
      if (reverse) {
         this._bytes = BitUtils.reverseBytes(bytes);
      } else {
         this._bytes = bytes;
      }
      _hash = -1;
   }

   public static Sha256Hash create(byte[] contents) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         return new Sha256Hash(digest.digest(contents));
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e); // Cannot happen.
      }
   }

   @Override
   public boolean equals(Object other) {
      if (other == this) {
         return true;
      }
      if (!(other instanceof Sha256Hash))
         return false;
      return Arrays.equals(_bytes, ((Sha256Hash) other)._bytes);
   }

   @Override
   public int hashCode() {
      if (_hash == -1) {
         final int offset = _bytes.length - 4;
         _hash = 0;
         for (int i = 0; i < 4; i++) {
            _hash <<= 8;
            _hash |= (((int) _bytes[offset + i]) & 0xFF);
         }
      }
      return _hash;
   }

   @Override
   public String toString() {
      return HexUtils.toHex(_bytes);
   }

   public byte[] getBytes() {
      return _bytes;
   }

   public void toByteBuffer(ByteBuffer buf, boolean reverse) {
      if (reverse) {
         buf.put(BitUtils.reverseBytes(_bytes));
      } else {
         buf.put(_bytes);
      }
   }

   public Sha256Hash duplicate() {
      return new Sha256Hash(_bytes);
   }
}
