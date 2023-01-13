package edu.msu.cse.dkvf;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;

/**
 * Server handler class
 *
 */
public class ServerHandler<ServerMessage extends GeneratedMessageV3> implements Runnable {
	/**
	 * The protocol to run its server handler upon receiving a server message
	 */
	DKVFServer protocol;

	Socket clientSocket;
	protected static final Logger LOGGER = LogManager.getLogger(Storage.class);

	/**
	 * Calls the protocol {@link DKVFServer#handleServerMessage} upon receiving a server message.
	 */
	public ServerHandler(Socket clientSocket, DKVFServer protocol) {
		this.clientSocket = clientSocket;
		this.protocol = protocol;
	}

	@Override
	/**
	 * Handles server message. It calls the protocol
	 * {@link edu.msu.cse.dkvf.DKVFServer#handleServerMessage} upon
	 * receiving a server message.
	 */
	public void run() {
		try {
			while (!clientSocket.isClosed()) {
				ServerMessage sm = (ServerMessage) this.protocol.serverMessageParser.parseDelimitedFrom(clientSocket.getInputStream());
				if (sm == null)
					return;
				LOGGER.debug(MessageFormat.format("New server message arrived:\n{0}", sm.toString()));
				protocol.handleServerMessage(sm);
			}
		} catch (SocketException e) {
			LOGGER.info("Socket exception in server handler. Probably server has closed the socket.");
			protocol.decrementNumberOfServers();
		} catch (Exception e) {
			LOGGER.fatal(MessageFormat.format("Error in reading server message. toString: {0} Message:\n{1}",
					e.toString(), e.getMessage()));
		}

	}

}
