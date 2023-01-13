package edu.msu.cse.dkvf;


import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

/**
 * The handler for incoming clients
 *
 */
public class ClientHandler<ClientMessage extends GeneratedMessageV3> implements Runnable {/**
	 * The protocol to run its client handler upon receiving a client message.
	 */
	DKVFServer protocol;

	Socket clientSocket;
	protected static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
	/**
	 * Constructor for ClientHandler.
	 * @param clientSocket The Socket object of the client
	 * @param protocol The Protocol object that is used to handle client requests
	 * @param logger The logger
	 */
	public ClientHandler(Socket clientSocket, DKVFServer protocol) {
		this.clientSocket = clientSocket;
		this.protocol = protocol;
	}

	/**
	 * Handles client message. It calls the protocol
	 * {@link edu.msu.cse.dkvf.DKVFServer#handleClientMessage} upon
	 * receiving a client message.
	 */
	public void run() {
		try {
			LOGGER.info("Waiting on client messages...");
			while (true) {
				ClientMessage cm = (ClientMessage) this.protocol.clientMessageParser.parseDelimitedFrom(clientSocket.getInputStream());
				if (cm == null) {
					LOGGER.info("Null message from client");
					protocol.decrementNumberOfClients();
					return;
				}
				LOGGER.debug("New client message arrived:\n\t{}", cm.toString());
				ClientMessageAgent cma = new ClientMessageAgent(cm, clientSocket.getOutputStream());
				protocol.handleClientMessage(cma);
			}
		} catch (Exception e) {
			LOGGER.fatal(Utils.exceptionLogMessge("Error in reading client message. toString: {0} Message:\n{1}", e));
			protocol.decrementNumberOfClients();
		}
	}
}
