package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

import java.io.Serializable;

/**
 * This class is used for output scripts that we do not understand
 */
public class ScriptOutputStrange extends ScriptOutput implements Serializable {
   private static final long serialVersionUID = 1L;

   protected ScriptOutputStrange(byte[][] chunks, byte[] scriptBytes) {
      super(scriptBytes);
   }

   @Override
   public Address getAddress(NetworkParameters network) {
      // We cannot determine the address from scripts we do not understand
      return Address.getNullAddress(network);
   }

}
