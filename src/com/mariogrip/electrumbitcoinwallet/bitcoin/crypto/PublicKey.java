package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

import com.mariogrip.electrumbitcoinwallet.bitcoin.crypto.ec.EcTools;
import com.mariogrip.electrumbitcoinwallet.bitcoin.crypto.ec.Parameters;
import com.mariogrip.electrumbitcoinwallet.bitcoin.crypto.ec.Point;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HashUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.ByteReader.InsufficientBytesException;

public class PublicKey implements Serializable {

   private static final long serialVersionUID = 1L;

   private final byte[] _pubKeyBytes;
   private byte[] _pubKeyHash;
   private Point _Q;

   public PublicKey(byte[] publicKeyBytes) {
      _pubKeyBytes = publicKeyBytes;
   }

   public byte[] getPublicKeyBytes() {
      return _pubKeyBytes;
   }

   public byte[] getPublicKeyHash() {
      if (_pubKeyHash == null) {
         _pubKeyHash = HashUtils.addressHash(_pubKeyBytes);
      }
      return _pubKeyHash;
   }

   @Override
   public int hashCode() {
      byte[] hash = getPublicKeyHash();
      return ((int) hash[0]) + (((int) hash[1]) << 8) + (((int) hash[1]) << 16) + (((int) hash[1]) << 32);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PublicKey)) {
         return false;
      }
      PublicKey other = (PublicKey) obj;
      return Arrays.equals(getPublicKeyHash(), other.getPublicKeyHash());
   }

   @Override
   public String toString() {
      return HexUtils.toHex(_pubKeyBytes);
   }

   public boolean verifyStandardBitcoinSignature(byte[] data, byte[] signature) {
      // Decode parameters r and s
      ByteReader reader = new ByteReader(signature);

      BigInteger[] params = decodeSignatureParameters(reader);
      if (params == null) {
         return false;
      }
      // Make sure that we have a hash type at the end
      if (reader.available() != 1) {
         return false;
      }
      return verifySignature(data, params[0], params[1], getQ());
   }

   /**
    * Is this a compressed public key?
    */
   public boolean isCompressed() {
      return getQ().isCompressed();
   }

   private Point getQ() {
      if (_Q == null) {
         _Q = Parameters.curve.decodePoint(_pubKeyBytes);
      }
      return _Q;
   }

   private static BigInteger[] decodeSignatureParameters(ByteReader reader) {
      try {
         // Read tag, must be 0x30
         if ((((int) reader.get()) & 0xFF) != 0x30) {
            return null;
         }

         // Read total length as a byte, standard inputs never get longer than
         // this
         int length = ((int) reader.get()) & 0xFF;

         // Read first type, must be 0x02
         if ((((int) reader.get()) & 0xFF) != 0x02) {
            return null;
         }

         // Read first length
         int length1 = ((int) reader.get()) & 0xFF;

         // Read first byte array
         byte[] bytes1 = reader.getBytes(length1);

         // Make sure BigInteger regards it as positive
         bytes1 = makePositive(bytes1);

         // Read second type, must be 0x02
         if ((((int) reader.get()) & 0xFF) != 0x02) {
            return null;
         }

         // Read second length
         int length2 = ((int) reader.get()) & 0xFF;

         // Read second byte array
         byte[] bytes2 = reader.getBytes(length2);

         // Make sure BigInteger regards it as positive
         bytes2 = makePositive(bytes2);

         // Validate that the lengths add up to the total
         if (2 + length1 + 2 + length2 != length) {
            return null;
         }

         BigInteger[] result = new BigInteger[] { new BigInteger(bytes1), new BigInteger(bytes2) };
         return result;
      } catch (InsufficientBytesException e) {
         return null;
      }
   }

   private static byte[] makePositive(byte[] bytes) {
      if (bytes[0] < 0) {
         byte[] temp = new byte[bytes.length + 1];
         System.arraycopy(bytes, 0, temp, 1, bytes.length);
         return temp;
      }
      return bytes;
   }

   private static boolean verifySignature(byte[] message, BigInteger r, BigInteger s, Point Q) {
      BigInteger n = Parameters.n;
      BigInteger e = calculateE(n, message);

      // r in the range [1,n-1]
      if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(n) >= 0) {
         return false;
      }

      // s in the range [1,n-1]
      if (s.compareTo(BigInteger.ONE) < 0 || s.compareTo(n) >= 0) {
         return false;
      }

      BigInteger c = s.modInverse(n);

      BigInteger u1 = e.multiply(c).mod(n);
      BigInteger u2 = r.multiply(c).mod(n);

      Point G = Parameters.G;

      Point point = EcTools.sumOfTwoMultiplies(G, u1, Q, u2);

      BigInteger v = point.getX().toBigInteger().mod(n);

      return v.equals(r);
   }

   private static BigInteger calculateE(BigInteger n, byte[] message) {
      if (n.bitLength() > message.length * 8) {
         return new BigInteger(1, message);
      } else {
         int messageBitLength = message.length * 8;
         BigInteger trunc = new BigInteger(1, message);

         if (messageBitLength - n.bitLength() > 0) {
            trunc = trunc.shiftRight(messageBitLength - n.bitLength());
         }

         return trunc;
      }
   }

}
