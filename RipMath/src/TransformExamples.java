/**
 * Enumeration of Transformation Matrix examples
 * @author Pye Phyo Maung
 *
 */
public enum TransformExamples {
	USER_DEFINE("User Defined", 
			new String[][] {
			{"", "", "", ""},
			{"", "", "", ""},
			{"", "", "", ""}}),
			
	SCALE_BY_2_ON_X_AXIS("Scale by 2 on x-axis",
			new String[][] {
			{"2", "0", "0", "0"},
			{"0", "1", "0", "0"},
			{"0", "0", "1", "0"}}),
			
	SCALE_BY_2_ON_Y_AXIS("Scale by 2 on y-axis",
			new String[][] {
			{"1", "0", "0", "0"},
			{"0", "2", "0", "0"},
			{"0", "0", "1", "0"}}),
					
	SCALE_BY_2_ON_Z_AXIS("Scale by 2 on z-axis",
			new String[][] {
			{"1", "0", "0", "0"},
			{"0", "1", "0", "0"},
			{"0", "0", "2", "0"}}),
			
	ROTATION_AROUND_X_AXIS("45\u00B0 Rotate around x-axis",
			new String[][] {
			{"1", "0", "0"},
			{"0", "cos(pi/4)", "-sin(pi/4)", "0"},
			{"0", "sin(pi/4)", "cos(pi/4)", "0"}}),		
			
	ROTATION_AROUND_Y_AXIS("45\u00B0 Rotate around y-axis",
			new String[][] {
			{"cos(pi/4)", "0", "sin(pi/4)", "0"},
			{"0", "1", "0", "0"},
			{"-sin(pi/4)", "0", "cos(pi/4)", "0"}}),
			
	ROTATION_AROUND_Z_AXIS("45\u00B0 Rotate around z-axis",
			new String[][] {
			{"cos(pi/4)", "-sin(pi/4)", "0", "0"},
			{"sin(pi/4)", "cos(pi/4)", "0", "0"},
			{"0", "0", "1", "0"}}),
	
	SPIRAL_AROUND_Z_AXIS("Spiral 45\u00B0 around z-axis",
			new String[][] {
			{"cos(z*pi/4)", "-sin(z*pi/4)", "0", "0"},
			{"sin(z*pi/4)", "cos(z*pi/4)", "0", "0"},
			{"0", "0", "1", "0"}});
	
	private String description;
	private String[][] matrixTable;
	
	private TransformExamples(String _description, String[][] _matrixTable)
	{
		description = _description;
		matrixTable = _matrixTable;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String[][] getMatrixTable()
	{
		return matrixTable;
	}
	
	 @Override  
	 public String toString() {  
		 return description;  
	 }
}
