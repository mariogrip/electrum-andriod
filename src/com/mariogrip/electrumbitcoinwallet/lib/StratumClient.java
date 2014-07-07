package com.mariogrip.electrumbitcoinwallet.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Basic stratum client (http://mining.bitcoin.cz/stratum-mining)
 * 
 * Uses several threads to handle simultaneous send & receive on the same socket.  Basic approach
 * is that there is a send & receive queue.  
 * 
 * - Messages that are received from the stratum server are read from the socket and placed onto an 
 * in-bound queue by the in-bound thread.
 * 
 * - Messages that are to be sent to the stratum server are placed onto a queue and then sent to
 * the stratum server by the out-bound thread that monitors the out-bound queue.
 * 
 * - A third thread monitors the in-bond thread.  Messages which are placed on this queue are 
 * taken off, their JSON is parsed, and a POJO for the message is then sent back to all observing 
 * code.
 * 
 * @author Mariogrip
 * BaseCode Matt
 */
public class StratumClient {

	/** ID counter used to keep track of messages */
	private Integer mCounter = 0;
	
	/** Contains all observers that are listening for stratum events */
	private List<StratumClientObserver> observers = new ArrayList<StratumClientObserver>(1);
	
	/** Socket to stratum server */
	private Socket mSocket;
	
	/** Address of the stratum server to connect to.  Should *not* include stratum+tcp:// */
	private String mServer;
	/** Port of the stratum server to connect to */
	private int mPort;
	
	/** Queue for messages that should be sent to the stratum server */
	private BlockingQueue<String> mOutboundQueue = new LinkedBlockingQueue<String>();
	
	/** Qeue for messages that have been received from the stratum server */
	private BlockingQueue<String> mInboundQueue = new LinkedBlockingQueue<String>();
	
	/** Handles sending queued out-bound messages to stratum server */
	private Thread mOutboundThread;
	
	/** Handles reading in-bound messages from the stratum server and queuing them */
	private Thread mInboundThread;
	
	/** Handles reading in-bound messages and updating Observers */
	private Thread mObserverThread;
	
	/** BufferedReader to reading in-bound messages from stratum server */
	private BufferedReader mSocketReceive;
	
	/** PrintWriter for sending messages to the stratum server */
	private PrintWriter mSocketSend;
	
	/** ... just because they are used so much in the JSON parsing */
	private static final String PARAMS = "params";
	private static final String ID = "id";
	private static final String ERROR = "error";
	private static final String RESULT = "result";
	
	/**
	 * Reads any in-bound requests from the stratum server socket and puts them in a queue
	 * 
	 * @author Matt
	 *
	 */
	private class InboundConnection implements Runnable {

		/** Reader for the socket we're monitoring */
		private BufferedReader mBufferedReader;
		
		/**
		 * Creates a new InboundConnection instance
		 * 
		 * @param bufferdReader
		 */
		public InboundConnection(BufferedReader bufferdReader) {
			mBufferedReader = bufferdReader;
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					// Blocks until something comes in then adds to queue
					mInboundQueue.add(mBufferedReader.readLine());
				}
			} catch (IOException ioe) {
				// nothing for now
			}
		}
	}
	
	/**
	 * Takes anything that is on the out-bound queue and sends to the stratum server socket
	 * @author Matt
	 *
	 */
	private class OutboundConnection implements Runnable {

		/** PrintWriter to send commends to on socket */
		private PrintWriter mPrintWriter;
		
		/**
		 * Creates a new OutboundConnection instance
		 * @param printWriter
		 */
		public OutboundConnection(PrintWriter printWriter) {
			mPrintWriter = printWriter;
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					// Blocks on the queue take(), then should send immediately
					String message = mOutboundQueue.take();
					mPrintWriter.println(message);
				}
			} catch (InterruptedException ie) {
				// nothing for now
			}
			
		}
		
	}

	/**
	orising workers so are not needed now - see authoriseWorker for that once the 
	 * connection is established.
	 * 
	 * @param server
	 * @param port
	 * @throws StratumException
	 */
	public StratumClient(String server, int port) throws StratumException {
		mServer = server;
		mPort = port;
		
		setupSocket();
		
		// Setup threads
		mOutboundThread = new Thread(new OutboundConnection(mSocketSend));
		mInboundThread = new Thread(new InboundConnection(mSocketReceive));
		mOutboundThread.start();
		mInboundThread.start();
		mObserverThread.start();
		
	}
	
	/**
	 * Register a new observer that will be informed of events from the stratum server
	 * 
	 * @param observer
	 */
	public void registerObserver(StratumClientObserver observer) {
		observers.add(observer);
	}
	
	/**
	 * Sets up the socket to the stratum server
	 * @throws StratumException 
	 */
	private void setupSocket() throws StratumException {
		try {
			mSocket = new Socket(mServer, mPort);
			mSocketSend = new PrintWriter(mSocket.getOutputStream(), true);
			mSocketReceive = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		} catch (UnknownHostException uhe) {
			throw new StratumException("Unable to use server " + mServer, uhe);
		} catch (IOException ioe) {
			throw new StratumException("Unable to open connection to server " + mServer, ioe);
		}
		
	}
	
	/**
	 * Send a JSON message to the stratum server by queuing it on the outbound queue.
	 * 
	 * @param message
	 */
	private void send(String message) {
		mOutboundQueue.add(message);
	}
	
	/**
	 * Start a new subscription with the stratum server
	 * 
	 * @return ID of message
	 */
	public Integer startStratumSubscription() throws StratumException {
		JSONObject subscription = new JSONObject();
		Integer id = getId();
		try {
			subscription.put("id", id);
			subscription.put("method", "mining.subscribe");
			subscription.put("params", new JSONArray());
			send(subscription.toString());
		} catch (JSONException je) {
			throw new StratumException("Unexpected JSON exception during subscription",je);
		}
		return id;
	}
	
	/**
	 * Authorise a new worker with the stratum server
	 * 
	 * @param worker
	 * @param password
	 * @return ID of message
	 * @throws StratumException 
	 */
	public Integer authoriseWorker(String worker, String password) throws StratumException {
		JSONObject authorise = new JSONObject();
		Integer id = getId();
		try {
			authorise.put("id", id);
			authorise.put("method", "mining.authorize"); // sic
			JSONArray params = new JSONArray();
			params.put(worker);
			params.put(password);
			authorise.put("params", params);
			send(authorise.toString());
		} catch (JSONException je) {
			throw new StratumException("Unexpected JSON exception during authorise worker",je);
		}
		return id;
	}
	
	/**
	 * Submit a share to the stratum server
	 * 
	 * @param worker
	 * @param jobId
	 * @param extraNonce2
	 * @param ntime
	 * @param nonce
	 * @return ID of message
	 * @throws StratumException
	 */
	public Integer submitShare(String worker, String jobId, String extraNonce2, String ntime, 
			String nonce) throws StratumException {
		JSONObject submit = new JSONObject();
		Integer id = getId();
		try {
			JSONArray params = new JSONArray();
			submit.put("id", id);
			params.put(worker);
			params.put(jobId);
			params.put(extraNonce2);
			params.put(ntime);
			params.put(nonce);
			submit.put("params", params);
			send(submit.toString());
		} catch (JSONException je) {
			throw new StratumException("Unexpected JSON exception during submit.",je);
		}
		return id;
	
	}
	
	/**
	 * Get an ID for the message to send with.  Wraps back to zero if we run out of integers!
	 * @return
	 */
	private Integer getId() {
		mCounter++;
		if (mCounter == Integer.MAX_VALUE) {
			mCounter = 0;
		}
		return mCounter;
	}
}
