package edu.msu.cse.dkvf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import edu.msu.cse.dkvf.ServerConnector.NetworkStatus;
import edu.msu.cse.dkvf.Storage.StorageStatus;
import edu.msu.cse.dkvf.config.Config;
import edu.msu.cse.dkvf.config.ConfigReader;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.*;

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
	protected Map<String, CodedOutputStream> serversOut = new HashMap<>();

	/**
	 * The map of IDs of servers to their input streams.
	 */
	protected Map<String, CodedInputStream> serversIn = new HashMap<>();

	/**
	 * The map of IDs of servers to their socket objects.
	 */
	Map<String, Socket> sockets = new HashMap<>();

	/**
	 * The logger used by the protocol layer.
	 */
	public Logger protocolLOGGER;

	/**
	 * The logger used by the framework layer.
	 */
	protected Logger frameworkLOGGER;

	// Handlers
	/**
	 * File handler for the logger of the framework.
	 */
	FileHandler frameworkFh;

	/**
	 * Standard output handler for the logger of the framework.
	 */
	DualConsoleHandler frameworkDch;

	/**
	 * File handler for the logger of the protocol layer.
	 */
	FileHandler protocolFh;

	/**
	 * Standard output handler for the logger of the protocol layer.
	 */
	DualConsoleHandler protocolDch;

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
			storage.init(cnfReader.getStorageProperties(), frameworkLOGGER);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
				 InvocationTargetException e) {
			throw new RuntimeException("Problem in setting up the storage", e);
		}
	}

	/**
	 * Sets up the logging.
	 */
	private void setupLogging() {
		try {
			Logger l0 = Logger.getLogger("");
			synchronized (l0) {
				if (l0.getHandlers().length > 0)
					l0.removeHandler(l0.getHandlers()[0]);
			}
			Level frameworkStdLevel = Level.parse(cnf.getFrameworkStdLogLevel().toUpperCase());
			Level frameworkLevel = Level.parse(cnf.getFrameworkLogLevel().toUpperCase());
			Level minLevel = (frameworkLevel.intValue() < frameworkStdLevel.intValue()) ? frameworkLevel : frameworkStdLevel;

			frameworkLOGGER = Logger.getLogger("frameworkLogger");
			frameworkLOGGER.setLevel(minLevel);
			Utils.checkParentAndCreate(cnf.getFrameworkLogFile());
			frameworkFh = new FileHandler(cnf.getFrameworkLogFile());
			frameworkFh.setLevel(frameworkLevel);
			if (cnf.getFrameworkLogType().equals("text"))
				frameworkFh.setFormatter(new SimpleFormatter());
			else
				frameworkFh.setFormatter(new XMLFormatter());
			frameworkLOGGER.addHandler(frameworkFh);
			frameworkDch = new DualConsoleHandler(frameworkStdLevel, Level.INFO);
			frameworkLOGGER.addHandler(frameworkDch);

			Level protocolStdLevel = Level.parse(cnf.getProtocolStdLogLevel().toUpperCase());
			Level protocolLevel = Level.parse(cnf.getProtocolLogLevel().toUpperCase());
			Level minProtocolLevel = (protocolLevel.intValue() < protocolStdLevel.intValue()) ? protocolLevel : protocolStdLevel;
			protocolLOGGER = Logger.getLogger("protocolLogger");
			protocolLOGGER.setLevel(minProtocolLevel);

			Utils.checkParentAndCreate(cnf.getProtocolLogFile());
			protocolFh = new FileHandler(cnf.getProtocolLogFile());
			protocolFh.setLevel(protocolLevel);
			if (cnf.getProtocolLogType().equals("text"))
				protocolFh.setFormatter(new SimpleFormatter());
			else
				protocolFh.setFormatter(new XMLFormatter());
			protocolLOGGER.addHandler(protocolFh);
			protocolDch = new DualConsoleHandler(protocolStdLevel, Level.INFO);
			protocolLOGGER.addHandler(protocolDch);

		} catch (Exception e) {
			throw new RuntimeException("Problem in logging setup.", e);
		}
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
			frameworkLOGGER.severe("Trying to put while stable storage is not set.");
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
			frameworkLOGGER.severe("Trying to getFirst while stable storage is not set.");
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
		frameworkLOGGER.info("Preparing to turnoff");
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

			frameworkLOGGER.removeHandler(frameworkDch);
			frameworkLOGGER.removeHandler(frameworkFh);

			protocolLOGGER.removeHandler(protocolDch);
			protocolLOGGER.removeHandler(protocolFh);

			frameworkLOGGER.info("Sucessfully prepared for turn off.");
			return true;
		} catch (Exception e) {
			frameworkLOGGER.severe(MessageFormat.format("Problem closing streams. e.toString() = {0}", e.toString()));
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
			ServerConnector sv = new ServerConnector(cnfReader.getServerInfos(), serversOut, serversIn, sockets, new Integer(cnf.getConnectorSleepTime().trim()), frameworkLOGGER);
			Thread t = new Thread(sv);
			t.start();
			return NetworkStatus.SUCCESS;
		} catch (Exception e) {
			frameworkLOGGER.severe("Failed to run Server Connector" + " " + e.toString() + " Message: " + e.getMessage());
			return NetworkStatus.FAILURE;
		}
	}
}
