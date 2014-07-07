package com.mariogrip.electrumbitcoinwallet;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mariogrip.electrumbitcoinwallet.lib.StratumClient;
import com.mariogrip.electrumbitcoinwallet.lib.StratumClientObserver;
import com.mariogrip.electrumbitcoinwallet.lib.StratumException;

public class test implements StratumClientObserver{

	public String server = "stratum2.dogechain.info";
	public int port = 3333;
	public String username = "testing.client";
	public String password = "password";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private StratumClient client;
	
	public void registerObserver() throws StratumException {
		client = new StratumClient(server, port);
		client.registerObserver(this);
		
	}
	
	public void testSubscription() throws StratumException {
		
		client.startStratumSubscription();
		
		
	}

		



	public static void main(String[] args) throws Exception {
		test test = new test();
		test.registerObserver();
		test.testSubscription();

		while(true) {
			Thread.sleep(100);
		}
		
	}
}
