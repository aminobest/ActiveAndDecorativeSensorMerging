package csvImporter;

public enum FIELD_TYPE {
	BOOLEAN ("Boolean"),
	DATE    ("Date"),
	DOUBLE  ("Double"),
	INTEGER ("Integer"),
	STRING  ("String");
	
	private final String name;
	
	private FIELD_TYPE(String s) {
		name = s;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
