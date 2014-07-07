package com.mariogrip.electrumbitcoinwallet.lib;

/**
 * Exception for stratum based issues
 * 
 * @author Matt
 *
 */
public class StratumException extends Exception {

	private static final long serialVersionUID = 4342357304794858169L;

	public StratumException(){
		super();
	}
	
	public StratumException(String message) {
		super(message);
	}
	
	public StratumException(String message, Throwable cause) {
		super (message, cause);
	}
	
}
