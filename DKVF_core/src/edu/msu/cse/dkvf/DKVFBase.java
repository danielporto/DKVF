package edu.msu.cse.dkvf;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import edu.msu.cse.dkvf.ServerConnector.NetworkStatus;
import edu.msu.cse.dkvf.Storage.StorageStatus;
import edu.msu.cse.dkvf.config.Config;
import edu.msu.cse.dkvf.config.ConfigReader;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The base class that is used by both {@link DKVFServer} and
 * {@link DKVFClient}. This class sets up the underlying storage and network
 * services.
 *
 */
public abstract class DKVFBase<Record extends GeneratedMessageV3, ServerMessage extends GeneratedMessageV3, ClientMessage extends GeneratedMessageV3, ClientReply extends GeneratedMessageV3> {

	protected final Class<ServerMessage> serverMessageClass;
	protected final Parser<ServerMessage> serverMessageParser;
	protected final Class<ServerMessage> recordClass;
	protected final Parser<Record> recordParser;
	protected final Class<ClientMessage> clientMessageClass;
	protected final Parser<ClientMessage> clientMessageParser;
	protected final Class<ClientReply> clientReplyClass;
	protected final Parser<ClientReply> clientReplyParser;

	/**
	 * Storage driver
	 */
	Storage storage;

	/**
	 * Configuration reader
	 */
	ConfigReader cnfReader;

	/**
	 * Configuration
	 */
	Config cnf;

	/**
	 * The map of IDs of servers to their output streams.
	 */
	protected Map<String, OutputStream> serversOut = new HashMap<String, java.io.OutputStream>();

	/**
	 * The map of IDs of servers to their input streams.
	 */
	protected Map<String, InputStream> serversIn = new HashMap<String, java.io.InputStream>();

	/**
	 * The map of IDs of servers to their socket objects.
	 */
	Map<String, Socket> sockets = new HashMap<>();

	/**
	 * Logger used
	 */
	public static final Logger LOGGER = LogManager.getLogger(DKVFBase.class);

	/**
	 * The constructor for DKVFBass class
	 * @param cnfReader
	 *            The configuration reader.
	 */
	public DKVFBase(ConfigReader cnfReader, Class<Record> r, Class<ServerMessage> sm, Class<ClientMessage> cm, Class<ClientReply> cr) throws IllegalAccessException {
		this.cnfReader = cnfReader;
		this.cnf = cnfReader.getConfig();
		this.recordClass = sm;
		this.recordParser = (Parser<Record>) FieldUtils.readStaticField(this.recordClass, "PARSER", true);
		this.serverMessageClass = sm;
		this.serverMessageParser = (Parser<ServerMessage>) FieldUtils.readStaticField(this.serverMessageClass, "PARSER", true);
		this.clientMessageClass = cm;
		this.clientMessageParser = (Parser<ClientMessage>) FieldUtils.readStaticField(this.clientMessageClass, "PARSER", true);
		this.clientReplyClass = cr;
		this.clientReplyParser = (Parser<ClientReply>) FieldUtils.readStaticField(this.clientReplyClass, "PARSER", true);
		setupLogging();
		setupStorage();
	}

	/**
	 * Sets up the storage engine.
	 */
	private void setupStorage() {
		try {
			Class<?> storageClass = Class.forName(cnf.getStorage().getClassName().trim());
			storage = (Storage) storageClass.getDeclaredConstructor(Class.class).newInstance(this.recordClass);
			storage.init(cnfReader.getStorageProperties());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
				 InvocationTargetException e) {
			throw new RuntimeException("Problem in setting up the storage", e);
		}
	}

	/**
	 * Sets up the logging.
	 */
	private void setupLogging() {

	}



	// DB management
	/**
	 * Closes the db.
	 *
	 * @return The result of the operation.
	 */
	public StorageStatus closeDb() {
		return storage.close();
	}

	/**
	 * Runs the storage.
	 *
	 * @return The result of the operation.
	 */
	public StorageStatus runDb() {
		return storage.run();
	}

	/**
	 * Cleans the storage data.
	 *
	 * @return The result of the operation.
	 */
	public StorageStatus cleanDb() {
		return storage.clean();
	}

	/**
	 * Inserts the given record for the given key.
	 *
	 * @param key
	 *            The key of the record to insert.
	 * @param value
	 *            The value to insert for the record.
	 * @return The result of the operation.
	 */
	public StorageStatus insert(String key, Record value) {
		if (storage == null) {
			LOGGER.fatal("Trying to put while stable storage is not set.");
			return StorageStatus.FAILURE;
		}
		return storage.insert(key, value);
	}

	/**
	 * Reads the value of the record with the given key that satisfies the given
	 * predicate.
	 *
	 * @param key
	 *            The key of the record to read.
	 * @param p
	 *            The predicate to check for the value.
	 * @param result
	 *            The value of the record. . Only the first element of the list
	 *            will be used as the result value. If the list is empty, key
	 *            does not exists.
	 * @return The result of the operation.
	 */
	public StorageStatus read(String key, Predicate<Record> p, List<Record> result) {
		if (storage == null) {
			LOGGER.fatal("Trying to getFirst while stable storage is not set.");
			return StorageStatus.FAILURE;
		}

		List<Record> resultByte = new ArrayList<Record>();
		StorageStatus status = storage.read(key, p, resultByte);
		if (status != StorageStatus.SUCCESS)
			return status;
		for (Record rec : resultByte) {
			result.add(rec);
		}
		return StorageStatus.SUCCESS;

	}

	/**
	 * Gets the storage driver object. It will be used whenever we want to call a
	 * method of the storage driver that is not provided by the DKVFBase.
	 *
	 * @return The storage driver object.
	 */
	public Storage getStorage() {
		return storage;
	}

	/**
	 * Prepares the server to turn off.
	 *
	 * @return true if successful. false if successful.
	 */
	public boolean prepareToTurnOff() {
		// We may need add more things to do here.
		LOGGER.info("Preparing to turnoff");
		if (closeDb() != StorageStatus.SUCCESS)
			return false;
		try {
			/*
			for (InputStream is : serversIn.values())
				is.close();
			for (OutputStream os : serversOut.values())
				os.close();
				*/
			for (Socket s : sockets.values())
				s.close();

			LOGGER.info("Sucessfully prepared for turn off.");
			return true;
		} catch (Exception e) {
			LOGGER.fatal(MessageFormat.format("Problem closing streams. e.toString() = {0}", e.toString()));
			return false;
		}
	}

	/**
	 * Runs the server connector thread.
	 *
	 * @return The result of the operation.
	 */
	public NetworkStatus connectToServers() {
		try {
			ServerConnector sv = new ServerConnector(cnfReader.getServerInfos(), serversOut, serversIn, sockets, new Integer(cnf.getConnectorSleepTime().trim()));
			Thread t = new Thread(sv);
			t.start();
			return NetworkStatus.SUCCESS;
		} catch (Exception e) {
			LOGGER.fatal("Failed to run Server Connector {} Message: {}", e.toString(), e.getMessage());
			return NetworkStatus.FAILURE;
		}
	}
}
