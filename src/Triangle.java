
import java.util.ArrayList;
public class Triangle {
	public ArrayList<Vertex> vertices;
	public Vertex normal;
	public Triangle(Vertex x, Vertex y, Vertex z, Vertex normal) {
		vertices = new ArrayList<>();
		vertices.add(x);
		vertices.add(y);
		vertices.add(z);
		this.normal = normal;
	}
	public Triangle(ArrayList<Vertex> vertices, Vertex normal) {
		this.vertices = vertices;
		this.normal = normal;
	}
	
	@Override
	public String toString() {
		//System.out.println(vertices);
		return "Triangle: Vertex 1: "+vertices.get(0)+", Vertex 2: "+vertices.get(1)+", Vertex 3: "+vertices.get(2)+", normal: "+normal;
		//return vertices.toString();
	}

}
