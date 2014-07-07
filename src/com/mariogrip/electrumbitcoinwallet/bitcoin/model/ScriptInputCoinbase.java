package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

public class ScriptInputCoinbase extends ScriptInput {

   public ScriptInputCoinbase(byte[] script) {
      super(script, true);
   }

}
