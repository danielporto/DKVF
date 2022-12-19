package edu.msu.cse.dkvf;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.logging.log4j.LogManager;

import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * The class to facilitate the communicate with client for the server side of the
 * protocol
 *
 *
 */
public class ClientMessageAgent<ClientMessage extends GeneratedMessageV3, ClientReply extends GeneratedMessageV3> {
	ClientMessage cm;
	protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ClientMessageAgent.class);
	OutputStream out;

	/**
	 * Constructor for ClientMEssageAgent
	 * @param cm The client message
	 * @param out The output stream for sending the reply to the client
	 */
	public ClientMessageAgent(ClientMessage cm, OutputStream out) {
		this.cm = cm;
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
			LOGGER.debug(MessageFormat.format("Sent to client: \n Client message={0}\n Response= {1}", cm.toString(), cr.toString()));
		} catch (Exception e) {
			LOGGER.fatal(MessageFormat.format("Problem in sending client response. toString={0}, Message={1}", e.toString(), e.getMessage()));
		}
	}
}
