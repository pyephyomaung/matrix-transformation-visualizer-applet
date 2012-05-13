import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.math.array.DoubleArray.increment;

import javax.swing.JApplet;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;

import org.math.plot.Plot3DPanel;

import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;


public class RipMathApplet extends JApplet {

	/**
	 * Create the applet.
	 */
	public RipMathApplet() {


		// define your data
		double[] x = increment(-0.10, 0.2, 10.0); // x = 0.0:0.1:1.0
		double[] y = increment(-0.10, 0.2, 10.0);// y = 0.0:0.05:1.0
		double[][] z1 = f1(x, y);
		double[][] z2 = f2(x, y);

		// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
		Plot3DPanel inplot = new Plot3DPanel("SOUTH");
		Plot3DPanel outplot = new Plot3DPanel("SOUTH");
		inplot.setSize(200, 200);

		// add grid plot to the PlotPanel
		inplot.addGridPlot("z=cos(PI*x)*sin(PI*y)", x, y, z1);
		getContentPane().setLayout(new MigLayout("", "[450px]", "[300px]"));
		//plot.addGridPlot("z=sin(PI*x)*cos(PI*y)", x, y, z2);
		
		getContentPane().add(inplot, "cell 0 0,alignx left,growy");
		getContentPane().add(outplot, "cell 0 0,grow");
	}

	// function definition: z=cos(PI*x)*sin(PI*y)
	public static double f1(double x, double y) {
		//double z = cos(x * PI) * sin(y * PI);
		double z = x * x;
		return z;
	}

	// grid version of the function
	public static double[][] f1(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f1(x[i], y[j]);
		return z;
	}

	// another function definition: z=sin(PI*x)*cos(PI*y)
	public static double f2(double x, double y) {
		double z = sin(x * PI) * cos(y * PI);
		return z;
	}

	// grid version of the function
	public static double[][] f2(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f2(x[i], y[j]);
		return z;
	}
}