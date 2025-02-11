package edu.msu.cse.cops.client;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;

import edu.msu.cse.dkvf.DKVFClient;
import edu.msu.cse.dkvf.ServerConnector.NetworkStatus;
import edu.msu.cse.dkvf.Utils;
import edu.msu.cse.dkvf.config.ConfigReader;
import edu.msu.cse.dkvf.cops.metadata.Metadata;
import edu.msu.cse.dkvf.cops.metadata.Metadata.*;

public class COPSClient extends  DKVFClient{

	int dcId;
	int numOfPartitions;

	HashMap<String, Long> nearest;

	public COPSClient(ConfigReader cnfReader) throws IllegalAccessException {
		super(cnfReader, Metadata.Record.class, Metadata.ServerMessage.class, Metadata.ClientMessage.class, Metadata.ClientReply.class );
		HashMap<String, List<String>> protocolProperties = cnfReader.getProtocolProperties();
		numOfPartitions = new Integer(protocolProperties.get("num_of_partitions").get(0));
		dcId = new Integer(protocolProperties.get("dc_id").get(0));
		nearest = new HashMap<>();
	}

	@Override
	public boolean put(String key, byte[] value) {
		try {
			PutMessage pm = PutMessage.newBuilder().setKey(key).setValue(ByteString.copyFrom(value)).addAllNearest(getDcTimeItems()).build();
			ClientMessage cm = ClientMessage.newBuilder().setPutMessage(pm).build();
			int partition = findPartition(key);
			String serverId = dcId + "_" + partition;
			if (sendToServer(serverId, cm) == NetworkStatus.FAILURE)
				return false;
			ClientReply cr = (ClientReply) readFromServer(serverId);
			if (cr != null && cr.getStatus()) {
				nearest.clear();
				nearest.put(key, cr.getPutReply().getVersion());
				return true;
			} else {
				LOGGER.fatal("Server could not put the key= " + key);
				return false;
			}
		} catch (Exception e) {
			LOGGER.fatal(Utils.exceptionLogMessge("Failed to put due to exception", e));
			return false;
		}
	}

	@Override
	public byte[] get(String key) {
		try {
			GetMessage gm = GetMessage.newBuilder().setKey(key).build();
			ClientMessage cm = ClientMessage.newBuilder().setGetMessage(gm).build();
			int partition = findPartition(key);
			String serverId = dcId + "_" + partition;
			if (sendToServer(serverId, cm) == NetworkStatus.FAILURE)
				return null;
			ClientReply cr = (ClientReply) readFromServer(serverId);
			if (cr != null && cr.getStatus()) {
				updateNearest(key, cr.getGetReply().getRecord().getVersion());
				return cr.getGetReply().getRecord().getValue().toByteArray();
			} else {
				LOGGER.fatal("Server could not get the key= " + key);
				return null;
			}
		} catch (Exception e) {
			LOGGER.fatal(Utils.exceptionLogMessge("Failed to get due to exception", e));
			return null;
		}
	}

	private int findPartition(String key) throws NoSuchAlgorithmException {
		long hash = Utils.getMd5HashLong(key);
		return (int) (hash % numOfPartitions);
	}

	private List<Dependency> getDcTimeItems() {
		List<Dependency> result = new ArrayList<>();
		for (Map.Entry<String, Long> entry : nearest.entrySet()) {
			Dependency dep = Dependency.newBuilder().setKey(entry.getKey()).setVersion(entry.getValue()).build();
			result.add(dep);
		}
		return result;
	}

	private void updateNearest (String key, long version){
		if (nearest.containsKey(key)){
			nearest.put(key, Math.max(nearest.get(key), version));
		}else
			nearest.put(key, version);
	}

}
