package adsm.core.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.ui.widgets.ProMScrollPane;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import adsm.core.utils.GUIUtils;
import adsm.datamodel.pluginGui.CSVLogTable;
import adsm.datamodel.pluginGui.CSVLogTableModel;
import csvImporter.FIELD_ROLE;
import csvImporter.FIELD_TYPE;

/**
 * 
 * @author Andrea Burattin
 */
public class CSVVisualizer extends JPanel {

	private static final long serialVersionUID = 8343715291246258244L;
	private CSVLog log;
	private CSVLogTableModel logTableModel;
	
	private JPanel tablePanel;
	private JPanel configurationPanel;
	private JPanel exportPanel;
	private JComboBox<FIELD_ROLE> fieldRole;
	private JComboBox<FIELD_TYPE> fieldType;
	private final JLabel warning = GUIUtils.prepareLabel("", SwingConstants.LEFT, Color.white);
	private final JButton exportLog = SlickerFactory.instance().createButton("Export log to workspace");;
	private final JProgressBar exportProgress = SlickerFactory.instance().createProgressBar(SwingConstants.HORIZONTAL);
	
	@Plugin(
		name = "CSV Visualizer",
		returnLabels = { "View" },
		returnTypes = { JComponent.class },
		parameterLabels = { "CSV Log" },
		userAccessible = true
	)
	@UITopiaVariant(
		affiliation = "Technical University of Denmark",
		author = "A. Burattin",
		email = "andbur@dtu.dk"
	)
	@Visualizer(name = "Smart CSV Visualizer")
	public JComponent visualize(final UIPluginContext context, final CSVLog log) {
		this.log = log;
		this.logTableModel = new CSVLogTableModel();
		this.fieldRole = new JComboBox<FIELD_ROLE>(FIELD_ROLE.values());
		this.fieldType = new JComboBox<FIELD_TYPE>(FIELD_TYPE.values());
		this.exportProgress.setVisible(false);
		
		this.exportLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						exportProgress.setVisible(true);
						exportLog.setVisible(false);
						if (!log.export(context, exportProgress)) {
							JOptionPane.showMessageDialog(CSVVisualizer.this,
								"You need to set these fields: activity name, case id and execution time.",
								"Fields Error!",
								JOptionPane.ERROR_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(CSVVisualizer.this,
									"Log exported into the workspace!",
									"Log Exported",
									JOptionPane.INFORMATION_MESSAGE);
						}
						exportLog.setVisible(true);
						exportProgress.setVisible(false);
					}
				}).start();
				
			}
		});
		
		updateWarninigLabel();
		
		// add headers
		if (log.hasHeaders()) {
			for(String head : log.getHeaders()) {
				logTableModel.addColumn(head);
			}
		} else {
			logTableModel.setColumnCount(log.fieldsNo());
		}
		
		// add rows
		int linesToAdd = Math.min(CSVLog.LINES_FOR_AUTO_DETECT, log.getEntries());
		for (int i = 0; i < linesToAdd; ++i) {
			List<String> line = log.getLine(i);
			logTableModel.addRow(line.toArray());
		}
		
		// build actual gui
		final JTable table = prepareJTable();
		JScrollPane tableContainer = new ProMScrollPane(table);
		
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setType(table.getSelectedColumn());
			}
		});
		
		SlickerDecorator.instance().decorate(fieldRole);
		fieldRole.setMinimumSize(new Dimension(300, 25));
		fieldRole.setPreferredSize(new Dimension(300, 25));
		fieldRole.setEnabled(false);
		fieldRole.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					FIELD_ROLE item = (FIELD_ROLE) event.getItem();
					int column = table.getSelectedColumn();
					log.setRole(column, item);
					
					if (item == FIELD_ROLE.OTHER) {
						fieldType.setEnabled(true);
					} else {
						fieldType.setEnabled(false);
					}
					
					updateWarninigLabel();
				}
			}
		});
		
		SlickerDecorator.instance().decorate(fieldType);
		fieldType.setMinimumSize(new Dimension(300, 25));
		fieldType.setPreferredSize(new Dimension(300, 25));
		fieldType.setEnabled(false);
		fieldType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					FIELD_TYPE item = (FIELD_TYPE) event.getItem();
					int column = table.getSelectedColumn();
					log.setType(column, item);
					
					updateWarninigLabel();
				}
			}
		});
		
		configurationPanel = SlickerFactory.instance().createRoundedPanel(15, WidgetColors.COLOR_ENCLOSURE_BG);
		configurationPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		configurationPanel.add(GUIUtils.prepareLabel("Selected field role:", SwingConstants.LEFT, Color.lightGray), c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		configurationPanel.add(fieldRole, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		configurationPanel.add(GUIUtils.prepareLabel("Selected field type:", SwingConstants.LEFT, Color.lightGray), c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		configurationPanel.add(fieldType, c);
		
		tablePanel = SlickerFactory.instance().createRoundedPanel(15, WidgetColors.COLOR_ENCLOSURE_BG);
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(tableContainer, BorderLayout.CENTER);
		
		
		exportPanel = SlickerFactory.instance().createRoundedPanel(15, WidgetColors.COLOR_ENCLOSURE_BG);
		exportPanel.setLayout(new GridBagLayout());
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		exportPanel.add(warning, c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		exportPanel.add(exportLog, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		exportPanel.add(exportProgress, c);
		
		setBackground(Color.GRAY);
		setLayout(new BorderLayout(0, 10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		add(configurationPanel, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(exportPanel, BorderLayout.SOUTH);
		
		return this;
	}
	
	private void setType(int column) {
		FIELD_ROLE currentRole = log.getRoles().get(column);
		FIELD_TYPE currentType = log.getTypes().get(column);
		
		fieldRole.setEnabled(true);
		fieldRole.setSelectedItem(currentRole);
		fieldType.setSelectedItem(currentType);
	}
	
	private JTable prepareJTable() {
		// build actual table
		CSVLogTable table = new CSVLogTable(logTableModel);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(false);
		
		// adjust columns width
		TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); ++column) {
			int width = 50; // Min width
			for (int row = 0; row < table.getRowCount(); ++row) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
		
		return table;
	}
	
	private void updateWarninigLabel() {
		String warning = "";
		for (FIELD_ROLE role : CSVLog.REQUIRED_FIELDS) {
			if (!log.getRoles().contains(role)) {
				warning += role.toString() + ", ";
			}
		}
		if (warning.length() > 0) {
			warning = "WARNING: You still have to identify the " + warning.substring(0, warning.length() - 2).toLowerCase() + "!";
			exportLog.setEnabled(false);
		} else {
			exportLog.setEnabled(true);
		}
		this.warning.setText(warning);
	}
}
