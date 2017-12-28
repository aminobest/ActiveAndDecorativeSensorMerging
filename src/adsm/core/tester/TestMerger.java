package adsm.core.tester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import adsm.core.plugin.CSVLog;
import adsm.core.utils.UnicodeBOMInputStream;
import adsm.core.utils.context.DummyGlobalContext;
import adsm.core.utils.context.DummyPluginContext;
import adsm.datamodel.merger.log;
import adsm.datamodel.pluginGui.FileParameters;
import adsm.datamodel.pluginGui.GlobalParameters;
import adsm.mergerAndMapper.Matcher;

public class TestMerger {

	public static void main(String[] args) throws Exception {

		DummyGlobalContext globalContext = new DummyGlobalContext();
		DummyPluginContext context = new DummyPluginContext(globalContext, "");

		/*
		 * Data population
		 */

		String folder = args[2];
		String out = args[3];

		String file1 = folder + "AmaSmartLog_clerkProcess.csv";
		String file2 = folder + "AmaSmartLog_robotProcess.csv";
		String file3 = folder + "AmaSmartLog_CLERK_RFID.csv";
		String file4 = folder + "AmaSmartLog_ROBOT_RFID.csv";
		String file5 = folder + "AmaSmartLog_accelerometer.csv";

		CSVLog[] csv = new CSVLog[] { read(file1), read(file2), read(file3), read(file4), read(file5) };

		ArrayList<FileParameters> files = new ArrayList<FileParameters>() {
			{
				for (CSVLog file : csv) {
					add(new FileParameters() {
						{
							setFilename(file.getName());
						}
					});
				}
			}
		};
		GlobalParameters gp = new GlobalParameters(files);
		gp.setMainlogfile(folder + "AmaSmartLog_clerkProcess.csv");
		gp.setSimilarityfunction(args[1]);

		for (FileParameters file : gp.getFiles()) {
			if (file.getFilename().equals(folder + "AmaSmartLog_clerkProcess.csv")) {
				file.setType("Event Log");
				file.setSelectionRatio(args[0]);
				file.setCaseIdIndex("0");
				file.setStarttimeIndex("1");
				file.setEndttimeIndex("2");

			}
			if (file.getFilename().equals(folder + "AmaSmartLog_robotProcess.csv")) {
				file.setType("Event Log");
				file.setSelectionRatio(args[0]);
				file.setCaseIdIndex("0");
				file.setStarttimeIndex("1");
				file.setEndttimeIndex("2");
			}
			if (file.getFilename().equals(folder + "AmaSmartLog_CLERK_RFID.csv")) {
				file.setType("Active Sensor Log");
				file.setSelectionRatio(args[0]);
				file.setStarttimeIndex("1");
			}
			if (file.getFilename().equals(folder + "AmaSmartLog_ROBOT_RFID.csv")) {
				file.setType("Active Sensor Log");
				file.setSelectionRatio(args[0]);
				file.setStarttimeIndex("1");
			}
			if (file.getFilename().equals(folder + "AmaSmartLog_accelerometer.csv")) {
				file.setType("Decorative Sensor Log");
				file.setMarking("shake");
				file.setDiscreteattr("0");
				file.setContinousattr("2");
			}

		}

		/*
		 * data merger
		 */

		CSVLog mergedlogcsv = new CSVLog("mergedlog");

		HashMap<String, HashMap<String, String>> attributesIndexes = new HashMap<String, HashMap<String, String>>() {
			{
				/// event id should be moved to the last col in simulator

				for (FileParameters f : gp.getFiles()) {

					if (f.getType().equals("Event Log")) {
						put(f.getFilename(), new HashMap<String, String>() {
							{
								put("type", "eventlog");
								int nlines = 1;
								for (CSVLog foundfile : csv) {
									if (foundfile.getName().equals(f.getFilename())) {
										nlines = foundfile.getEntries();
									}
								}

								double SelectionRatio = Double.valueOf(f.getSelectionRatio()) / (nlines);
								System.out.println(SelectionRatio);
								put("selectionRatio", SelectionRatio + "");

								put("caseid", f.getCaseIdIndex());
								put("starttime", f.getStarttimeIndex());
								put("endtime", f.getEndttimeIndex());
							}
						});
					} else if (f.getType().equals("Active Sensor Log")) {
						put(f.getFilename(), new HashMap<String, String>() {
							{
								put("type", "active");
								int nlines = 1;
								for (CSVLog foundfile : csv) {
									if (foundfile.getName().equals(f.getFilename())) {
										nlines = foundfile.getEntries();
									}
								}

								double SelectionRatio = Double.valueOf(f.getSelectionRatio()) / (nlines);
								System.out.println(SelectionRatio);
								put("selectionRatio", SelectionRatio + "");

								put("starttime", f.getStarttimeIndex());
								put("endtime", f.getEndttimeIndex());
							}
						});
					} else if (f.getType().equals("Decorative Sensor Log")) {
						put(f.getFilename(), new HashMap<String, String>() {
							{
								put("type", "decorative");
								put("discreteDimensions", f.getDiscreteattr());
								put("continiousDimensions", f.getContinousattr());
							}
						});
					}

				}
			}
		};

		Matcher Merger = new Matcher(1000000, attributesIndexes, gp.getSimilarityfunction());
		log mainlog = null;
		System.out.println("mainlog " + gp.getMainlogfile());
		for (CSVLog file : csv) {
			System.out.println("checking " + file.getName());
			if (file.getName().equals(gp.getMainlogfile())) {
				System.out.println("found");
				mainlog = Merger.processFile(file, "event");
			}
		}

		ArrayList<log> logs = new ArrayList<log>() {
			{
				for (String key : attributesIndexes.keySet()) {
					if (!key.equals(gp.getMainlogfile())
							&& !attributesIndexes.get(key).get("type").equals("decorative")) {
						for (CSVLog file : csv) {
							if (file.getName().equals(key)) {
								add(Merger.processFile(file, "event"));
							}
						}

					}
				}
			}
		};

		log mergedlog = Merger.Hmerger(mainlog, logs, gp.getSimilarityfunction()); // or JA

		for (String key : attributesIndexes.keySet()) {
			if (!key.equals(gp.getMainlogfile()) && attributesIndexes.get(key).get("type").equals("decorative")) {
				for (CSVLog file : csv) {
					if (file.getName().equals(key)) {
						log decorativelog = Merger.processFile(file, "decorative");

						for (FileParameters ff : gp.getFiles()) {
							if (ff.getFilename().equals(file.getName())) {
								mergedlog = Merger.mapDecorativeData(mergedlog, decorativelog, ff.getMarking(),
										mainlog.getLabel());
							}
						}

					}
				}

			}
		}

		mergedlogcsv = Merger.initNewLog(mergedlogcsv, mainlog.getLabel(), out);

		Merger.printNewLog(mergedlog, mergedlogcsv, mainlog.getLabel(), out);
		mergedlogcsv.prepare();

		System.out.println("Done");
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

}
