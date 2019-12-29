package DataClasses;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class ObserveableUser {

    StringProperty name;
    StringProperty dob;
    StringProperty status;
    StringProperty role;
    IntegerProperty dayLeft;

    public ObserveableUser(){
        this.name = new SimpleStringProperty();
        this.dob = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.role = new SimpleStringProperty();
        this.dayLeft = new SimpleIntegerProperty();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDob() {
        return dob.get();
    }

    public StringProperty dobProperty() {
        return dob;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public int getDayLeft() {
        return dayLeft.get();
    }

    public IntegerProperty dayLeftProperty() {
        return dayLeft;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setDob(String dob) {
        this.dob.set(dob);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public void setDayLeft(int dayLeft) {
        this.dayLeft.set(dayLeft);
    }

    public static ObserveableUser fromUser(User user){
        ObserveableUser oUser = new ObserveableUser();
        oUser.dayLeft.setValue(user.getDaysLeft());
        oUser.dob.setValue(user.getDateOfBirth().toString());
        oUser.name.setValue(user.getName());
        oUser.role.setValue(user.getRole().name());
        oUser.status.setValue(user.getStatus().getStatus().name());
        return oUser;
    }
}
