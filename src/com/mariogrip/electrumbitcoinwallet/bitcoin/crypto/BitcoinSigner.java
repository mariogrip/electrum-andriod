package com.mariogrip.electrumbitcoinwallet.bitcoin.crypto;

public interface BitcoinSigner {
   public byte[] makeStandardBitcoinSignature(byte[] transactionSigningHash);
}
