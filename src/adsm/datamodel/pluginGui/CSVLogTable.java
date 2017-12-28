package adsm.datamodel.pluginGui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.processmining.framework.util.ui.widgets.HeaderRenderer;
import org.processmining.framework.util.ui.widgets.WidgetColors;

/**
 * 
 * @author Andrea Burattin
 */
public class CSVLogTable extends JTable {

	private static final long serialVersionUID = 5936958134749152263L;

	public CSVLogTable(TableModel tableModel) {
		super(tableModel);
		
		setBackground(WidgetColors.COLOR_LIST_BG);
		setForeground(WidgetColors.COLOR_LIST_FG);
		setSelectionBackground(WidgetColors.COLOR_LIST_SELECTION_BG);
		setSelectionForeground(WidgetColors.COLOR_LIST_SELECTION_FG);
		
		getTableHeader().setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
		getTableHeader().setOpaque(false);
		getTableHeader().setForeground(WidgetColors.COLOR_LIST_SELECTION_FG);
		getTableHeader().setBorder(BorderFactory.createEmptyBorder());
		getTableHeader().setFont(getTableHeader().getFont().deriveFont(13f).deriveFont(Font.BOLD));
		getTableHeader().setAlignmentX(Component.CENTER_ALIGNMENT);
		getTableHeader().setDefaultRenderer(new HeaderRenderer());
		
		setShowHorizontalLines(true);
		setShowVerticalLines(true);
		setGridColor(WidgetColors.COLOR_ENCLOSURE_BG);
		setFont(getFont().deriveFont(Font.BOLD));
	}
}
