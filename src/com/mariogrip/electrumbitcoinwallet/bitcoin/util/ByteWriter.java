package com.mariogrip.electrumbitcoinwallet.bitcoin.util;

import java.nio.charset.Charset;

import com.mariogrip.electrumbitcoinwallet.bitcoin.model.CompactInt;

final public class ByteWriter {

   private static final Charset UTF8_CHARSET = Charset.forName("UTF8");

   private byte[] _buf;
   private int _index;

   public ByteWriter(int capacity) {
      _buf = new byte[capacity];
      _index = 0;
   }

   public ByteWriter(byte[] buf) {
      _buf = buf;
      _index = buf.length;
   }

   final private void ensureCapacity(int capacity) {
      if (_buf.length - _index < capacity) {
         byte[] temp = new byte[_buf.length * 2 + capacity];
         System.arraycopy(_buf, 0, temp, 0, _index);
         _buf = temp;
      }
   }

   public void put(byte b) {
      ensureCapacity(1);
      _buf[_index++] = b;
   }

   public void putShortLE(short value) {
      ensureCapacity(2);
      _buf[_index++] = (byte) (0xFF & (value >> 0));
      _buf[_index++] = (byte) (0xFF & (value >> 8));
   }

   public void putIntLE(int value) {
      ensureCapacity(4);
      _buf[_index++] = (byte) (0xFF & (value >> 0));
      _buf[_index++] = (byte) (0xFF & (value >> 8));
      _buf[_index++] = (byte) (0xFF & (value >> 16));
      _buf[_index++] = (byte) (0xFF & (value >> 24));
   }

   public void putLongLE(long value) {
      ensureCapacity(8);
      _buf[_index++] = (byte) (0xFFL & (value >> 0));
      _buf[_index++] = (byte) (0xFFL & (value >> 8));
      _buf[_index++] = (byte) (0xFFL & (value >> 16));
      _buf[_index++] = (byte) (0xFFL & (value >> 24));
      _buf[_index++] = (byte) (0xFFL & (value >> 32));
      _buf[_index++] = (byte) (0xFFL & (value >> 40));
      _buf[_index++] = (byte) (0xFFL & (value >> 48));
      _buf[_index++] = (byte) (0xFFL & (value >> 56));
   }

   public void putBytes(byte[] value) {
      ensureCapacity(value.length);
      System.arraycopy(value, 0, _buf, _index, value.length);
      _index += value.length;
   }

   public void putBytes(byte[] value, int offset, int length) {
      ensureCapacity(length);
      System.arraycopy(value, offset, _buf, _index, length);
      _index += length;
   }

   public void putCompactInt(long value) {
      putBytes(CompactInt.toBytes(value));
   }

   public void putSha256Hash(Sha256Hash hash) {
      putBytes(hash.getBytes());
   }

   public void putSha256Hash(Sha256Hash hash, boolean reverse) {
      if (reverse) {
         putBytes(BitUtils.reverseBytes(hash.getBytes()));
      } else {
         putBytes(hash.getBytes());
      }
   }

   public void putString(String s) {
      byte[] bytes = s.getBytes(UTF8_CHARSET);
      putIntLE(bytes.length);
      putBytes(bytes);
   }

   public byte[] toBytes() {
      byte[] bytes = new byte[_index];
      System.arraycopy(_buf, 0, bytes, 0, _index);
      return bytes;
   }

   public int length() {
      return _index;
   }
}
