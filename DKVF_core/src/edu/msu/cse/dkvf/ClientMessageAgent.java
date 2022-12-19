package edu.msu.cse.dkvf;

import com.google.protobuf.GeneratedMessageV3;

import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * The class to facilitate the communicate with client for the server side of the
 * protocol
 *
 *
 */
public class ClientMessageAgent<ClientMessage extends GeneratedMessageV3, ClientReply extends GeneratedMessageV3> {
	ClientMessage cm;
	Logger LOGGER;
	OutputStream out;

	/**
	 * Constructor for ClientMEssageAgent
	 * @param cm The client message
	 * @param out The output stream for sending the reply to the client
	 * @param logger THe logger
	 */
	public ClientMessageAgent(ClientMessage cm, OutputStream out, Logger logger) {
		this.cm = cm;
		this.LOGGER = logger;
		this.out = out;
	}

	/**
	 * Gets the received client message.
	 * @return
	 * 			The received client message
	 */
	public ClientMessage getClientMessage() {
		return cm;
	}

	/**
	 * Sends response to the client message.
	 * @param cr
	 * 			The client reply to send to client.
	 */
	public void sendReply(ClientReply cr) {
		try {
			cr.writeDelimitedTo(out);
			LOGGER.finer(MessageFormat.format("Sent to client: \n Client message={0}\n Response= {1}", cm.toString(), cr.toString()));
		} catch (Exception e) {
			LOGGER.severe(MessageFormat.format("Problem in sending client response. toString={0}, Message={1}", e.toString(), e.getMessage()));
		}
	}
}
