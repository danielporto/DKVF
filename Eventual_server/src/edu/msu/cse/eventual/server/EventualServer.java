package edu.msu.cse.eventual.server;

import com.google.protobuf.GeneratedMessageV3;
import edu.msu.cse.dkvf.ClientMessageAgent;
import edu.msu.cse.dkvf.DKVFServer;
import edu.msu.cse.dkvf.Storage.StorageStatus;
import edu.msu.cse.dkvf.config.ConfigReader;
import edu.msu.cse.dkvf.eventual.metadata.Metadata;
import edu.msu.cse.dkvf.eventual.metadata.Metadata.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventualServer extends DKVFServer {
	ConfigReader cnfReader;

	int numOfDatacenters;
	int dcId;
	int pId;

	public EventualServer(ConfigReader cnfReader) throws IllegalAccessException {
		super(cnfReader, Metadata.Record.class, Metadata.ServerMessage.class, Metadata.ClientMessage.class, Metadata.ClientReply.class);
		this.cnfReader = cnfReader;
		HashMap<String, List<String>> protocolProperties = cnfReader.getProtocolProperties();
		numOfDatacenters = new Integer(protocolProperties.get("num_of_datacenters").get(0));
		dcId = new Integer(protocolProperties.get("dc_id").get(0));
		pId = new Integer(protocolProperties.get("p_id").get(0));
	}

	public void handleClientMessage(ClientMessageAgent cma) {
		Metadata.ClientMessage cmsg = (Metadata.ClientMessage) cma.getClientMessage();
		if (cmsg.hasGetMessage()) {
			handleGetMessage(cma);
		} else if (cmsg.hasPutMessage()) {
			handlePutMessage(cma);
		}
	}

	private void handlePutMessage(ClientMessageAgent cma) {
		Metadata.ClientMessage cmsg = (Metadata.ClientMessage) cma.getClientMessage();
		Record.Builder builder = Record.newBuilder();
		builder.setValue(cmsg.getPutMessage().getValue());
		builder.setUt(System.currentTimeMillis());
		Record rec = builder.build();
		StorageStatus ss = insert(cmsg.getPutMessage().getKey(), rec);

		ClientReply cr = null;

		if (ss == StorageStatus.SUCCESS) {
			cr = ClientReply.newBuilder().setStatus(true).setPutReply(PutReply.newBuilder().setUt(rec.getUt())).build();
		} else {
			cr = ClientReply.newBuilder().setStatus(false).build();
		}

		cma.sendReply(cr);
		sendReplicateMessages(cmsg.getPutMessage().getKey(), rec);
	}

	private void sendReplicateMessages(String key, Record recordToReplicate) {
		ServerMessage sm = ServerMessage.newBuilder().setReplicateMessage(ReplicateMessage.newBuilder().setKey(key).setRec(recordToReplicate)).build();
		for (int i = 0; i < numOfDatacenters; i++) {
			if (i == dcId)
				continue;
			String id = i + "_" + pId;

			protocolLOGGER.finer(MessageFormat.format("Sendng replicate message to {0}: {1}", id, sm.toString()));
			sendToServerViaChannel(id, sm);
		}
	}

	private void handleGetMessage(ClientMessageAgent cma) {
		Metadata.ClientMessage cmsg = (Metadata.ClientMessage) cma.getClientMessage();
		GetMessage gm = cmsg.getGetMessage();
		List<Record> result = new ArrayList<>();
		StorageStatus ss = read(gm.getKey(), p -> {
			return true;
		}, result);
		ClientReply cr = null;
		if (ss == StorageStatus.SUCCESS) {
			Record rec = result.get(0);
			cr = ClientReply.newBuilder().setStatus(true).setGetReply(GetReply.newBuilder().setValue(rec.getValue())).build();
		} else {
			cr = ClientReply.newBuilder().setStatus(false).build();
		}
		cma.sendReply(cr);
	}

	public void handleServerMessage(GeneratedMessageV3 sm) {
		System.out.println("EventualServer.handleServerMessage was called");
		ServerMessage smc = (ServerMessage) sm;
		Record newRecord = smc.getReplicateMessage().getRec();
		insert(smc.getReplicateMessage().getKey(), newRecord);
	}


}
