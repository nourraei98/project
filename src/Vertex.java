
public class Vertex {
	public int x;
	public int y;
	public int z;
	
	public Vertex(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof Vertex)) {
			return false;
		}
		Vertex v = (Vertex) object;
		if (this.x == v.x && this.y == v.y && this.z == v.z){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return x+","+y+","+z;
	}
}
