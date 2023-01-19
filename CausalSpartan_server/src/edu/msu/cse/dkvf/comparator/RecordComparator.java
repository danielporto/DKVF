package edu.msu.cse.dkvf.comparator;

import java.io.Serializable;
import java.util.Comparator;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.msu.cse.dkvf.causalspartan.metadata.Metadata.Record;

public class RecordComparator implements Comparator<byte[]>, Serializable{

	public int compare(byte[] b1, byte[] b2) {

		Record record1;
		Record record2;
		try {
			record1 = Record.parseFrom(b1);
			record2 = Record.parseFrom(b2);
			// we want to put records with higher ut first:
			if (record1.getUt() > record2.getUt())
				return -1;
			else if (record1.getUt() == record2.getUt()) {
				// if timestamps are equal we give priority to the version with
				// higher dc number.
				if (record1.getSr() > record2.getSr())
					return -1;
			}
			return 1;
		} catch (InvalidProtocolBufferException e) {
			System.err.println("Invalid byte[] to parse records inside comparator.");
			e.printStackTrace();
		}
		return -1;
	}
}