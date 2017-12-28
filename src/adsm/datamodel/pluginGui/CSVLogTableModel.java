package adsm.datamodel.pluginGui;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author Andrea Burattin
 */
public class CSVLogTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -3749870932834439988L;

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
