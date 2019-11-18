import java.util.*;

public class CCC {
	public static void main(String[] args) {
		Reader r = new Reader();

		for (int i = 0; i < 5; i++) {
			r.open("level3_"+(i+1)+".in");
			String rowCols = r.nextLine();
			int rows = Integer.parseInt(rowCols.split(" ")[0]);
			int cols = Integer.parseInt(rowCols.split(" ")[1]);
			SortedMap<Integer,Country> countries = new TreeMap<>();
			CountryTile[][] earth = new CountryTile[rows][cols];
			for (int i1 = 0; i1 < rows; i1++) {
				String[] line = r.nextLine().split(" ");
				for (int i2 = 0; i2 < (cols); i2++) {
					int country = Integer.parseInt(line[i2 * 2 + 1]);
					if (!countries.containsKey(country)) {
						countries.put(country,new Country(country));
					}
					Country c = countries.get(country);
					earth[i1][i2] = new CountryTile(Integer.parseInt(line[i2 * 2]), c,i1,i2);

					c.addTile(earth[i1][i2]);
				}
			}
			for (int j = 0; j < earth.length; j++) {
				for (int k = 0; k < earth[j].length; k++) {
					int c = earth[j][k].country.country;
					int c1, c2, c3, c4;
					try {
						c1 = earth[j - 1][k].country.country;
						c2 = earth[j][k + 1].country.country;
						c3 = earth[j + 1][k].country.country;
						c4 = earth[j][k - 1].country.country;
					} catch (IndexOutOfBoundsException e) {
						earth[j][k].setBorder();
						continue;
					}
					if (c != c1 || c != c2 || c != c3 || c != c4) earth[j][k].setBorder();
				}
			}

			Writer w = new Writer();
			w.open("level3_"+(i+1)+".out");
			for (Integer integer : countries.keySet()) {
				Country c = countries.get(integer);
				c.computeCenter();
				if (earth[c.avgRow][c.avgCol].country.equals(c)&&!earth[c.avgRow][c.avgCol].isBorder) {
					w.writeLine(countries.get(integer).avgCol + " " + countries.get(integer).avgRow);
				} else {
					CountryTile center = new CountryTile(0,c,c.avgRow,c.avgCol);
					double minDist = Double.MAX_VALUE;
					CountryTile nearest = null;
					for (CountryTile tile : c.tiles) {
						if (!tile.isBorder){
							if (tile.computeDistance(center)<minDist){
								minDist = tile.computeDistance(center);
								nearest = tile;
							}
						}
					}
					w.writeLine(nearest.colum + " " + nearest.row);
				}
			}

			//Writer w = new Writer();
			//w.open("level2_"+(i+1)+".out");
		}

	}
}
