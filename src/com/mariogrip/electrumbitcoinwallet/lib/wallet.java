/* Electrum Bitcoin wallet android navetive
 * This file = Class walletStorage from py version */
package com.mariogrip.electrumbitcoinwallet.lib;

import android.util.Log;

import com.mariogrip.electrumbitcoinwallet.R.string;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class wallet {
int COINBASE_MATURITY = 100;
int DUST_THRSHOLD = 5430;
String IMPORTED_ACCOUNT = "/x";
JSONObject reads;
String gets;

	public static void NewWallet(string arr){
		init_path();

		
	}
	public static void init_path(){
		File MkDir = new File("/sdcard/electrum/");
		File MkFile = new File("/sdcard/electrum/wallet-a");
		if (!MkDir.exists()){
		MkDir.mkdirs();
		}
		if (!MkFile.exists()){
			try {
				MkFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean canWR(){
		init_path();
		File Read = new File("/sdcard/electrum/wallet-a");
		if (Read.canRead() && Read.canWrite()){
			return true;
			}else{
				Log.e("Electrum LOG", "error? canRW");
			return false;
			}
	}
	
	public void read(){
		//this.canWR();
		//	try{
		/*	JSONParser parser = new JSONParser();
		//	Object fileA = parser.parse(new FileReader("/sdcard/electrum/wallet-a"));
		//	JSONObject Readobj = (JSONObject) fileA;
		//	this.reads = Readobj;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			*/
	}
	
	
	public String get(String key){
		try {
		  this.gets = (String) this.reads.get(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.gets;
	}
	public static boolean put(String key, String value, boolean save) throws JSONException, IOException, Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/sdcard/electrum/wallet-a"));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        JSONArray file = new JSONArray(builder.toString());
        JSONObject files = null;
        for (int i = 0; i < file.length(); i++) {
            files = file.getJSONObject(i);
        }
        JSONObject obj = new JSONObject();
        files.put(key, value);
        FileWriter file2 = new FileWriter("/sdcard/electrum/wallet-a");
        file2.write(obj.toString());
        file2.flush();
        file2.close();
        return true;
    }
    public static void write(){

    }

}
