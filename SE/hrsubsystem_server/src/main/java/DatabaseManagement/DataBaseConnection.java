package DatabaseManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DataBaseConnection {
    INSTANCE;

    static {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        try {
            Class.forName(driver);
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final String DB_URL = "jdbc:derby:HRDB;create=true";

    private Connection dbconnect = null;

    public void openConnection(){
        if (dbconnect!=null){
            throw  new IllegalStateException("previous connection still open");
        }
        try {
            dbconnect = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
