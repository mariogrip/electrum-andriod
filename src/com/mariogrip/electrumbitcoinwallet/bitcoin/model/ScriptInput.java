package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

public class ScriptInput extends Script {

   public static final ScriptInput EMPTY = new ScriptInput(new byte[] {});

   public static ScriptInput fromScriptBytes(byte[] scriptBytes) throws ScriptParsingException {
      byte[][] chunks = Script.chunksFromScriptBytes(scriptBytes);
      if (ScriptInputStandard.isScriptInputStandard(chunks)) {
         return new ScriptInputStandard(chunks, scriptBytes);
      } else if (ScriptInputPubKey.isScriptInputPubKey(chunks)) {
         return new ScriptInputPubKey(chunks, scriptBytes);
      } else {
         return new ScriptInput(scriptBytes);
      }

   }

   /**
    * Construct an input script from an output script.
    * <p>
    * This is used when verifying or generating signatures, where the input is
    * set to the output of the funding transaction.
    */
   public static ScriptInput fromOutputScript(ScriptOutput output) {
      return new ScriptInput(output._scriptBytes);
   }

   protected ScriptInput(byte[] scriptBytes) {
      super(scriptBytes, false);
   }

   /**
    * Special constructor for coinbase scripts
    * 
    * @param script
    */
   protected ScriptInput(byte[] script, boolean isCoinBase) {
      super(script, isCoinBase);
   }

}
