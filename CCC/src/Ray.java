import java.util.ArrayList;

public class Ray {
	public final int xOrigin, yOrigin, xDir, yDir;

	public ArrayList<Cell> touchedCells;

	public Ray(int xOrigin, int yOrigin, int xDir, int yDir) {
		this.xOrigin = xOrigin;
		this.yOrigin = yOrigin;
		this.xDir = xDir;
		this.yDir = yDir;
		this.touchedCells = new ArrayList<>();
	}

	public void computeTouchedCells(int rows, int cols){
		if (Math.abs(rows)>Math.abs(cols)){
			if (this.xDir>0)
				if (this.yDir>0){
					for (Integer yplus : getYplus(cols)) {
						for (Integer xplus : getXplus(rows)) {
							checkCell
						}
					}
				}
		}
	}

	private ArrayList<Integer> getXplus(int rows){
		ArrayList<Integer> ret = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			ret.add(i);
		}
		return ret;
	}
	private ArrayList<Integer> getXneg(int rows){
		ArrayList<Integer> ret = new ArrayList<>();
		for (int i = rows; i > 0; i--) {
			ret.add(i);
		}
		return ret;
	}
	private ArrayList<Integer> getYneg(int cols){
		ArrayList<Integer> ret = new ArrayList<>();
		for (int i = cols; i > 0; i--) {
			ret.add(i);
		}
		return ret;
	}
	private ArrayList<Integer> getYplus(int cols){
		ArrayList<Integer> ret = new ArrayList<>();
		for (int i = 0; i < cols; i++) {
			ret.add(i);
		}
		return ret;
	}
}
