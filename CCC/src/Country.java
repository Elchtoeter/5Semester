import java.util.ArrayList;
import java.util.Objects;

public class Country implements Comparable<Country>{
	public final int country;
	private int borders;
	public int avgRow, avgCol;
	public ArrayList<CountryTile> tiles;
	public Country(int country) {
		this.country = country;
		this.borders = 0;
		this.tiles = new ArrayList<>();
	}

	public void addTile(CountryTile tile){
		this.tiles.add(tile);
	}

	public void setBorder(){
		borders++;
	}

	public String getBorder(){
		return String.valueOf(borders);
	}


	public void computeCenter(){
		avgRow = 0;
		avgCol = 0;
		for (CountryTile tile : tiles) {
			avgRow+=tile.row;
			avgCol+=tile.colum;
		}
		avgCol/=tiles.size();
		avgRow/=tiles.size();
	}

	@Override
	public String toString() {
		return String.format("Country %s has %s borders",this.country,this.borders);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Country country1 = (Country) o;
		return country == country1.country;
	}

	@Override
	public int hashCode() {
		return Objects.hash(country);
	}

	@Override
	public int compareTo(Country o) {
		return Integer.compare(this.country,o.country);
	}
}
