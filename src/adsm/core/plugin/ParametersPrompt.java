package adsm.core.plugin;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.contexts.uitopia.UIPluginContext;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import adsm.datamodel.pluginGui.FileParameters;
import adsm.datamodel.pluginGui.GlobalParameters;
import iciPlugin.CaseIdentifier;

public class ParametersPrompt extends JPanel {
	

	  private GlobalParameters gp ;

		/**
		 * 
		 */
		private static final long serialVersionUID = -6804610998661748174L;

		
	
		
		
		/// new declarations
		private JPanel panel = new JPanel();
		
		private JTextField caseidindexinput=new RoundJTextField(5);
		private JTextField activityindexinput=new RoundJTextField(5);
		private JTextField dsdiminput= new RoundJTextField(5);
		private JTextField countdiminput= new RoundJTextField(5);
		private JTextField endtimeindexinput= new RoundJTextField(5);
		private JTextField markinginput= new RoundJTextField(5);
		private JPanel decorativepanel = new JPanel();
		private JComboBox fileparametersinput = new JComboBox();
		private JPanel globalparamspanel = new JPanel();
		private JLabel globalparamslabel = new JLabel("Global Parameters:");
		
		private JLabel similarityfunctionlabel = new JLabel("Similarity Function:");
		private JLabel lblMainlog = new JLabel("Main Log File:");
		private JLabel markinglabel = new JLabel("Marking:");
		
		private JComboBox  similarityfunctioninput = new JComboBox();
		private JComboBox mainlogfileinput = new JComboBox();
		private JPanel eventactivepanel = new JPanel();
		private JLabel fileparameterslabel = new JLabel("File Parameters:");
		private JTextField starttimeindexinput =new RoundJTextField(15);
		private JComboBox typeinput = new JComboBox();
		private JLabel typelabel = new JLabel("Type:");
		private JSlider selectionratiosliderinput = new JSlider(0, 10000, 9000);
		
		
		private JLabel dsdimlabel = new JLabel("Discrete Dimension Attr. Indexes*:");
		private JLabel contdimlabel = new JLabel("Continuous Dimension Attr. Indexes*:");
		private JLabel lblUseTo = new JLabel("* Use comma (,) to separate attribute indexes");
		private JLabel selectionratiolabel = new JLabel("Selection Ratio:");
		private JLabel caseidindexlabel = new JLabel("Case Id Index:");
		private JLabel activityindexlabel = new JLabel("Event Name Index (to infer case id):");
		private JLabel starttimeindexlabel = new JLabel("Start Time Index:");
		private JLabel endtimeindexlabel = new JLabel("End Time Index:");
		private JButton infercaseid = new JButton("Infer Case Id");
		private JLabel slidervalue = new JLabel("1.0");
		
		
		private boolean retry ;
		
		private HashMap<String, Integer> types = new HashMap<String, Integer>(){
			{
				put("Select",0);
				put("Event Log",1);
				put("Active Sensor Log",2);
				put("Decorative Sensor Log",3);
			}
		};
		
		UIPluginContext context = null ;
		
		public ParametersPrompt(GlobalParameters gp, UIPluginContext context,boolean retry) {
			
			this.gp = gp ;
			this.context = context ;
			this.retry = retry ;
		
			
//			// Add fake classifier for parameters panel post-view
//			HashSet<XEventClassifier> set = new HashSet<XEventClassifier>();
//			XEventClassifier nameCl = new XEventNameClassifier();
//	        XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
//	        XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
//	        set.add(attrClass);
//			this.classifiersPanel = new ClassifiersPanel(set);
			this.init();
		}
		
	
		
		
		boolean initialized = true ;
		private void init(){
			
//			SlickerFactory factory = SlickerFactory.instance();
//			SlickerDecorator decorator = SlickerDecorator.instance();
//			
//			this.thresholdsPanel = factory.createRoundedPanel(15, Color.gray);
//		
//			this.thresholdTitle = factory.createLabel("Thresholds");
//			this.thresholdTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
//			this.thresholdTitle.setForeground(new Color(40,40,40));
//			

//			
//			FlowLayout customLayout = new FlowLayout(FlowLayout.LEFT);
//			customLayout.setHgap(2);
//			int customPanelWidth = 405;
//			
//			JPanel p1=  new JPanel(customLayout);
//			p1.setBackground(Color.gray);
			
//			JTextField txtTest = new JTextField();
//			txtTest.setText("test");
//			p1.add(txtTest);
//			txtTest.setColumns(10);
			
			
			
			SlickerFactory factory = SlickerFactory.instance();
		    
			similarityfunctioninput.setBackground(Color.LIGHT_GRAY);
		    caseidindexinput.setBackground(Color.LIGHT_GRAY);
		    activityindexinput.setBackground(Color.LIGHT_GRAY);
			dsdiminput.setBackground(Color.LIGHT_GRAY);
			countdiminput.setBackground(Color.LIGHT_GRAY);
			endtimeindexinput.setBackground(Color.LIGHT_GRAY);
			starttimeindexinput.setBackground(Color.LIGHT_GRAY);
			markinginput.setBackground(Color.LIGHT_GRAY);
			selectionratiosliderinput.setBackground(Color.gray);
		   
			// properties
			this.globalparamspanel = factory.createRoundedPanel(60, Color.gray);
			this.panel = factory.createRoundedPanel(60, Color.gray);
			panel.setLayout(null);
			panel.setBounds(16, 154, 697, 260);
			fileparametersinput.setBounds(340, 17, 210, 27);
			globalparamspanel.setBounds(16, 6, 697, 133);
			globalparamspanel.setLayout(null);
//			globalparamspanel.setBackground(this.getBackground());
	
			globalparamslabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			globalparamslabel.setBounds(42, 26, 154, 16);
	
	
			if(retry){
				 JOptionPane.showMessageDialog(new JFrame(), "Some fields are missing, please fill in the missing fields!", "Error",
					        JOptionPane.ERROR_MESSAGE);
			}
			
			globalparamspanel.add(globalparamslabel);
			
			
			
//			dateformatlabel.setBounds(68, 71, 120, 16);

			similarityfunctionlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			similarityfunctionlabel.setBounds(68, 59, 160, 16);
			globalparamspanel.add(similarityfunctionlabel);
			lblMainlog.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			lblMainlog.setBounds(68, 87, 95, 16);
			globalparamspanel.add(lblMainlog);
//			dateformatinput.setBounds(342, 66, 199, 26);
			
			similarityfunctioninput.setBounds(342, 54, 199, 26);
			globalparamspanel.add(similarityfunctioninput);
			similarityfunctioninput.setModel(new DefaultComboBoxModel(new String[] {"Jaccard Similarity", "TF-IDF Similarity"}));
			
			mainlogfileinput.setBounds(342, 83, 199, 27);
			for(FileParameters fp : gp.getFiles()){
				mainlogfileinput.addItem(fp);
			}
			globalparamspanel.add(mainlogfileinput);
			fileparameterslabel.setBounds(42, 18, 149, 20);
			fileparameterslabel.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
			panel.add(fileparameterslabel);
			for(FileParameters fp : gp.getFiles()){
				fileparametersinput.addItem(fp);
			}
			panel.add(fileparametersinput);
			typelabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			typelabel.setBounds(71, 60, 120, 16);
			panel.add(typelabel);
			typeinput.setModel(new DefaultComboBoxModel(new String[] {"Select", "Event Log", "Active Sensor Log", "Decorative Sensor Log"}));
			
			
			typeinput.setBounds(340, 56, 210, 27);
			panel.add(typeinput);
			decorativepanel.setBounds(16, 122, 642, 105);
			
			panel.add(decorativepanel);
			decorativepanel.setLayout(null);
			decorativepanel.setBackground(panel.getBackground());
			decorativepanel.setVisible(false);
			
			dsdimlabel.setBounds(57, 10, 219, 16);
			decorativepanel.add(dsdimlabel);
			dsdimlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			dsdiminput.setBounds(329, 5, 212, 26);
			decorativepanel.add(dsdiminput);
			dsdiminput.setColumns(10);
			contdimlabel.setBounds(57, 46, 240, 16);
			decorativepanel.add(contdimlabel);
			contdimlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			countdiminput.setBounds(329, 41, 212, 26);
			decorativepanel.add(countdiminput);
			countdiminput.setColumns(10);
			lblUseTo.setBounds(168, 83, 233, 16);
			decorativepanel.add(lblUseTo);
			lblUseTo.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
			
			slidervalue.setBounds(552, 92, 61, 16);
			panel.add(slidervalue);	
			
			selectionratiosliderinput.setBounds(340, 92, 210, 29);
			markinginput.setBounds(345, 92, 210, 26);
			
			panel.add(selectionratiosliderinput);
			panel.add(markinginput);
			
			
		
			
			selectionratiolabel.setBounds(71, 95, 120, 16);
			markinglabel.setBounds(71, 95, 120, 16);
			selectionratiolabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			markinglabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			
			panel.add(selectionratiolabel);
			panel.add(markinglabel);
			
			
			
			
			eventactivepanel.setBounds(41, 118, 642, 130);
			panel.add(eventactivepanel);
			eventactivepanel.setLayout(null);	
			eventactivepanel.add(caseidindexlabel);
			eventactivepanel.add(activityindexlabel);
			caseidindexlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			activityindexlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			eventactivepanel.add(caseidindexinput);
			eventactivepanel.add(activityindexinput);
			eventactivepanel.add(infercaseid);
			
			caseidindexinput.setColumns(10);
			activityindexinput.setColumns(10);
			eventactivepanel.add(starttimeindexlabel);
			starttimeindexlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			eventactivepanel.add(starttimeindexinput);
			starttimeindexinput.setColumns(10);
			eventactivepanel.add(endtimeindexlabel);
			endtimeindexlabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
			starttimeindexlabel.setBounds(30, 17, 200, 16);
			starttimeindexinput.setBounds(301, 15, 61, 26);
			endtimeindexlabel.setBounds(30, 47, 107, 16);
			endtimeindexinput.setBounds(301, 45, 61, 26);
			activityindexlabel.setBounds(30, 77, 250, 16);
			activityindexinput.setBounds(301, 75, 61, 26);
			caseidindexlabel.setBounds(30, 107, 200, 16);
			caseidindexinput.setBounds(301, 105, 61, 26);
			infercaseid.setBounds(400, 107, 160, 26);
			eventactivepanel.setBackground(panel.getBackground());
			eventactivepanel.add(endtimeindexinput);
			endtimeindexinput.setColumns(10);
			slidervalue.setVisible(false);
			decorativepanel.setVisible(false);
			markinglabel.setVisible(false);
			markinginput.setVisible(false);
		   ///actions 
			
			gp.setMainlogfile(gp.getFiles().get(0).getFilename());
			gp.setSimilarityfunction("JA");
			
			
			
			similarityfunctioninput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//Jaccard Similarity", "TF-IDF Similarity
					if(similarityfunctioninput.getSelectedItem().toString().equals("Jaccard Similarity"))
					gp.setSimilarityfunction("JA");
					else if(similarityfunctioninput.getSelectedItem().toString().equals("TF-IDF Similarity"))
					gp.setSimilarityfunction("simTF");
					
				}
			});
			
			mainlogfileinput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gp.setMainlogfile(mainlogfileinput.getSelectedItem().toString());
				}
			});
			
			fileparametersinput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

//					dsdiminput.setText("");
//					countdiminput.setText("");
//					caseidindexinput.setText("");
//					endtimeindexinput.setText("");
//					starttimeindexinput.setText("");
//					typeinput.setSelectedIndex(0);
//					selectionratiosliderinput.setValue(0);
					
					
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							if(f.getType()!=null){
								typeinput.setSelectedIndex(types.get(f.getType()));
								
								if(typeinput.getSelectedItem().toString().equals("Decorative Sensor Log")){
									slidervalue.setVisible(false);
								} 
								else {
									slidervalue.setVisible(true);
									
								}
								
							}
							else if(f.getType()==null) {
								typeinput.setSelectedIndex(0);
								
							}
							
							if(f.getSelectionRatio()!=null && !typeinput.getSelectedItem().equals("Decorative Sensor Log")){
								selectionratiosliderinput.setValue(Integer.valueOf(f.getSelectionRatio().replace(".", "")));
							}
							else if(f.getSelectionRatio()==null){
								selectionratiosliderinput.setValue(0);
							}
							
							if(f.getDiscreteattr()!=null){
								dsdiminput.setText(f.getDiscreteattr());
							}
							else if(f.getDiscreteattr()==null){
								dsdiminput.setText("");
							}
							
							if(f.getContinousattr()!=null){
								countdiminput.setText(f.getContinousattr());
							}
							else if(f.getContinousattr()==null){
								countdiminput.setText("");
							}
							
							if(f.getCaseIdIndex()!=null){
								caseidindexinput.setText(f.getCaseIdIndex());
							}
							else if(f.getCaseIdIndex()==null){
								caseidindexinput.setText("");
							}
							if(f.getActivityIndex()!=null){
								activityindexinput.setText(f.getActivityIndex());
							}
							else if(f.getActivityIndex()==null){
								activityindexinput.setText("");
							}
							
							if(f.getStarttimeIndex()!=null){
								starttimeindexinput.setText(f.getStarttimeIndex());
							}
							else if(f.getStarttimeIndex()==null){
								starttimeindexinput.setText("");
							}
							
							if(f.getEndttimeIndex()!=null){
								endtimeindexinput.setText(f.getEndttimeIndex());
							}
							else if(f.getEndttimeIndex()==null){
								endtimeindexinput.setText("");
							}
							if(f.getMarking()!=null){
								markinginput.setText(f.getMarking());
							}
							else if(f.getMarking()==null){
								markinginput.setText("");
							}
							
							
							
						}
					}
					
				}
			});
			
			typeinput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
	              
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setType(typeinput.getSelectedItem().toString());
						}
					}
					//// change panel depeding on type 
					if(typeinput.getSelectedItem().toString().equals("Decorative Sensor Log")){
						eventactivepanel.setVisible(false);
						decorativepanel.setVisible(true);
						selectionratiolabel.setVisible(false);
						selectionratiosliderinput.setVisible(false);
						slidervalue.setVisible(false);
						markinglabel.setVisible(true);
						markinginput.setVisible(true);
						slidervalue.setVisible(false);
						
					}
					else {
						decorativepanel.setVisible(false);
						eventactivepanel.setVisible(true);
						selectionratiolabel.setVisible(true);
						selectionratiosliderinput.setVisible(true);
						slidervalue.setVisible(true);
						markinglabel.setVisible(false);
						markinginput.setVisible(false);
						slidervalue.setVisible(true);
						
						if(typeinput.getSelectedItem().toString().equals("Active Sensor Log")){
							caseidindexlabel.setVisible(false);
							caseidindexinput.setVisible(false);
							activityindexlabel.setVisible(false);
							activityindexinput.setVisible(false);
							infercaseid.setVisible(false);
							endtimeindexinput.setVisible(false);
							endtimeindexlabel.setVisible(false);
						}
						else { // event log
							caseidindexlabel.setVisible(true);
							caseidindexinput.setVisible(true);
							infercaseid.setVisible(true);
							endtimeindexinput.setVisible(true);
							endtimeindexlabel.setVisible(true);
							activityindexlabel.setVisible(true);
							activityindexinput.setVisible(true);
						}
						
					}	
					//////
				}
			});
			dsdiminput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
						String filename = fileparametersinput.getSelectedItem().toString();
						for(FileParameters f : gp.getFiles()){
							if(f.getFilename().equals(filename)){
								f.setDiscreteattr(dsdiminput.getText().toString());
							}
						}
				}
			});
		

			countdiminput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setContinousattr(countdiminput.getText().toString());
						}
					}
				}
			});
				
			caseidindexinput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setCaseIdIndex(caseidindexinput.getText().toString());
						}
					}
				}
			});
			
			activityindexinput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setActivityIndex(activityindexinput.getText().toString());
						}
					}
				}
			});
			
			markinginput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setMarking(markinginput.getText().toString());
						}
					}
				}
			});
			
			starttimeindexinput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setStarttimeIndex(starttimeindexinput.getText().toString());
						
						}
					}
				}
			});
			
			endtimeindexinput.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setEndttimeIndex(endtimeindexinput.getText().toString().trim().length()==0 ? starttimeindexinput.getText().toString() : endtimeindexinput.getText().toString());
						}
					}
				}
			});
			
			selectionratiosliderinput.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					String labelStr = String.valueOf(sliderValueFunction(selectionratiosliderinput.getValue()));
					slidervalue.setText(labelStr);
					slidervalue.setVisible(true);
					String filename = fileparametersinput.getSelectedItem().toString();
					for(FileParameters f : gp.getFiles()){
						if(f.getFilename().equals(filename)){
							f.setSelectionRatio(labelStr);
						}
					}
					
					
				}
			});
			
			
			infercaseid.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
							
							
							String filename = fileparametersinput.getSelectedItem().toString();
							for(FileParameters f : gp.getFiles()){
								if(f.getFilename().equals(filename)){
									
							
							
							
							
							CaseIdentifier ci = new CaseIdentifier();
						
							try {
											
							ArrayList<Integer> candidateCaseIdIndexes = new ArrayList<Integer>() {
								{
									for (String candidateCaseIdColumn : f.getCsvfile().getHeaders()) {
										int candidateCaseIdIndex = f.getCsvfile().getHeaders()
												.indexOf(candidateCaseIdColumn);
										if (candidateCaseIdIndex != Integer.valueOf(f.getStarttimeIndex())
												&& candidateCaseIdIndex != Integer.valueOf(f.getActivityIndex())) {
											add(candidateCaseIdIndex);
										}

									}
								}
							};
								
							
					int caseId = ci.InferCaseIdFromLog(f.getCsvfile(), Integer.valueOf(f.getStarttimeIndex()), Integer.valueOf(f.getActivityIndex()), candidateCaseIdIndexes);
					
					
					f.setCaseIdIndex(caseId+"");
					caseidindexinput.setText(f.getCaseIdIndex());
					
				
					
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			
								}
							}
							
				}
			});
			
			
//
//			p1.add(starttimeindexlabel);
//			thresholdsPanel.add(p1);

//			this.thresholdsPanel.setBounds(0, 50, 520, 240);
//			this.thresholdTitle.setBounds(10, 10, 200, 30);
			
			this.setLayout(null);
			this.add(globalparamspanel);
			this.add(panel);
//			this.add(eventactivepanel);
//			this.add(decorativepanel);
			//this.add(btnTest);
			
//			this.validate();
//			this.repaint();
			
	
			
			
		}
		
	
		private double sliderValueFunction(int x) {
			
			//normal scaling
			return x / 10000.0;
			
	}
	
	
		
	
	

}