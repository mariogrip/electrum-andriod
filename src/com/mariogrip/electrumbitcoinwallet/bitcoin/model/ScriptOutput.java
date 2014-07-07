package com.mariogrip.electrumbitcoinwallet.bitcoin.model;

public abstract class ScriptOutput extends Script {

   public static ScriptOutput fromScriptBytes(byte[] scriptBytes) throws ScriptParsingException {
      byte[][] chunks = Script.chunksFromScriptBytes(scriptBytes);
      if (chunks == null) {
         return null;
      }
      if (ScriptOutputStandard.isScriptOutputStandard(chunks)) {
         return new ScriptOutputStandard(chunks, scriptBytes);
      } else if (ScriptOutputPubkey.isScriptOutputPubkey(chunks)) {
         return new ScriptOutputPubkey(chunks, scriptBytes);
      } else if (ScriptOutputMultisig.isScriptOutputMultisig(chunks)) {
         return new ScriptOutputMultisig(chunks, scriptBytes);
      } else if (ScriptOutputMsg.isScriptOutputMsg(chunks)) {
         return new ScriptOutputMsg(chunks, scriptBytes);
      } else {
         return new ScriptOutputStrange(chunks, scriptBytes);
      }

   }

   protected ScriptOutput(byte[] scriptBytes) {
      super(scriptBytes, false);
   }

   public abstract Address getAddress(NetworkParameters network);

}
