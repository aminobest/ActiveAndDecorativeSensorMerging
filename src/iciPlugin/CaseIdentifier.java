package iciPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.GlobalContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMa;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMc;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMfa;
import org.processmining.plugins.InductiveMiner.plugins.IM;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.fitness.TreeFitnessAbstract;
import org.processmining.plugins.etm.fitness.metrics.FitnessReplay;
import org.processmining.plugins.etm.fitness.metrics.Generalization;
import org.processmining.plugins.etm.fitness.metrics.PrecisionEscEdgesImproved;
import org.processmining.plugins.etm.fitness.metrics.SimplicityMixed;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.ProcessTreeToNAryTree;
import org.processmining.processtree.ProcessTree;

import adsm.core.plugin.CSVLog;
import adsm.core.utils.UnicodeBOMInputStream;
import adsm.core.utils.context.DummyGlobalContext;
import adsm.core.utils.context.DummyPluginContext;
import csvImporter.FIELD_ROLE;
import nl.tue.astar.AStarThread.Canceller;

public class CaseIdentifier {

	private GlobalContext context = new DummyGlobalContext();
	private PluginContext pluginContext = new DummyPluginContext(context, "");

	public HashMap<String, Double> generateLogMetricsFromCSV(CSVLog csv, int timestampIndex, int activityIndex,
			int candidateCaseIdIndex) throws Exception {

		List<String> colsname = csv.getHeaders();

		String alg = "imfa";

		Map<TreeFitnessAbstract, Double> v = getValues(csv, timestampIndex, activityIndex, candidateCaseIdIndex, alg);

		HashMap<String, Double> metricsTEMP = new HashMap<String, Double>();

		for (TreeFitnessAbstract tfa : v.keySet()) {

			metricsTEMP.put(tfa.getInfo().toString(), v.get(tfa));
		}
		metricsTEMP.put("gpratio", GroupingRate.GetGroupingScore(csv, candidateCaseIdIndex).doubleValue());

		return metricsTEMP;

	}

	public int InferCaseIdFromLog(CSVLog csv, int timestampIndex, int activityIndex,
			ArrayList<Integer> candidateCaseIdIndexes) throws Exception {

		HashMap<Integer, HashMap<String, Double>> data = new HashMap<Integer, HashMap<String, Double>>();

		double totalratios = 0;

		for (int candidateCaseIdIndex : candidateCaseIdIndexes) {
			System.out.println("calculating quality metrics for candidate case: " + candidateCaseIdIndex);
			HashMap<String, Double> entry = generateLogMetricsFromCSV(csv, timestampIndex, activityIndex,
					candidateCaseIdIndex);
			totalratios += entry.get("gpratio");
			data.put(candidateCaseIdIndex, entry);
		}

		double averageRatio = totalratios / candidateCaseIdIndexes.size();

		int caseidIndex = -1;
		double highestQualityScore = 0;

		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();

			Integer condidateCaseIdIndex = (Integer) pair.getKey();
			HashMap<String, Double> entry = (HashMap<String, Double>) pair.getValue();

			double QualityScore = (entry.get("Fr") + entry.get("Pi") + entry.get("Sm") + entry.get("Gv")
					+ (1 - Math.abs(entry.get("gpratio") - averageRatio))) / 5;

			if (QualityScore > highestQualityScore) {
				highestQualityScore = QualityScore;
				caseidIndex = condidateCaseIdIndex;
			}

		}

		return caseidIndex;
	}

	public void generateLogMetrics(String CSVFile, int timestampIndex, int activityIndex, int candidateCaseIdIndex,
			PrintStream out) throws Exception {

		CSVLog csv = read(CSVFile);
		ArrayList<String> colsname = getCandidateColumnsName(CSVFile);

		String[] algs = new String[] { "imfa"/* ,"ima","imf","im","imc" */ }; // ,"imc" "imfa","ima","imf","im"

		for (String alg : algs) {
			Map<TreeFitnessAbstract, Double> v = getValues(csv, timestampIndex, activityIndex, candidateCaseIdIndex,
					alg);

			HashMap<String, Double> metricsTEMP = new HashMap<String, Double>();

			for (TreeFitnessAbstract tfa : v.keySet()) {
				metricsTEMP.put(tfa.getInfo().toString(), v.get(tfa));
			}

			out.print("\"" + alg + "\"\t");
			out.print("\"" + CSVFile + "\"\t");
			out.print("\"" + colsname.get(candidateCaseIdIndex) + "\"\t");
			for (String m : new String[] { "Fr", "Pi", "Sm", "Gv" }) {
				out.print("\"" + m + "\"\t");
				out.print(metricsTEMP.get(m) + "\t");
			}
			out.println("");

		}
	}

	public ArrayList<String> getCandidateColumnsName(String filename) throws IOException {

		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		line = bufferedReader.readLine().replace("\"", "");
		StringTokenizer st = new StringTokenizer(line, ",");
		ArrayList<String> colsname = new ArrayList<String>();

		while (st.hasMoreElements()) {
			colsname.add(st.nextToken());
		}

		fileReader.close();

		return colsname;

	}

	public static CSVLog read(String file) throws Exception {
		CSVLog log = new CSVLog(file);
		UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(new FileInputStream(file));
		ubis.skipBOM();
		InputStreamReader isr = new InputStreamReader(ubis);
		BufferedReader reader = new BufferedReader(isr);
		String readLine;
		while ((readLine = reader.readLine()) != null) {
			log.addLine(readLine);
		}
		reader.close();
		isr.close();
		ubis.close();
		log.prepare();
		return log;
	}

	private Map<TreeFitnessAbstract, Double> getValues(CSVLog csv, int time, int activityName, int caseId, String alg) {

		XLog log = getXLog(csv, time, activityName, caseId);
		// {"im","ima","imf","imfa","imc"}
		ProcessTree pt = null;
		if (alg.equals("im"))
			pt = this.doInductiveMinerIM(log);
		else if (alg.equals("ima"))
			pt = this.doInductiveMinerIMa(log);
		else if (alg.equals("imf"))
			pt = this.doInductiveMinerIMf(log);
		else if (alg.equals("imfa"))
			pt = this.doInductiveMinerIMfa(log);
		else if (alg.equals("imc"))
			pt = this.doInductiveMinerIMc(log);

		CentralRegistry registry = new CentralRegistry(log, new XEventNameClassifier(), new Random(1));
		ProcessTreeToNAryTree converter = new ProcessTreeToNAryTree(registry.getEventClasses());
		NAryTree tree = converter.convert(pt);

		List<TreeFitnessAbstract> evals = new ArrayList<TreeFitnessAbstract>();
		evals.add(new FitnessReplay(registry, new DummyCanceler())); // what is DummyCanceler ?

		//		evals.add(new PrecisionEscEdges(registry));
		evals.add(new PrecisionEscEdgesImproved(registry));
		//		evals.add(new PrecisionFlowerSize());
		//		evals.add(new PrecisionOperatorCosts());
		//		evals.add(new SimplicityDuplMissingAct(registry));
		evals.add(new SimplicityMixed());
		//		evals.add(new SimplicityOperatorAlternation());
		//		evals.add(new SimplicityTreeSize());
		//		evals.add(new SimplicityUselessNodes());
		evals.add(new Generalization(registry));
		//		evals.add(new GeneralizationByFitnessReplayDeviation(registry));

		Map<TreeFitnessAbstract, Double> values = new HashMap<TreeFitnessAbstract, Double>();

		for (TreeFitnessAbstract tfa : evals) {

			values.put(tfa, tfa.getFitness(tree, Arrays.asList(tree)));

			System.gc();
		}

		return values;
	}

	private ProcessTree doInductiveMinerIM(XLog log) {
		IM im = new IM();
		MiningParametersIM para = new MiningParametersIM();
		return im.mineProcessTree(pluginContext, log, para);
	}

	private ProcessTree doInductiveMinerIMf(XLog log) {
		IM im = new IM();
		MiningParametersIMf para = new MiningParametersIMf();

		return im.mineProcessTree(pluginContext, log, para);

	}

	private ProcessTree doInductiveMinerIMfa(XLog log) {
		IM im = new IM();
		MiningParametersIMfa para = new MiningParametersIMfa();

		return im.mineProcessTree(pluginContext, log, para);

	}

	private ProcessTree doInductiveMinerIMa(XLog log) {
		IM im = new IM();
		MiningParametersIMa para = new MiningParametersIMa();

		return im.mineProcessTree(pluginContext, log, para);

	}

	private ProcessTree doInductiveMinerIMc(XLog log) {
		IM im = new IM();
		MiningParametersIMc para = new MiningParametersIMc();

		return im.mineProcessTree(pluginContext, log, para);

	}

	private XLog getXLog(CSVLog csv, int time, int activityName, int caseId) {
		csv.setRole(time, FIELD_ROLE.DATE);
		csv.setRole(activityName, FIELD_ROLE.ACTIVITY_NAME);
		csv.setRole(caseId, FIELD_ROLE.CASE_ID);
		return csv.toXLog(null);
	}
}

class DummyCanceler implements Canceller {
	public boolean isCancelled() {
		return false;
	}
}
