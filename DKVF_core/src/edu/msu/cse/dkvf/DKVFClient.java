package edu.msu.cse.dkvf;


import com.google.protobuf.GeneratedMessageV3;
import edu.msu.cse.dkvf.ServerConnector.NetworkStatus;
import edu.msu.cse.dkvf.config.ConfigReader;

import java.text.MessageFormat;

/**
 * The base class for the client side of the protocol. Any protocol needs to
 * extends this class for the client side.
 *
 */
public abstract class DKVFClient<Record extends GeneratedMessageV3, ServerMessage extends GeneratedMessageV3, ClientMessage extends GeneratedMessageV3, ClientReply extends GeneratedMessageV3> extends DKVFBase<Record, ServerMessage, ClientMessage, ClientReply> {
	/**
	 * The abstract method for putting a value with the given key.
	 *
	 * @param key
	 *            The key of data to put
	 * @param value
	 *            The value to put
	 * @return The result of the operation. <br/>
	 *         <b>true</b> successful  <br/>
	 *         <b>false</b> unsuccessful
	 */
	public abstract boolean put(String key, byte[] value);

	/**
	 * The abstract method for getting a value with the given key.
	 *
	 * @param key
	 *            The key of data to put
	 * @return The value resulted for the give key. <br/>
	 *         <b>null</b> if the key does not exist.
	 */
	public abstract byte[] get(String key);

	public DKVFClient(ConfigReader cnfReader, Class<Record> r, Class<ServerMessage> sm, Class<ClientMessage> cm, Class<ClientReply> cr) throws IllegalAccessException {
		super(cnfReader, r, sm, cm, cr);
	}

	/**
	 * Runs server connector and storage.
	 *
	 * @return <b>true</b> if successful <br/>
	 *         <b>false</b> if unsuccessful
	 */
	public boolean runAll() {
		if (connectToServers() == NetworkStatus.SUCCESS){
			LOGGER.info("Sucessfully ran the server connector");

		}
		else
			return false;
		/* I comment here, for now. Later we need to add storage enable/disable in the XMLs
		 * and disable it for YCSB experiments, because client threads share same folder that
		 * creates problems for lock.


		if (runDb() == StorageStatus.SUCCESS)
			LOGGER.info("Sucessfully ran the stable storage");
		else
			return false;
			*/

		/*
		//debug
		try {

			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		return true;
	}

	/**
	 * Sends a client message to the server with the given ID.
	 *
	 * @param serverId
	 *            The ID of the destination server.
	 * @param cm
	 *            The client message to send
	 * @return The result of the operation
	 */
	public NetworkStatus sendToServer(String serverId, ClientMessage cm) {
		try {
			cm.writeDelimitedTo(serversOut.get(serverId));
			LOGGER.debug(MessageFormat.format("Sent to server with id= {0} \n{1}", serverId, cm.toString()));
			return NetworkStatus.SUCCESS;
		} catch (Exception e) {
			LOGGER.warn(Utils.exceptionLogMessge(MessageFormat.format("Problem in sending to server with Id= {0}" , serverId), e));
			return NetworkStatus.FAILURE;
		}
	}

	/**
	 * Reads from input stream of the server with the given ID.
	 *
	 * @param serverId
	 *            The ID of the server to read from its input stream.
	 * @return The received ClientReply message.
	 */
	public ClientReply readFromServer(String serverId) {
		try {
			ClientReply cr = (ClientReply) this.clientReplyParser.parseDelimitedFrom(serversIn.get(serverId));
			LOGGER.debug(MessageFormat.format("READ from server with id= {0} \n{1}", serverId, cr.toString()));
			return cr;
		} catch (Exception e) {
			//debug
			LOGGER.warn("serversIn= " + serverId + " serversIn.get(serverId) = " + serversIn.get(serverId) + " serversOut.get(serverId)= " + serversOut.get(serverId));
			LOGGER.warn(Utils.exceptionLogMessge(MessageFormat.format("Problem in reading response from server with Id= {0}" , serverId), e));
			return null;
		}
	}

}
