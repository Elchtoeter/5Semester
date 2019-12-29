package Client;

import DataClasses.ObserveableUser;
import DataClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    private final ObservableList<ObserveableUser> users;

    public Model() {
        users = FXCollections.observableArrayList();
    }

    public ObservableList<ObserveableUser> getUsers() {
        return users;
    }

    public void addUser(User user) {
        ObserveableUser ouser = ObserveableUser.fromUser(user);
        users.add(ouser);
    }
}
