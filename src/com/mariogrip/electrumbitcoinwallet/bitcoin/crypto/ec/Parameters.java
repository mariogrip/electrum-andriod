package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto.ec;

import java.math.BigInteger;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;

public class Parameters {
   public static final Curve curve;
   public static final byte[] seed;
   public static final Point G;
   public static final BigInteger n;
   public static final BigInteger h;

   static {
      BigInteger p = new BigInteger(1,
            HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
      BigInteger a = BigInteger.ZERO;
      BigInteger b = BigInteger.valueOf(7);
      curve = new Curve(p, a, b);
      seed = null;
      G = curve.decodePoint(HexUtils.toBytes("04" + "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"
            + "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"));
      n = new BigInteger(1, HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"));
      h = BigInteger.ONE;
   }
}
