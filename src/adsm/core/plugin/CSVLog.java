package adsm.core.plugin;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import javax.swing.JProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.annotations.AuthoredType;
import org.processmining.framework.providedobjects.ProvidedObjectManager;

import com.google.common.base.CharMatcher;

import adsm.core.utils.DateUtil;
import adsm.core.utils.Utils;
import adsm.core.utils.XLogHelper;
import au.com.bytecode.opencsv.CSVParser;
import csvImporter.FIELD_ROLE;
import csvImporter.FIELD_TYPE;

@AuthoredType(
	typeName = "CSV Log",
	affiliation = "Università di Padova",
	author = "A. Burattin",
	email = "burattin@math.unipd.it"
)
public class CSVLog {

	public static final int LINES_FOR_AUTO_DETECT = 100;
	public static FIELD_ROLE[] REQUIRED_FIELDS = new FIELD_ROLE[]{
		FIELD_ROLE.ACTIVITY_NAME,
		FIELD_ROLE.CASE_ID,
		FIELD_ROLE.DATE };
	private static final String[] CANDIDATE_FIELDS_SEPARATORS = {";", ",", "\t", "|"};
	
	private String name = "";
	private Deque<String> linesLog = new ArrayDeque<String>();
	private int fieldsNumber = -1;
	private List<List<String>> fieldsLog = new ArrayList<List<String>>();
	private List<FIELD_ROLE> fieldsRole = new ArrayList<FIELD_ROLE>();
	private List<FIELD_TYPE> fieldsType = new ArrayList<FIELD_TYPE>();
	private List<String> fieldsHeader = new ArrayList<String>();
	
	private char fieldSeparator = '\0';
	private CSVParser parser;
	
	public CSVLog(String name) {
		this.name = name;
	}
	
	public void addLine(String line) {
		linesLog.add(line);
	}
	
	public void prepare() throws Exception {
		Utils.l("Identifying field separator");
		if(identifyFieldSeparator()) {
			parser = new CSVParser(fieldSeparator);
			splitLines();
		} else {
			throw new Exception("Cannot find proper field separator");
		}
		
		Utils.l("Identifying presence of headers");
		detectHeaders();
//		Utils.l("Has headers: " + hasHeaders());
		
		Utils.l("Identifying fields types");
		detectFieldsType();
		
		Utils.l("Identifying fields role");
		detectRoles();
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int fieldsNo() {
		return fieldsNumber;
	}
	
	public int getEntries() {
		if (hasHeaders()) {
			return fieldsLog.size() - 1;
		}
		return fieldsLog.size();
	}
	
	public List<String> getLine(int linesNumber) {
		if (hasHeaders()) {
			++linesNumber;
		}
		return fieldsLog.get(linesNumber);
	}
	
	public boolean hasHeaders() {
		return fieldsHeader.size() > 0;
	}
	
	public List<String> getHeaders() {
		return fieldsHeader;
	}
	
	public List<FIELD_ROLE> getRoles() {
		return fieldsRole;
	}
	
	public List<FIELD_TYPE> getTypes() {
		return fieldsType;
	}
	
	public void setRole(int fieldNumber, FIELD_ROLE role) {
		for (FIELD_ROLE single : REQUIRED_FIELDS) {
			if (role == single) {
				int index = fieldsRole.indexOf(single);
				if (index >= 0) {
					fieldsRole.set(index, FIELD_ROLE.OTHER);
				}
			}
		}
		
		fieldsRole.set(fieldNumber, role);
		if (role == FIELD_ROLE.ACTIVITY_NAME || role == FIELD_ROLE.CASE_ID || role == FIELD_ROLE.RESOURCE_NAME) {
			fieldsType.set(fieldNumber, FIELD_TYPE.STRING);
		} else if (role == FIELD_ROLE.DATE) {
			fieldsType.set(fieldNumber, FIELD_TYPE.DATE);
		}
	}
	
	public void setType(int fieldNumber, FIELD_TYPE type) {
		if (fieldsRole.get(fieldNumber) == FIELD_ROLE.OTHER) {
			fieldsType.set(fieldNumber, type);
		}
	}
	
	public XLog toXLog(JProgressBar progress) {
		XLog log = XLogHelper.generateNewXLog(name);
		int parsed = 0;
		boolean headerParsed = !hasHeaders();
		for (List<String> fields : fieldsLog) {
			if (!headerParsed) {
				headerParsed = true;
				continue;
			}
			
			if (progress != null) {
				progress.setValue(++parsed);
			}
			
			XTrace t = XLogHelper.insertTrace(log, fields.get(fieldsRole.indexOf(FIELD_ROLE.CASE_ID)));
			
			try {
				XEvent e = XLogHelper.insertEvent(t,
						fields.get(fieldsRole.indexOf(FIELD_ROLE.ACTIVITY_NAME)),
						DateUtil.parse(fields.get(fieldsRole.indexOf(FIELD_ROLE.DATE))));
				
				for (int i = 0; i < fieldsNo(); ++i) {
					String name = "field_" + i;
					if (hasHeaders()) {
						name = fieldsHeader.get(i);
					}
					String value = fields.get(i);
					
					if (fieldsRole.get(i) == FIELD_ROLE.RESOURCE_NAME) {
					
						XLogHelper.decorateElement(e, "org:resource", value, "Organizational");
					
					} else if (fieldsRole.get(i) == FIELD_ROLE.OTHER) {
						
						try {
							if (fieldsType.get(i) == FIELD_TYPE.BOOLEAN) {
								XLogHelper.decorateElement(e, name, Boolean.parseBoolean(value));
							} else if (fieldsType.get(i) == FIELD_TYPE.DATE) {
								XLogHelper.decorateElement(e, name, DateUtil.parse(value));
							} else if (fieldsType.get(i) == FIELD_TYPE.DOUBLE) {
								XLogHelper.decorateElement(e, name, Double.parseDouble(value));
							} else if (fieldsType.get(i) == FIELD_TYPE.INTEGER) {
								XLogHelper.decorateElement(e, name, Integer.parseInt(value));
							} else {
								XLogHelper.decorateElement(e, name, value);
							}
						} catch (ParseException e2) {
							e2.printStackTrace();
						}
					
					}
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return log;
	}
	
	public boolean export(UIPluginContext context, JProgressBar progress) {
		for(FIELD_ROLE field : REQUIRED_FIELDS) {
			if (fieldsRole.indexOf(field) == -1) {
				return false;
			}
		}
		
		if (progress != null) {
			progress.setMinimum(0);
			progress.setMaximum(fieldsLog.size());
		}
		
		XLog log = toXLog(progress);
		
		// export to context
		ProvidedObjectManager pom = context.getProvidedObjectManager();
		pom.createProvidedObject("XES log - " + name, log, XLog.class, context);
		context.getGlobalContext().getResourceManager().getResourceForInstance(log).setFavorite(true);
		return true;
	}
	
	private void detectHeaders() {
		for(int i = 0; i < fieldsNo(); ++i) {
			String value = CharMatcher.WHITESPACE.trimFrom(fieldsLog.get(0).get(i));
			if (getStringType(value) != FIELD_TYPE.STRING) {
				return;
			}
		}
		for(int i = 0; i < fieldsNo(); ++i ) {
			fieldsHeader.add(fieldsLog.get(0).get(i));
		}
	}
	
	private void detectRoles() {
		for(int i = 0; i < fieldsNo(); ++i) {
			FIELD_TYPE type = fieldsType.get(i);
			
			if (hasHeaders() && CharMatcher.WHITESPACE.trimFrom(fieldsHeader.get(i)).isEmpty()) {
				fieldsRole.add(FIELD_ROLE.SKIP);
			} else {
				
				// boolean, double, integer
				if (type == FIELD_TYPE.BOOLEAN || type == FIELD_TYPE.DOUBLE || type == FIELD_TYPE.INTEGER) {
					fieldsRole.add(FIELD_ROLE.OTHER);
				}
				
				// date
				if (type == FIELD_TYPE.DATE) {
					if (!fieldsRole.contains(FIELD_ROLE.DATE)) {
						fieldsRole.add(FIELD_ROLE.DATE);
					} else {
						fieldsRole.add(FIELD_ROLE.OTHER);
					}
				}
				
				// string
				if (type == FIELD_TYPE.STRING) {
					if (hasHeaders()) {
						String head = CharMatcher.WHITESPACE.trimFrom(fieldsHeader.get(i)).toLowerCase();
						if (head.contains("case") && !fieldsRole.contains(FIELD_ROLE.CASE_ID)) {
							fieldsRole.add(FIELD_ROLE.CASE_ID);
						} else if ((head.contains("activit") || head.contains("task")) && !fieldsRole.contains(FIELD_ROLE.ACTIVITY_NAME)) {
							fieldsRole.add(FIELD_ROLE.ACTIVITY_NAME);
						} else if ((head.contains("author") || head.contains("originat") || head.contains("resour") || head.contains("user")) && !fieldsRole.contains(FIELD_ROLE.RESOURCE_NAME)) {
							fieldsRole.add(FIELD_ROLE.RESOURCE_NAME);
						} else {
							fieldsRole.add(FIELD_ROLE.OTHER);
						}
						
					} else {
						if (!fieldsRole.contains(FIELD_ROLE.CASE_ID)) {
							fieldsRole.add(FIELD_ROLE.CASE_ID);
						} else if (!fieldsRole.contains(FIELD_ROLE.ACTIVITY_NAME)) {
							fieldsRole.add(FIELD_ROLE.ACTIVITY_NAME);
						} else {
							fieldsRole.add(FIELD_ROLE.OTHER);
						}
					}
				}
			}
		}
	}
	
	private void detectFieldsType() {
		for(int i = 0; i < fieldsNo(); i++) {
			fieldsType.add(getFieldType(i));
//			Utils.l("field " + i + " - " + getFieldType(i));
		}
	}
	
	private FIELD_TYPE getFieldType(int fieldNo) {
		FIELD_TYPE t = FIELD_TYPE.STRING;
		boolean first = hasHeaders();
		int i = 0;
		for (List<String> l : fieldsLog) {
			if (i++ >= LINES_FOR_AUTO_DETECT) {
				break;
			}
			if (first) {
				first = false;
				continue;
			}
			t = getStringType(l.get(fieldNo));
		}
		return t;
	}
	
	private FIELD_TYPE getStringType(String value) {
		value = CharMatcher.WHITESPACE.trimFrom(value);
		if (value.toLowerCase().equals("true") || value.toLowerCase().equals("false")) {
			return FIELD_TYPE.BOOLEAN;
		} else if (DateUtil.isValidDate(value)) {
			return FIELD_TYPE.DATE;
		} else {
			try {
				Integer.parseInt(value);
				return FIELD_TYPE.INTEGER;
			} catch(NumberFormatException e) {}
			try {
				Double.parseDouble(value);
				return FIELD_TYPE.DOUBLE;
			} catch(NumberFormatException e) {}
		}
		return FIELD_TYPE.STRING;
	}
	
	private void splitLines() {
		String line;
		while((line = linesLog.poll()) != null) {
			line = CharMatcher.WHITESPACE.trimFrom(line);
			if (line.isEmpty()) {
				continue;
			}
			try {
				List<String> toAdd = new ArrayList<String>();
				for (String i : Arrays.asList(parser.parseLine(line))) {
					toAdd.add(CharMatcher.WHITESPACE.trimFrom(i));
				}
				fieldsLog.add(toAdd);
				fieldsNumber = (fieldsNumber == -1)? toAdd.size() : Math.min(fieldsNumber, toAdd.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean identifyFieldSeparator() {
		for(String candidateSeparator : CANDIDATE_FIELDS_SEPARATORS) {
			int i = 0;
			double average = 0;
			double variance = 0;
			for (String line : linesLog) {
				if (i++ >= LINES_FOR_AUTO_DETECT) {
					break;
				}
				int x = StringUtils.countMatches(line, candidateSeparator);
				double delta = x - average;
				average = average + ((1.0 / i) * delta);
				variance = variance + delta * (x - average);
			}
			variance = variance / (i - 1);
			if (average > 0 && variance <= average * 0.1) {
				fieldSeparator = candidateSeparator.toCharArray()[0];
				Utils.l("Field separator identified: `" + fieldSeparator + "'");
				return true;
			}
		}
		Utils.l("Field separator not found");
		return false;
	}
}
