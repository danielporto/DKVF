package edu.msu.cse.dkvf;


import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * The storage layer. Any driver written for a specific product must extend this
 * class to let the framework work with it.
 *
 *
 */
public abstract class Storage<Record extends GeneratedMessageV3> {
	protected final Class<Record> recordClass;
	protected final Parser<Record> recordParser;

	/**
	 * Logger used
	 */
	protected static final Logger LOGGER = LogManager.getLogger(Storage.class);

	/**
	 * The status of a storage operation.
	 */
	public enum StorageStatus {
		SUCCESS, FAILURE
	}

	public Storage(Class<Record> c) throws IllegalAccessException {
		this.recordClass = c;
		this.recordParser = (Parser<Record>) FieldUtils.readStaticField(this.recordClass, "PARSER", true);
	}

	/**
	 * Initializes the storage engine.
	 * @param parameters
	 * 			A map of properties required by the storage engine to initialized.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus init(HashMap<String, String> parameters);


	/**
	 * Inserts/updates a value for the given key.
	 * @param key
	 * 			The record key of the key to insert.
	 * @param value
	 * 			The value to insert to the record.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus insert(String key, Record value);

	/**
	 * Reads the value of a key.
	 *
	 * @param key
	 * 			The record key of the key to read.
	 * @param p
	 * 			The predicate that must be true for the value to be returned.
	 * @param result
	 * 			The value of the record. . Only the first element of the list will be used as the result value.
	 * 			If the list is empty, key does not exists.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus read(String key, Predicate<Record> p, List<Record> result);

	/**
	 * Runs the storage engine.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus run();

	/**
	 * Closes the storage engine.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus close ();
	/**
	 * Cleans the whole data.
	 * @return
	 * 			The result of the operation.
	 */
	public abstract StorageStatus clean();


}
