package adsm.core.utils.context;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.AbstractGlobalContext;

public class DummyGlobalContext extends AbstractGlobalContext {

	private final PluginContext mainPluginContext;
	
	public DummyGlobalContext() {
		super();
		mainPluginContext = new DummyPluginContext(this, "Main Plugin Context");
	}
	
	protected PluginContext getMainPluginContext() {
		return mainPluginContext;
	}

	public Class<? extends PluginContext> getPluginContextType() {
		return DummyPluginContext.class;
	}
	
}