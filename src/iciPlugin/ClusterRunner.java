package iciPlugin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ClusterRunner {

	public static void main(String[] args) throws Exception {
		
		if (args.length != 4) {
			System.err.println("use java -jar file.jar FILENAME TIMESTAMP_COL ACT_COL CASEID_COL");
			System.exit(-1);
		}
		
		String csvFile = args[0];
		int timestampIndex = Integer.parseInt(args[1]);
		int activityIndex = Integer.parseInt(args[2]);
		int candidateCaseIdIndex = Integer.parseInt(args[3]);
		
		ByteArrayOutputStream pipeOut = new ByteArrayOutputStream();
		PrintStream old_out = System.out;
		System.setOut(new PrintStream(pipeOut));
		
		CaseIdentifier ci = new CaseIdentifier();
		ci.generateLogMetrics(csvFile, timestampIndex, activityIndex, candidateCaseIdIndex, old_out);
	}
}
