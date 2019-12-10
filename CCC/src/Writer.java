import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

	private File outfile;
	private BufferedWriter write;

	public void open(String file){
		outfile = new File(file);
		if (!outfile.exists()){
			try {
				outfile.createNewFile();
			} catch (IOException e) {
				System.out.println("badonk");
			}
		}
		try {
			write = new BufferedWriter(new FileWriter(outfile));
		} catch (IOException e) {
			System.out.println("Can't open output file");
		}
	}

	public void writeLine(String toBeWritten){
		try {
			write.write(toBeWritten+"\n");
			write.flush();
		} catch (IOException e) {
			System.out.println("Can't write to output file " + outfile);
		}
	}
}
