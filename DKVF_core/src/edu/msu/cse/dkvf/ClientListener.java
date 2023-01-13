package edu.msu.cse.dkvf;

import org.apache.logging.log4j.LogManager;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * The listener for incoming clients
 *
 */
public class ClientListener implements Runnable {
	/**
	 * The port to listen for clients
	 */
	int port;

	/**
	 * The protocol to run its client handler upon receiving a client message
	 */
	DKVFServer protocol;

	/**
	 * The logger to use by client listener
	 */
	protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ClientListener.class);

	/**
	 * Constructor for ClientListener
	 * @param port The port to listen for incoming clients
	 * @param protocol The Protocol object that is used to handle client requests
	 */
	public ClientListener(int port, DKVFServer protocol) {
		this.port = port;
		this.protocol = protocol;
	}

	@Override
	/**
	 * Listens for clients, and creates one thread for each client.
	 */
	public void run() {
		LOGGER.info("Start listening for clients at port: " + port);
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			try {
				LOGGER.fatal("Problem in creating server socket to accept clients at port={} toString: {} Message:\n\t{}",
						port, e.toString(), e.getMessage());
				if (serverSocket != null)
					serverSocket.close();
			} catch (Exception e1) {
				LOGGER.warn("Problem in closing serverSocket to accept clients at port={} toString: {} Message:\n\t{}",
						port, e1.toString(), e1.getMessage());
			}
			return;
		}

		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				LOGGER.debug("New client arrived.");
				protocol.incrementNumberOfClients();
				ClientHandler ch = new ClientHandler(clientSocket, protocol);
				Thread t = new Thread(ch);
				t.start();
			} catch (Exception e) {
				LOGGER.fatal("Problem in accepting new client socket at port={} toString: {} Message:\n\t{}",
						port, e.toString(), e.getMessage());
			}

		}
	}

}
