import java.util.ArrayList;

public class RayCompute {
	public static void main(String[] args) {
		for (int f = 0; f < 5; f++) {
			ArrayList<Ray> rays = new ArrayList<>();
			Reader r = new Reader();
			r.open("level4_"+(f+1)+".in");
			String[] line = r.nextLine().split(" ");
			int rows = Integer.parseInt(line[0]);
			int cols = Integer.parseInt(line[1]);
			line = r.nextLine().split(" ");
			int queries = Integer.parseInt(line[0]);
			for (int q = 0; q < queries; q++) {
				line = r.nextLine().split(" ");
				Ray ray = new Ray(Integer.parseInt(line[0]),Integer.parseInt(line[1]),Integer.parseInt(line[2]),Integer.parseInt(line[3]));
				rays.add(ray);
			}
		}
	}
}
