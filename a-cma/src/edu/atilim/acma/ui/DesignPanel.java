package edu.atilim.acma.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import edu.atilim.acma.RunConfig;
import edu.atilim.acma.design.Design;
import edu.atilim.acma.design.Type;
import edu.atilim.acma.metrics.MetricCalculator;
import edu.atilim.acma.metrics.MetricSummary;
import edu.atilim.acma.metrics.MetricTable;
import edu.atilim.acma.transition.TransitionManager;
import edu.atilim.acma.transition.actions.Action;
import edu.atilim.acma.ui.MainWindow.WindowEventListener;
import edu.atilim.acma.ui.design.DesignPanelBase;
import edu.atilim.acma.util.ACMAUtil;
import edu.atilim.acma.util.Log;

public class DesignPanel extends DesignPanelBase implements WindowEventListener {
	private static final long serialVersionUID = 1L;

	private Design design;
	private DesignData designData;
	
	DesignPanel(Design design) {
		this.design = design;
		this.designData = new DesignData();
		
		MainWindow.getInstance().addEventListener(this);
		
		initPossibleActions();
		initMetrics();
		initConfigSelector();
		
		validateDesignData();
	}
	
	private void initPossibleActions() {
		btnPosActionsRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				posActionsList.validate();
			}
		});
		
		btnPosActionsChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				HashMap<String, Integer> actionMap = new HashMap<String, Integer>();
				for (Action act : designData.getActions()) {
					String type = act.getClass().getEnclosingClass().getSimpleName();
					
					if (actionMap.containsKey(type))
						actionMap.put(type, actionMap.get(type) + 1);
					else
						actionMap.put(type, 1);
				}
				
				DefaultPieDataset dataset = new DefaultPieDataset();
				for (Entry<String, Integer> e : actionMap.entrySet()) {
					dataset.setValue(ACMAUtil.splitCamelCase(e.getKey()), e.getValue());
				}
				
				JFreeChart chart = ChartFactory.createPieChart3D("Action Distribution", dataset, true, false, false);
				chart.setBackgroundPaint(new Color(255, 255, 255, 0));
				
				PiePlot plot = (PiePlot)chart.getPlot();
				plot.setBackgroundPaint(new Color(255, 255, 255, 0));
				plot.setOutlineVisible(false);
				plot.setForegroundAlpha(0.75f);
				ChartPanel panel = new ChartPanel(chart);
				panel.setOpaque(false);
				PanelDialog.display(panel, 700, 470);
			}
		});
	}
	
	private void initMetrics() {
		metricTable.setDefaultRenderer(Object.class, new MetricTableRenderer());
		
		metricTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						drawMetricsChart();
					}
				});
			}
		});
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "CSV File";
					}
					
					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) return true;
						return f.getName().endsWith(".csv");
					}
				});
				fc.showSaveDialog(DesignPanel.this);

				File out = fc.getSelectedFile();
				
				if (out == null) return;
				
				if (out.exists()) {
					int res = JOptionPane.showConfirmDialog(DesignPanel.this, "This file already exists.\nRewrite?", "File exists", JOptionPane.OK_CANCEL_OPTION);
					if (res == JOptionPane.CANCEL_OPTION) return;
				}
				
				Log.info("Saving metrics to %s", out.getAbsolutePath());
				
				try {
					designData.getTable().writeCSV(out.getAbsolutePath());
				} catch (IOException e1) {
					Log.severe("Error saving metrics");
				}
			}
		});
		
		btnPreset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = JOptionPane.showInputDialog(DesignPanel.this, "Please input a name for preset", design.toString());
				
				if (name == null) return;
				
				MetricSummary ms = new MetricSummary(name, designData.getTable());
				ConfigManager.add(ms);
				ConfigManager.saveChanges();
			}
		});
	}
	
	private void drawMetricsChart() {
		List<String> cols = designData.getCols();
		List<String> rows = designData.getRows();
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		for (int i = 0; i < cols.size(); i++) {
			ds.addValue(Math.log(designData.getTable().getAverage(cols.get(i)) + 1), "Averages", cols.get(i));
		}

		if (metricTable.getSelectedRow() >= 0) {
			for (int i = 0; i < cols.size(); i++)
				ds.addValue(Math.log(designData.getTable().get(rows.get(metricTable.getSelectedRow()), cols.get(i)) + 1), "Selected", cols.get(i));
		}
		
		JFreeChart chart = ChartFactory.createBarChart("", "", "log(value)", ds, PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.getDomainAxis().setVisible(false);
		ChartPanel panel = new ChartPanel(chart);
		
		chartPanel.removeAll();
		chartPanel.add(panel);
		chartPanel.validate();
	}
	
	private void validateDesignData() {
		designData = new DesignData();
		
		metricTable.setModel(new MetricTableModel());
		metricTable.getColumnModel().getColumn(0).setPreferredWidth(300);
		
		lblValNumMetrics.setText(String.valueOf(designData.getCols().size()));
		lblValNumItems.setText(String.valueOf(designData.getRows().size()));
		lblValWeightedSum.setText(String.format("%.2f", designData.getScore()));
		
		final DefaultListModel model = new DefaultListModel();
		for (Action act : designData.getActions()) {
			model.addElement(act.toString());
		}
		posActionsList.setModel(model);
		
		drawMetricsChart();
	}
	
	private RunConfig getRunConfig() {
		Object rc = runConfigBox.getSelectedItem();
		if (rc == null || !(rc instanceof RunConfig)) return RunConfig.getDefault();
		return (RunConfig)rc;
	}
	
	private void initConfigSelector() {
		updateConfigSelector();
		
		runConfigBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					validateDesignData();
				}
			}
		});
	}
	
	private void updateConfigSelector() {
		boolean prevfound = false;
		UUID previd = UUID.randomUUID();
		Object prc = runConfigBox.getSelectedItem();
		if (prc != null && prc instanceof RunConfig)
			previd = ((RunConfig)prc).getId();
		
		runConfigBox.removeAllItems();
		
		for (RunConfig rc : ConfigManager.runConfigs()) {
			runConfigBox.addItem(rc);
			
			if (rc.getId().equals(previd)) { 
				prevfound = true;
				prc = rc;
			}
		}
		
		if (prevfound) {
			runConfigBox.setSelectedItem(prc);
		} else {
			runConfigBox.setSelectedIndex(0);
		}
	}
	
	@Override
	public void onWindowEvent(Object e) {
		if (ConfigManager.CONFIG_CHANGED.equals(e)) {
			updateConfigSelector();
		}
	}
	
	private class DesignData {
		private MetricTable table;
		private List<String> cols;
		private List<String> rows;
		private List<Action> actions;
		
		public List<String> getRows() {
			return rows;
		}
		
		public List<String> getCols() {
			return cols;
		}
		
		public MetricTable getTable() {
			return table;
		}
		
		public List<Action> getActions() {
			return actions;
		}
		
		public double getScore() {
			return MetricCalculator.normalize(table, getRunConfig());
		}
		
		private DesignData() {
			table = MetricCalculator.calculate(design, getRunConfig());
			rows = table.getRows();
			Collections.sort(rows);
			
			cols = new ArrayList<String>();
			for (ConfigManager.Metric m : ConfigManager.getMetrics(getRunConfig())) {
				if (m.isEnabled())
					cols.add(m.getName());
			}
			Collections.sort(cols);
		
			actions = new ArrayList<Action>(TransitionManager.getPossibleActions(design, getRunConfig()));
		}
	}
	
	private class MetricTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		@Override
		public int getColumnCount() {
			return designData.getCols().size() + 1;
		}
		
		@Override
		public String getColumnName(int arg0) {
			if (arg0 == 0)
				return "Name";
			
			return designData.getCols().get(arg0 - 1); 
		}

		@Override
		public int getRowCount() {
			return designData.getRows().size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0)
				return getRow(designData.getRows().get(row));
			
			return designData.getTable().get(designData.getRows().get(row), designData.getCols().get(col - 1));
		}
		
		private Object getRow(String name) {
				Type t = design.getType(name);
				if (t != null)
					return t;
				return name;
		}
	}
	
	private static class MetricTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		
		private static final Icon packageIcon = new ImageIcon(MetricTableRenderer.class.getResource("/resources/icons/java/package.gif"));
		private static final Icon classIcon = new ImageIcon(MetricTableRenderer.class.getResource("/resources/icons/java/class.gif"));
		private static final Icon interfaceIcon = new ImageIcon(MetricTableRenderer.class.getResource("/resources/icons/java/interface.gif"));
		private static final Icon annotIcon = new ImageIcon(MetricTableRenderer.class.getResource("/resources/icons/java/annotation.gif"));
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			
			setIcon(null);
			
			if (value instanceof Double) {
				double dval = (Double)value;
				if (Double.isNaN(dval)) {
					setText("");
				}
			} else if (value instanceof Type) {
				Type tval = (Type)value;
				if (tval.isAnnotation())
					setIcon(annotIcon);
				else if (tval.isInterface())
					setIcon(interfaceIcon);
				else
					setIcon(classIcon);
			} else if (value instanceof String) {
				setIcon(packageIcon);
			}
			
			return this;
		}
	}
}
