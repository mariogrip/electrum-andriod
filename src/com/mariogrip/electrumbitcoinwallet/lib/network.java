package com.mariogrip.electrumbitcoinwallet.lib;

import java.util.Random;

public class network {


    int DEF_PORT_F = 50001;
    int DEF_PORT_S = 50002;
    int DEF_PORT_H = 8081;
    int DEF_PORT_G = 8082;

    public static String[] DEF_SERVER = {"ecdsa.org", "ecdsa.net", "electrum.hachre.de"};


    public static String pick_random_server(){
        return DEF_SERVER[new Random().nextInt(DEF_SERVER.length)];
    }

	public static void get_servers(){
		 
	}
	public static void is_connected(){
		
	}
	public static void startup(){
		
	
	}
    public static boolean send(String message, String callback){

return true;
    }
	public static void is_up_to_date(){
		
	}
	public static void main_server(){
		
	}
	
}
