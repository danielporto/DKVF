package edu.msu.cse.dkvf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.GeneratedMessageV3;

import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.logging.Logger;

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
	Logger LOGGER;

	/** 
	 * Calls the protocol {@link DKVFServer#handleServerMessage} upon receiving a server message. 
	 */
	public ServerHandler(Socket clientSocket, DKVFServer protocol, Logger logger) {
		LOGGER = logger;
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
			CodedInputStream in = CodedInputStream.newInstance(clientSocket.getInputStream());
			while (!clientSocket.isClosed()) {
				int size = in.readInt32();
				byte[] newMessageBytes = in.readRawBytes(size);
				ServerMessage sm = (ServerMessage) this.protocol.serverMessageParser.parseFrom(newMessageBytes);
				if (sm == null)
					return;
				LOGGER.finest(MessageFormat.format("New server message arrived:\n{0}", sm.toString()));
				protocol.handleServerMessage(sm);
			}
		} catch (SocketException e) {
			LOGGER.info("Socket exception in server handler. Probably server has closed the socket.");
			protocol.decrementNumberOfServers();
		} catch (Exception e) {
			LOGGER.severe(MessageFormat.format("Error in reading server message. toString: {0} Message:\n{1}",
					e.toString(), e.getMessage()));
		}

	}

}
