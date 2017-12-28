package csvImporter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import adsm.core.plugin.CSVLog;
import adsm.core.utils.UnicodeBOMInputStream;
import adsm.core.utils.Utils;

@Plugin(
	name = "Import Comma-Separated Values (.csv)",
	returnLabels = { "Imported log" },
	returnTypes = { CSVLog.class },
	parameterLabels = { "File" },
	userAccessible = true
)
@UIImportPlugin(
	description = "Smart Comma-Separated Values (.csv)",
	extensions = { "csv" }
)
public class CSVImport extends AbstractImportPlugin {

	@Override
	protected Object importFromStream(PluginContext context, InputStream input,
			String filename, long fileSizeInBytes) throws Exception {
		context.getFutureResult(0).setLabel(filename);
		return loadFromFile(filename, input);
	}

	private static CSVLog loadFromFile(String filename, InputStream input)
			throws Exception {
		Utils.l("Extracting CSV lines");
		CSVLog log = new CSVLog(filename);

		UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(input);
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

		Utils.l("Preparing CSV");
		log.prepare();

		return log;
	}
}
