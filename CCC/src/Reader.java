import java.io.*;

public class Reader {

	private String filename;
	private BufferedReader read;

	public Reader(){
	}

	public void open(String filename){
		this.filename = filename;
		try {
			read = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Can't open file "+ filename);
		}
	}

	public String nextLine(){
		try {
			return read.readLine();
		} catch (IOException e) {
			System.out.println("trouble while reading opened file "+filename);
		}
		return null;
	}
}
