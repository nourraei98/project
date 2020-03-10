import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;


public class AStar {
	private final List<Node> open;
	private final List<Node> closed;
	private final List<Node> path;
	private int[][] maze;
	private Node now;
	private int xstart;
	private int ystart;
	private int xend, yend;
	private final boolean diag;
	private int direction;

	// Node class
	@SuppressWarnings("rawtypes")
	static class Node implements Comparable {
		public Node parent;
		public int x, y;
		public double g;
		public double h;
		Node(Node parent, int xpos, int ypos, double g, double h) {
			this.parent = parent;
			this.x = xpos;
			this.y = ypos;
			this.g = g;
			this.h = h;
		}
		// Compare by f value (g + h)
		@Override
		public int compareTo(Object o) {
			Node that = (Node) o;
			return (int)((this.g + this.h) - (that.g + that.h));
		}
	}

	AStar(int[][] maze, int xstart, int ystart, boolean diag) {
		this.open = new ArrayList<>();
		this.closed = new ArrayList<>();
		this.path = new ArrayList<>();
		this.maze = maze;
		this.now = new Node(null, xstart, ystart, 0, 0);
		this.xstart = xstart;
		this.ystart = ystart;
		this.diag = diag;    
		this.direction = 1;		//current direction robot is facing ranging from 1-4. N is 1, E is 2, S is 3, W is 4
	}
	/*
	 ** Finds path to xend/yend or returns null
	 **
	 ** @param (int) xend coordinates of the target position
	 ** @param (int) yend
	 ** @return (List<Node> | null) the path
	 */
	public List<Node> findPathTo(int xend, int yend) {
		this.xend = xend;
		this.yend = yend;
		this.closed.add(this.now);
		addNeigborsToOpenList();
		while (this.now.x != this.xend || this.now.y != this.yend) {
			if (this.open.isEmpty()) { // Nothing to examine
				return null;
			}
			this.now = this.open.get(0); // get first node (lowest f score)
			this.open.remove(0); // remove it
			this.closed.add(this.now); // and add to the closed
			addNeigborsToOpenList();
		}
		this.path.add(0, this.now);
		while (this.now.x != this.xstart || this.now.y != this.ystart) {
			this.now = this.now.parent;
			this.path.add(0, this.now);
		}
		return this.path;
	}
	/*
	 ** Looks in a given List<> for a node
	 **
	 ** @return (bool) NeightborInListFound
	 */
	private static boolean findNeighborInList(List<Node> array, Node node) {
		return array.stream().anyMatch((n) -> (n.x == node.x && n.y == node.y));
	}
	/*
	 ** Calculate distance between this.now and xend/yend
	 **
	 ** @return (int) distance
	 */
	private double distance(int dx, int dy) {
		if (this.diag) { // if diagonal movement is allowed
			return Math.hypot(this.now.x + dx - this.xend, this.now.y + dy - this.yend); // return hypotenuse
		} else {
			return Math.abs(this.now.x + dx - this.xend) + Math.abs(this.now.y + dy - this.yend); // else return "Manhattan distance"
		}
	}
	@SuppressWarnings("unchecked")
	private void addNeigborsToOpenList() {
		Node node;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (!this.diag && x != 0 && y != 0) {
					continue; // skip if diagonal movement is not allowed
				}
				node = new Node(this.now, this.now.x + x, this.now.y + y, this.now.g, this.distance(x, y));
				if ((x != 0 || y != 0) // not this.now
						&& this.now.x + x >= 0 && this.now.x + x < this.maze[0].length // check maze boundaries
						&& this.now.y + y >= 0 && this.now.y + y < this.maze.length
						&& this.maze[this.now.y + y][this.now.x + x] != -1 // check if square is walkable
						&& !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)) { // if not already done
					node.g = node.parent.g + 1.; // Horizontal/vertical cost = 1.0
					node.g += maze[this.now.y + y][this.now.x + x]; // add movement cost for this square
					this.open.add(node);
				}
			}
		}
		Collections.sort(this.open);
	}

	/**
	 * Compares the first node (current) with the second node (destination) to see which direction to go in
	 * @param first
	 * @param second
	 * @return	The direction that the robot has to go in
	 */
	public static int compare(Node first, Node second) {
		if (second.y < first.y) {return 1;}
		else if (second.x > first.x){return 2;}
		else if (second.y > first.y) {return 3;}
		else if (second.x < first.x) {return 4;}
		else {return 0;}
	}

	/**
	 * Changes the direction of the robot to be in the new direction n
	 * @param n
	 */
	public void changeDirection(int n) {
		direction = n;
	}

	/**
	 * Returns the direction the robot is currently facing
	 * @return
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Writes to file the string
	 * @param text
	 */
	public static void writeToFile(String text) {
		try {
			PrintWriter writer = new PrintWriter("Instructions.txt", "UTF-8");
			writer.print(text);
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Instructions.txt not found");
		}
	}

	/**
	 * Inserts a string into another string at index given
	 * @param bag
	 * @param marble
	 * @param index
	 * @return
	 */
	public String insert(String bag, String marble, int index) {
		String bagBegin = bag.substring(0,index);
		String bagEnd = bag.substring(index);
		return bagBegin + marble + bagEnd;
	}

	/**
	 * Given 2 chars (directions), check if they are different, and returns the turn needed.
	 * @param c
	 * @param d
	 * @return
	 */
	public String checkTurn(char c, char d) {
		int i = Character.getNumericValue(c);

		int j = Character.getNumericValue(d);

		if (i==1) {	//facing north
			if (j==2)
				return "R";  //turning east
			else if(j==4)
				return "L"; //turning west
		}
		if (i==2) { //facing east
			if (j==3)
				return "R"; //turning south
			else if(j==1)
				return "L"; // turning north
		}
		if (i==3) { //facing south
			if (j==4)
				return "R"; // turning west
			else if(j==2)
				return "L"; // turning east
		}
		if (i==4) { //facing west
			if (j==1)
				return "R"; //turning east
			else if(j==3)
				return "L"; // turning west
		}

		return "";
	}
	
	/**
	 * Replaces all 1, 2, 3, and 4s in the text with F1
	 * @param text
	 * @return
	 */
	public String replace(String text) {
		text = text.replaceAll("1", " F1 ");
		text = text.replaceAll("2", " F1 ");
		text = text.replaceAll("3", " F1 ");
		text = text.replaceAll("4", " F1 ");
		return text;
	}
	public static void main(String[] args) {
		// -1 = blocked
		// 0+ = additional movement cost

		 if(args.length == 0)
		    {
		        System.out.println("Proper Usage is: Java AStar int int");
		        System.exit(0);
		    }
		//initializing variables to input parameters
		int xstart = Integer.parseInt(args[0]);
		int ystart = Integer.parseInt(args[1]);
		int first =  Integer.parseInt(args[2]);
		int second = Integer.parseInt(args[3]);
		//add more variable assignments from args
		
		FileTo2DArray convert = new FileTo2DArray();
		int[][] maze = convert.getMap();
		int next = 0;
		AStar as = new AStar(maze, xstart, ystart, false);
		List<Node> path = as.findPathTo(first, second);	//travel from start location to object pickup location
		//List<Node> deliver = as.findPathTo(30, 30);
		
		if (path != null) {
			
//			Prints the entire path in coordinates
			path.forEach((n) -> {
				System.out.print("[" + n.x + ", " + n.y + "] ");
				maze[n.y][n.x] = -1;
			}
			);
	
//			Prints the total cost of the path
			System.out.printf("\nTotal cost: %.02f\n", path.get(path.size() - 1).g);

//			Prints the entire map visually
			for (int[] maze_row : maze) {
				for (int maze_entry : maze_row) {
					switch (maze_entry) {
					case 0:
						System.out.print(". ");
						break;
					case -1:
						System.out.print("* ");
						break;
					default:
						System.out.print("# ");
					}
				}
				System.out.println();
			}
			String instructions = "";

			//Gets next direction in the path and adds it to the instructions
			for (int i =0; i<path.size() - 1; i++) {
				next = compare(path.get(i), path.get(i+1));
				//if (next != as.getDirection()) {
				as.changeDirection(next);
				instructions += next;
				//}
			}

			//Checks if a turn is needed at the beginning, then adds it
			String turn = "";
			for (int i = 0; i<instructions.length()-1; i++) {
				if (instructions.charAt(i) != instructions.charAt(i+1)) {
					turn = as.checkTurn(instructions.charAt(i), instructions.charAt(i+1));
					instructions = as.insert(instructions, " " + turn + "90 ", i+1);
					i+=5;
				}
			}
			//check if first instruction needs a turn
			if (compare(path.get(0), path.get(1)) != 1) {
				next = compare(path.get(0), path.get(1));
				as.changeDirection(next);
				turn = as.checkTurn('1', instructions.charAt(0));
				instructions = as.insert(instructions, turn + "90 ", 0);
			}
			//replace all directions with Forward 1
			instructions = as.replace(instructions);
			
			//add an instruction that turns the robot to face North (0) at end of each run 
			
			//write the instructions to the file
			System.out.print(instructions);
			writeToFile(instructions);
			

		}
	}
}