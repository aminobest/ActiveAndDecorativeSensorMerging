package csvImporter;

public enum FIELD_ROLE {
	CASE_ID       ("Case Identifier"),
	ACTIVITY_NAME ("Activity Name"),
	RESOURCE_NAME ("Resource"),
	DATE          ("Execution Time"),
	OTHER         ("Other Attribute"),
	SKIP          ("Skip Attribute");
	
	private final String name;
	
	private FIELD_ROLE(String s) {
		name = s;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
