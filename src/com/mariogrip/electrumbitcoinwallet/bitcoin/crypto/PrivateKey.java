package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto;

import java.io.Serializable;
import java.math.BigInteger;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteWriter;

public abstract class PrivateKey implements BitcoinSigner , Serializable {

   private static final long serialVersionUID = 1L;

   public abstract PublicKey getPublicKey();

   @Override
   public byte[] makeStandardBitcoinSignature(byte[] transactionSigningHash) {
      byte[] signature = signMessage(transactionSigningHash);
      ByteWriter writer = new ByteWriter(1024);
      // Add signature
      writer.putBytes(signature);
      // Add hash type
      writer.put((byte) ((0 + 1) | 0));
      return writer.toBytes();
   }

   protected byte[] signMessage(byte[] message) {
      BigInteger[] signature = generateSignature(message);
      // Write DER encoding of signature
      ByteWriter writer = new ByteWriter(1024);
      // Write tag
      writer.put((byte) 0x30);
      // Write total length
      byte[] s1 = signature[0].toByteArray();
      byte[] s2 = signature[1].toByteArray();
      int totalLength = 2 + s1.length + 2 + s2.length;
      if (totalLength > 127) {
         // We assume that the total length never goes beyond a 1-byte
         // representation
         throw new RuntimeException("Unsupported signature length: " + totalLength);
      }
      writer.put((byte) (totalLength & 0xFF));
      // Write type
      writer.put((byte) 0x02);
      // We assume that the length never goes beyond a 1-byte representation
      writer.put((byte) (s1.length & 0xFF));
      // Write bytes
      writer.putBytes(s1);
      // Write type
      writer.put((byte) 0x02);
      // We assume that the length never goes beyond a 1-byte representation
      writer.put((byte) (s2.length & 0xFF));
      // Write bytes
      writer.putBytes(s2);
      return writer.toBytes();
   }

   protected abstract BigInteger[] generateSignature(byte[] message);

   @Override
   public int hashCode() {
      return getPublicKey().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PrivateKey)) {
         return false;
      }
      PrivateKey other = (PrivateKey) obj;
      return getPublicKey().equals(other.getPublicKey());
   }

}
