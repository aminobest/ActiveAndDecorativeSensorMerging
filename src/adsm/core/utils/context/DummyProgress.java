package adsm.core.utils.context;

import org.processmining.framework.plugin.Progress;

public class DummyProgress implements Progress {

	private int value;

	public DummyProgress() {
		value = 0;
	}

	public void cancel() {
	}

	public String getCaption() {
		return "";
	}

	public int getMaximum() {
		return 1;
	}

	public int getMinimum() {
		return 0;
	}

	public int getValue() {
		return value;
	}

	public void inc() {
		value = 1;
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isIndeterminate() {
		return false;
	}

	public void setCaption(String message) {
	}

	public void setIndeterminate(boolean makeIndeterminate) {
	}

	public void setMaximum(int value) {
	}

	public void setMinimum(int value) {
	}

	public void setValue(int value) {
	}
}