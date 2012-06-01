import static org.math.array.LinearAlgebra.*;
import la4j.err.MatrixDecompositionException;
import Jama.Matrix;
import Jama.QRDecomposition;
import Jama.util.Maths;

/**
 * class for QR Decomposition of 3x3 matrix
 * @author Pye Phyo Maung
 *
 */
public class QRDecomposition3x3 {
	private double[][] A;
	public double[][] Q;
	public double[][] R;
	
	public QRDecomposition3x3(double[][] argA)
	{
		if(argA.length == 3 && argA[0].length == 3)
		{
			this.A = argA;
			decompose();
		}
	}
	
	private void decompose()
	{
		/*/ test if A is an upper triangular matrix 
		if(isUpperTriangle(A))
		{
			Q = identity(3);
			R = A;
			return;
		}
		*/
		
		/* QR decompositon using JAMA
		Jama.Matrix MatrixA = new Jama.Matrix(A);
		Jama.QRDecomposition jamaQRDecomposition = new Jama.QRDecomposition(MatrixA);
		Q = jamaQRDecomposition.getQ().getArray();
		R = jamaQRDecomposition.getR().getArray();
		*/
		
		
		// QR decomposition using la4j
		la4j.factory.DenseFactory denseFactory = new la4j.factory.DenseFactory();
		la4j.matrix.Matrix A_matrix = denseFactory.createMatrix(A);
		
		try {
			la4j.matrix.Matrix[] qr = A_matrix.decompose(new la4j.decomposition.QRDecompositor());
			Q = qr[0].toArray();
			R = qr[1].toArray();
			
			// fix diagonal of R
			if(R[0][0] < 0)
			{
				double[][] fixer = identity(3);
				fixer[0][0] = -1;
				Q = times(Q, fixer);
				R = times(fixer, R);
			}
			
			if(R[1][1] < 0)
			{
				double[][] fixer = identity(3);
				fixer[1][1] = -1;
				Q = times(Q, fixer);
				R = times(fixer, R);
			}
			
			if(R[2][2] < 0)
			{
				double[][] fixer = identity(3);
				fixer[2][2] = -1;
				Q = times(Q, fixer);
				R = times(fixer, R);
			}
			
			// handle negative zeros
			for(int i = 0; i < Q.length; i++)
			{
				for(int j = 0; j < Q[i].length; j++)
				{
					if(Q[i][j] == 0.0f) Q[i][j] = 0.0f;
					if(R[i][j] == 0.0f) R[i][j] = 0.0f;
				}
			}
			
			/* Debug
			double[][] la4j_q = qr[0].toArray();
			double[][] la4j_r = qr[1].toArray();
			System.out.println("la4j_q: \n" + RipMathApplet.format(la4j_q));
			System.out.println("la4j_r: \n" + RipMathApplet.format(la4j_r));
			*/
		} catch (MatrixDecompositionException e) {
			e.printStackTrace();
		}
		
		/*
		if(det(Q) == -1.0)
		{
			Q = times(Q, -1);
			R = times(R, -1);
		}
		*/
	}
	
	/**
	 * Inner function for QR decomposition
	 * A = QR using householder matrix 
	 */
	private void decompose1()
	{
		if(A == null) return;
		
		// test if A is a orthogonal matrix
		else if(isOrthogonal(A))
		{
			Q = A;
			R = identity(3);
			return;
		}
		
		// test if A is an upper triangular matrix 
		else if(isUpperTriangle(A))
		{
			Q = identity(3);
			R = A;
			return;
		}
		
		//double start = System.nanoTime();
		// Find Q1
		double[][] x1 = new double[][] {{A[0][0]}, {A[1][0]}, {A[2][0]}};
		double[][] y1 = new double[][] {{norm(x1)}, {0}, {0}};
		double[][] x1_minus_y1 = minus(x1, y1);
		
		double norm_x1_minus_y1 = norm(x1_minus_y1);
		double[][] u1;
		//if(norm_x1_minus_y1 == 0)
		if(x1[1][0] == 0 && x1[2][0] == 0)
		{
			u1 = identity(3);
		}
		else
		{
			u1 = divide(x1_minus_y1, norm(x1_minus_y1));
		}
		
		double[][] delta1 = times(times(u1, transpose(u1)), 2);
		double[][] Q1 = minus(identity(3), delta1);
		
		double[][] Q1A = times(Q1, A);
		double[][] A1 = new double[][] 
				{{Q1A[1][1], Q1A[1][2]},
				{Q1A[2][1], Q1A[2][2]}};
		
		// Find Q2
		double[][] x2 = new double[][] {{A1[0][0]}, {A1[1][0]}};
		double[][] y2 = new double[][] {{norm(x2)}, {0}};
		double[][] x2_minus_y2 = minus(x2, y2);
		
		double norm_x2_minus_y2 = norm(x2_minus_y2);
		double[][] u2;
		//if(norm_x2_minus_y2 == 0)
		if(x2[1][0] == 0 && x2[2][0] == 0)
		{
			u2 = identity(2);
		}
		else
		{
			u2 = divide(x2_minus_y2, norm(x2_minus_y2));
		}
		
		double[][] delta2 = times(times(u2, transpose(u2)), 2);
		double[][] P2 = minus(identity(2), delta2);
		double[][] Q2 = new double[][] 
				{{1, 0, 0},
				{0, P2[0][0], P2[0][1]},
				{0, P2[1][0], P2[1][1]}};
		
		Q = times(Q1, Q2);
		R = times(Q2, times(Q1, A));
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				if(i > j) R[i][j] = 0;
			}
		}
		//double end = System.nanoTime();
		//double dur1 = end-start;
		//System.out.println(dur1);
		
		double[][] QR = times(Q, R);
		System.out.println(RipMathApplet.format(QR));
		
		/* Check with Jama
		start = System.nanoTime();
		Jama.Matrix MatrixA = new Jama.Matrix(A);
		Jama.QRDecomposition jamaQRDecomposition = new Jama.QRDecomposition(MatrixA);
		double[][] jamaQ = jamaQRDecomposition.getQ().getArray();
		double[][] jamaR = jamaQRDecomposition.getR().getArray();
		end = System.nanoTime();
		double dur2 = end-start;
		
		System.out.println(dur2);
		System.out.println(dur2 / dur1);
		
		System.out.println(RipMathApplet.format(Q));
		System.out.println(RipMathApplet.format(R));
		System.out.println(RipMathApplet.format(jamaQ));
		System.out.println(RipMathApplet.format(jamaR));
		*/
	}
	
	/**
	 * Check if a matrix is a upper triangular matrix
	 * @param mat
	 * @return a boolean 
	 */
	public static boolean isUpperTriangle(double[][] mat)
	{
		for(int i = 0; i < mat.length; i++)
		{
			for(int j = 0; j < mat[i].length; j++)
			{
				if(i > j && mat[i][j] != 0) return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Check if a matrix is orthogonal
	 * A matrix is orthogonal iff AA^T = I
	 * @param mat
	 * @return a boolean
	 */
	public static boolean isOrthogonal(double[][] mat)
	{
		Matrix m = new Matrix(mat);
		double[][] product = times(mat, transpose(mat));
		System.out.println(RipMathApplet.format(product)); 
		for(int i = 0; i < mat.length; i++)
		{
			for(int j = 0; j < mat[i].length; j++)
			{
				if(i == j && mat[i][j] != 1) return false;
				else if(i != j && mat[i][j] != 0) return false;
			}
		}
		return true;
	}
	
	/**
	 * Find the length (norm) of k-th column using Euclidean 
	 * @param vec - vector
	 * @return the norm of the input vector
	 */
	public static double norm(double[][] mat)
	{
		double sumsq = 0;
		for(double[] row : mat)
		{
			for(double d : row)
			{
				sumsq += d * d;
			}
		}
		
		return Math.sqrt(sumsq);
	}
}
