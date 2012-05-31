import com.jgoodies.forms.util.Utilities;


/**
 * Enumeration of Transformation Matrix examples
 * @author Pye Phyo Maung
 *
 */
public enum TransformExamples {
	USER_DEFINE("User Defined", 
			new String[][] {
			{"", "", ""},
			{"", "", ""},
			{"", "", ""}}),
			
	ROTATION_AROUND_X_AXIS("45\u00B0 counter-clockwise Rotate around x-axis",
			new String[][] {
			{"1", "0", "0"},
			{"0", "cos(pi/4)", "-sin(pi/4)"},
			{"0", "sin(pi/4)", "cos(pi/4)"}}),
			
	ROTATION_AROUND_Y_AXIS("45\u00B0 counter-clockwise Rotate around y-axis",
			new String[][] {
			{"cos(pi/4)", "0", "sin(pi/4)"},
			{"0", "1", "0"},
			{"-sin(pi/4)", "0", "cos(pi/4)"}}),
			
	ROTATION_AROUND_Z_AXIS("45\u00B0 counter-clockwise Rotate around z-axis",
			new String[][] {
			{"cos(pi/4)", "-sin(pi/4)", "0"},
			{"sin(pi/4)", "cos(pi/4)", "0"},
			{"0", "0", "1"}}),
	
	SPIRAL_AROUND_Z_AXIS("Spiral 45\u00B0 around z-axis",
			new String[][] {
			{"cos(z*pi/4)", "-sin(z*pi/4)", "0"},
			{"sin(z*pi/4)", "cos(z*pi/4)", "0"},
			{"0", "0", "1"}});
	
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
