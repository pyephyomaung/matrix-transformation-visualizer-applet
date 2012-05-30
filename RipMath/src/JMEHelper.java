import java.nio.FloatBuffer;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;


public class JMEHelper {
	public static double[][] getVertices(Mesh _mesh)
    { 
        FloatBuffer vertexBuffer = _mesh.getFloatBuffer(Type.Position);
        Vector3f[] vertexList = BufferUtils.getVector3Array(vertexBuffer);
        double[][] result = new double[vertexList.length][3];
        
        for(int i = 0; i < vertexList.length; i++)
        {
        	result[i] = new double[] {vertexList[i].x, vertexList[i].y, vertexList[i].z};
        }
        return result;
    }
    
    public static int[] getIndices(Mesh _mesh)
    {
        IndexBuffer indexBuffer = _mesh.getIndexBuffer();
        int[] intBuffer = new int[_mesh.getTriangleCount() * 3];
        for(int i = 0; i < _mesh.getTriangleCount() * 3; i++)
        {
            intBuffer[i] = indexBuffer.get(i);
        }
        return intBuffer;
    }
}
