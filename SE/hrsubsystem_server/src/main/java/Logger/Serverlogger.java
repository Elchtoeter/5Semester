package Logger;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Serverlogger {

    final private Logger instance = Logger.getGlobal();
    private FileHandler logFile;

}
