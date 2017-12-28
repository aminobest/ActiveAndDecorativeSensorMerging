package adsm.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.GlobalContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import adsm.core.utils.context.DummyGlobalContext;
import adsm.core.utils.context.DummyPluginContext;
import adsm.datamodel.merger.log;
import adsm.datamodel.pluginGui.FileParameters;
import adsm.datamodel.pluginGui.GlobalParameters;
import adsm.mergerAndMapper.Matcher;

public class ADSMplugin {
	private GlobalContext context = new DummyGlobalContext();
	private PluginContext pluginContext = new DummyPluginContext(context, "");

	@Plugin(name = "Active and Decorative Sensor Merging (ADSM)", parameterLabels = { "CSV file" }, returnLabels = {
			"Merged Event Log" }, returnTypes = { CSVLog.class }, userAccessible = true)
	@UITopiaVariant(affiliation = "Technical University of Denmark", author = "Amine A. Andaloussi", email = "amab@dtu.dk")

	public CSVLog callMerger(UIPluginContext context, CSVLog[] csv) throws Exception {

		CSVLog mergedlogcsv = new CSVLog("mergedlog");

		ArrayList<FileParameters> files = new ArrayList<FileParameters>() {
			{

				for (CSVLog file : csv) {
					add(new FileParameters() {
						{
							setFilename(file.getName());
							setCsvfile(file);
						}
					});
				}

			}
		};

		GlobalParameters gp = new GlobalParameters(files);

		ParametersPrompt parameters = new ParametersPrompt(gp, context);

		InteractionResult result = context.showConfiguration("Merging and Mapping Parameters", parameters);
		if (result.equals(InteractionResult.CANCEL)) {
			context.getFutureResult(0).cancel(true);
		}
		if (result.equals(InteractionResult.CONTINUE)) {

			/*
			 * test output
			 */

			System.out.println("mainlog " + gp.getMainlogfile());
			System.out.println("similarity function " + gp.getSimilarityfunction());

			for (FileParameters f : gp.getFiles()) {
				System.out.println("--- new file");
				System.out.println("file " + f.getFilename());
				System.out.println("file " + f.getType());

				// "Event Log", "Active Sensor Log", "Decorative Sensor Log"
				if (f.getType().equals("Event Log") || f.getType().equals("Active Sensor Log")) {
					System.out.println("case id " + f.getCaseIdIndex());
					System.out.println("starttime " + f.getStarttimeIndex());
					System.out.println("endtime " + f.getEndttimeIndex());
				} else if (f.getType().equals("Decorative Sensor Log")) {
					System.out.println("discrete dim " + f.getDiscreteattr());
					System.out.println("contunious dim  " + f.getContinousattr());
				}
			}

			HashMap<String, HashMap<String, String>> attributesIndexes = new HashMap<String, HashMap<String, String>>() {
				{

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

			mergedlogcsv = Merger.initNewLog(mergedlogcsv, mainlog.getLabel(), "test");

			Merger.printNewLog(mergedlog, mergedlogcsv, mainlog.getLabel(), "test");
			mergedlogcsv.prepare();

		}

		return mergedlogcsv;
	}
}
