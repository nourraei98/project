import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
		String filename = "obj.stl";

		ArrayList<Triangle> triangles = getTrianglesFromSTL(filename); 
		int[] floorCoords = getXYCoords(triangles);

		// x,y corners of the floor plan
		int lowestX = floorCoords[0], lowestY = floorCoords[1], greatestX = floorCoords[2], greatestY = floorCoords[3];

		// get floor triangles by checking if they have vertices that belong to any
		// corner point
		// also get floor height through max z of all given floor triangles
		int floorHeight = -50000;
		ArrayList<Triangle> floorTriangles = new ArrayList<Triangle>();
		for (Triangle t : triangles) {
			boolean floorT = false;
			for (Vertex v : t.vertices) {
				boolean corner1 = v.x == greatestX && v.y == greatestY, corner2 = v.x == greatestX && v.y == lowestY,
						corner3 = v.x == lowestX && v.y == lowestY, corner4 = v.x == lowestX && v.y == greatestY;

				if (corner1 || corner2 || corner3 || corner4) {
					floorT = true;
					if (v.z > floorHeight) {
						floorHeight = v.z;
					}
				}
			}
			if (floorT) {
				floorTriangles.add(t);
			}
		}

		// find all triangles not part of the floor, based on their vertices
		// if any vertex has z lower or equal to floor height they are part of the floor
		for (Triangle t : triangles) {
			boolean floorT = true; 
			for (Vertex v : t.vertices) {
				if (v.z > floorHeight) {
					floorT = false;
					break;
				}
			}
			if (floorT) {
				floorTriangles.add(t); 
			}
		}

		// normalize everything by adding offset from 0, so that
		// floor coordinates start at 0,0
		// and objects will always have positive coords
		int diffFromZeroX = 0 - lowestX;
		int diffFromZeroY = 0 - lowestY;
		int diffFromZeroZ = 0 - getLowestHeight(triangles);
		greatestX += diffFromZeroX;
		greatestY += diffFromZeroY;
		floorHeight += diffFromZeroZ;
		lowestX = 0;
		lowestY = 0;

		int floorX = greatestX;
		int floorY = greatestY;
		for (Triangle t : triangles) {
			for (Vertex v : t.vertices) {
				v.x += diffFromZeroX;
				v.y += diffFromZeroY;
				v.z += diffFromZeroZ;
			}
		}

		// remove all floor triangles from our triangles to work with, since we have all
		// info about floor
		int k = 0;
		ArrayList<Triangle> tToRemove = floorTriangles;
		
		for (Triangle t : tToRemove) {
			triangles.remove(t);
			
		}
		writeTriangleToSTL(triangles, "out.stl");
		// at this point, we have objects separated from the floor
		// we have to remove objects until we have no empty triangles left

		// go through every triangle, vertex by vertex, and add triangles with matching
		// vertices until the object is fully built

		ArrayList<ArrayList<Triangle>> objects = new ArrayList<>();
		while (triangles.size() > 0) {
			ArrayList<Triangle> object = new ArrayList<>();
			// start at the first arbitrary triangle, so that any vertices it shares
			// with other triangles will be used to determine the object
			object.add(triangles.get(0));
			int i = 0;
			// looping over object triangles. each loop attempts to find a triangle match
			// for
			// every vertex, then adds it to object. this way, 'i' can iterate over every
			// triangle
			// matched in the object
			while (i < object.size()) {
				Triangle t = object.get(i);
				for (Vertex v : t.vertices) {
					for (Triangle t2 : triangles) {
						if (t == t2) // same triangle, ignore
							continue;
						for (Vertex v2 : t2.vertices) { // if ANY vertex match we have a connected object's triangle
							if (v2.equals(v) && !object.contains(t2)) {
								object.add(t2);
								break;
							}
						}
					}
				}
				i++;

			}

			// remove all triangles from the object so that we can separate objects
			for (Triangle r : object) {
				if (!triangles.remove(r)) {
				}
			}

			// we now have an isolated object
			objects.add(object);

		}

		System.out.println("There are " + objects.size() + " objects");
		for (ArrayList<Triangle> object : objects) {
			int[] coords = getXYCoords(object);
			System.out.println("Object occupies coords: (" + coords[0] + ", " + coords[1] + ") to (" + coords[2] + ","
					+ coords[3] + ")");
		}

		// floor plan, true = occupied, false otherwise
		boolean[][] floor = new boolean[floorX][floorY];
		for (boolean[] b : floor) {
			for (boolean bb : b) {
				bb = false;
			}
		}

		// draw occupied spaces as big boxes
		for (ArrayList<Triangle> object : objects) {
			int[] coords = getXYCoords(object);
			int x1 = coords[0], y1 = coords[1], x2 = coords[2], y2 = coords[3];
			for (int i = 0; i < x2 - x1; i++) {
				floor[x1 + i][y1] = true;
				for (int c = 0; c < y2 - y1; c++) {
					floor[x1 + i][y1 + c] = true;
				}
			}
		}

		// write floor plan to a text file for display purposes
		BufferedWriter out = new BufferedWriter(new FileWriter("floorplan.txt"));
		for (boolean[] b : floor) {
			for (boolean bb : b) {
				if (bb)
					out.write("x");
				else
					out.write(".");
			}
			out.write("\n");
		}
		out.close();

	}

	// get triangles from an STL file using stl file format
	public static ArrayList<Triangle> getTrianglesFromSTL(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));

		ArrayList<String> lines = new ArrayList<>();

		String line = "";
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		ArrayList<Triangle> triangles = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			String li = lines.get(i);
			String[] words = li.split(" ");

			// facet indicates start of a triangle, and gives the normal of its face
			if (words[0].equals("facet") && words[1].equals("normal")) {
				ArrayList<Vertex> vertices = new ArrayList<>();
				Vertex normal = parseVertex(li);
				i++;
				li = lines.get(i).trim();
				words = li.split(" ");

				// outer loop wraps around the vertices
				if (words[0].equals("outer") && words[1].equals("loop")) {
					i++;
					for (int j = 0; j < 3; j++) {
						li = lines.get(i).trim();
						words = li.split(" ");
						if (words[0].equals("vertex")) {
							Vertex v = parseVertex(li);
							vertices.add(v);
							i++;
						}
					}
				}

				Triangle T = new Triangle(vertices, normal);
				triangles.add(T);
			}
		}
		in.close();
		return triangles;
	}

	public static ArrayList<Triangle> getTrianglesFromOBJ(String filename) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(filename));

		ArrayList<String> lines = new ArrayList<>();

		String line = "";
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		ArrayList<Triangle> triangles = new ArrayList<>();
		ArrayList<Vertex> vertices = new ArrayList<>();
		for (String li : lines) {
			String[] words = li.split(" ");
			if (words[0].equals("v")) {
				// string of form 'v x y z', where v indicates this is a vertex,
				// and x y z are the coordinates of the vertex
				Vertex v = parseVertex(li);
				vertices.add(v);
			}
			else if (words[0].equals("f")){
				// triangle face of the form f v1 v2 v3, where
				// v1 v2 v3 are the vertices of that triangle
				// these are 1-indexed, so subtract 1 to have 0-index
				int vertex1Index = Integer.parseInt(words[1]) - 1,
					vertex2Index = Integer.parseInt(words[2]) - 1,
					vertex3Index = Integer.parseInt(words[3]) - 1;
				Vertex v1 = vertices.get(vertex1Index),
				v2 = vertices.get(vertex2Index),
				v3 = vertices.get(vertex3Index),
				normal = calculateNormal(v1,v2,v3);
				
				Triangle T = new Triangle(v1, v2, v3, normal);
				triangles.add(T);
			}
		}
		System.out.println(triangles.size());
		return triangles;
	}

	private static Vertex calculateNormal(Vertex v1, Vertex v2, Vertex v3) {
		Vertex V = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
		Vertex W = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
		int Nx = V.y * W.z - V.z * W.y;
		int Ny = V.z * W.x - V.x * W.z;
		int Nz = V.x * W.y - V.y * W.x;
		 
		int Ax = Nx / (Math.abs(Nx) + Math.abs(Ny) + Math.abs(Nz));
		int Ay = Ny / (Math.abs(Nx) + Math.abs(Ny) + Math.abs(Nz));
		int Az = Nz / (Math.abs(Nx) + Math.abs(Ny) + Math.abs(Nz));

		return new Vertex(Ax, Ay, Az);
	}

	// writes triangles to stl file, to test things. date is hard coded
	public static boolean writeTriangleToSTL(ArrayList<Triangle> triangles, String fileout) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileout));
			out.write("solid Uranium STLWriter Thu 14 Nov 2019 00:36:30\n");
			for (Triangle t : triangles) {
				Vertex normal = t.normal;
				out.write("facet normal " + normal.x + ".0 " + normal.y + ".0 " + normal.z + ".0\n");
				out.write("  outer loop\n");
				for (Vertex v : t.vertices) {
					out.write("    vertex " + v.x + ".0 " + v.y + ".0 " + v.z + ".0\n");
				}
				out.write("  endloop\n");
				out.write("endfacet\n");
			}
			out.write("endsolid Uranium STLWriter Thu 14 Nov 2019 00:36:30\n");
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	// parses a Vertex from the last three words in a string
	public static Vertex parseVertex(String s) {
		// assumes vertices are always at the end of a string
		String[] words = s.split(" ");
		int n = words.length;
		int x = (int) Double.parseDouble(words[n - 3]);
		int y = (int) Double.parseDouble(words[n - 2]);
		int z = (int) Double.parseDouble(words[n - 1]);
		Vertex v = new Vertex(x, y, z);
		return v;
	}

	// gets the extreme x / y of the object
	public static int[] getXYCoords(ArrayList<Triangle> object) {
		int lowestX = 5000, lowestY = 5000, greatestX = -5000, greatestY = -5000;

		for (Triangle t : object) {
			for (Vertex v : t.vertices) {
				if (v.x < lowestX) {
					lowestX = v.x;
				}
				if (v.y < lowestY) {
					lowestY = v.y;
				}
				if (v.x > greatestX) {
					greatestX = v.x;
				}
				if (v.y > greatestY) {
					greatestY = v.y;
				}
			}
		}
		int[] coords = new int[] { lowestX, lowestY, greatestX, greatestY };
		return coords;
	}

	// gets the lowest height, for floor shifting purposes
	public static int getLowestHeight(ArrayList<Triangle> object) {
		int lowestZ = 50000;

		for (Triangle t : object) {
			for (Vertex v : t.vertices) {
				if (v.z < lowestZ) {
					lowestZ = v.z;
				}
			}
		}
		return lowestZ;
	}

}