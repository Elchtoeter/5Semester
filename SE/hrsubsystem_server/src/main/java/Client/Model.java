package Client;

import DataClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

public class Model {
    
    private final ObservableList<User> users;

    public Model() {
        users = FXCollections.observableArrayList();
    }
}
