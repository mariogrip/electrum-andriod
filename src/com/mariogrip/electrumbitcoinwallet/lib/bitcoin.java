package com.mariogrip.electrumbitcoinwallet.lib;

import com.mariogrip.electrumbitcoinwallet.bitcoin.util.HexUtils;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.Sha256Hash;
import com.mariogrip.electrumbitcoinwallet.bitcoin.util.aes;

public class bitcoin {
    HexUtils hex = new HexUtils();


	public String pw_encode(String seed ,String pass) throws Exception {
		if (!pass.equals(null)){
            Sha256Hash sha = new Sha256Hash(pass.getBytes());
            String pass_sha = sha.toString();
            aes ase = new aes();
            byte[] pass_enc = new byte[0];
            pass_enc = ase.encrypt(pass_sha, seed);;
            return pass_enc.toString();
        }else{
            return seed;
        }
	}
	public String pw_decode(String seed, String pass) throws Exception {
        Sha256Hash sha = new Sha256Hash(pass.getBytes());
        String pass_sha = sha.toString();
        aes ase = new aes();
        String pass_dec = ase.decrypt(pass_sha.getBytes(), seed);
        return pass_dec;
	}
	public String rev_hex(String s){
		//TODO Add working rev_hex
		return "";
	}
	public String int_to_hex(int i){
        return Integer.toHexString(i);
	}
	
}
