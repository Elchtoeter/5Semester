import java.util.Objects;

public class CountryTile {
	final public int height;
	final public Country country;
	public int row;
	public int colum;
	public boolean isBorder = false;
	public CountryTile(int height,Country country,int row,int colum){
		this.height = height;
		this.country = country;
		this.row = row;
		this.colum =colum;
	}

	public void setBorder(){
		this.isBorder = true;
	}

	@Override
	public String toString() {
		return String.format("tile in country %s and at height %s",this.country,this.height);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CountryTile that = (CountryTile) o;
		return height == that.height &&
				row == that.row &&
				colum == that.colum &&
				Objects.equals(country, that.country);
	}

	public double computeDistance(CountryTile tile){
		return Math.sqrt(Math.pow(Math.abs(this.row-tile.row),2)+Math.pow(Math.abs(this.colum-tile.colum),2));
	}

	@Override
	public int hashCode() {
		return Objects.hash(height, country, row, colum);
	}
}
