package edu.msu.cse.dkvf;


import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class manages the reliable FIFO delivery to servers.
 *
 */
public class ChannelManager<ServerMessage extends GeneratedMessageV3> implements Runnable {
	LinkedBlockingDeque<ServerMessage> deque = new LinkedBlockingDeque<>();
	boolean running = true;

	private Socket socket;
	private CodedOutputStream out;
	private String ip;
	private int port;
	private ServerMessage currentMessage;
	private int tryAgainWaitTime;
	protected static final Logger LOGGER = LogManager.getLogger(ChannelManager.class);
	
	/**
	 * Constructor for ChannelManager class
	 * @param ip The IP address of the destination
	 * @param port The port number of the destination
	 * @param tryAgainWaitTime The time before trying again in case of a failed delivery
	 * @param capacity The capacity of pending messages
	 */
	public ChannelManager(String ip, int port, int tryAgainWaitTime, int capacity) {
		this.ip = ip;
		this.port = port;
		this.deque = new LinkedBlockingDeque<>(capacity);
		this.tryAgainWaitTime = tryAgainWaitTime;
		Thread thread = new Thread(this);
		thread.start();
		
	}

	/**
	 * Connects to the destination
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(ip, port);
		out = CodedOutputStream.newInstance(socket.getOutputStream());
	}

	/**
	 * Starts sending messages to the destination
	 */
	public void run() {
		boolean connectionFailure = false;
		while (running) {
			try {
				if (connectionFailure){
					if (socket != null)
						socket.close();
					connectionFailure = false;
				}
				if (socket == null || socket.isClosed() || !socket.isConnected())
					connect();
				
				if (currentMessage == null)
					currentMessage = deque.takeFirst();
				LOGGER.debug(MessageFormat.format("Sending message to ip: {0}, port:{1}\n Message:\n{2}", ip, port, currentMessage.toString()));
				out.writeInt32NoTag(currentMessage.getSerializedSize());
				currentMessage.writeTo(out);
				out.flush();
				currentMessage = null;
			} catch (InterruptedException e) {
				// TODO log here: Interrupted while waiting for queue.
			}catch (UnknownHostException e){
				//just log and exit
				break;
			}catch (IOException e) {
				// TODO log here
				try {
					Thread.sleep(tryAgainWaitTime);
					connectionFailure = true;
				} catch (InterruptedException e1) {
					// TODO log here: Interrupted while waiting to try again. 
				}
			}
		}
	}

	/** 
	 * Adds a new server message to the queue. 
	 * @param sm The ServerMessage object to add to the queue. 
	 */
	public void addMessage(ServerMessage sm) {
		deque.addLast(sm);
	}

	/**
	 * Stops sending messages.
	 */
	public void stop() {
		running = false;
	}
}
