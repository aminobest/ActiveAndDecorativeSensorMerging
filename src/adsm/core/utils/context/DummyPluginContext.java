package adsm.core.utils.context;

import org.processmining.contexts.cli.CLIPluginContext;
import org.processmining.framework.plugin.GlobalContext;
import org.processmining.framework.plugin.impl.AbstractPluginContext;

public class DummyPluginContext extends CLIPluginContext {
	
	public DummyPluginContext(GlobalContext context, String label) {
		super(context, label);
		progress = new DummyProgress();
	}
	
	public DummyPluginContext(AbstractPluginContext context, String label) {
		super(context, label);
		progress = new DummyProgress();
	}
}