package Client;

import DataClasses.EmploymentStatus;
import DataClasses.ObserveableUser;
import DataClasses.Pay;
import DataClasses.User;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;


public class Controller {

    private final Model model;

    @FXML
    private TableView<ObserveableUser> mainTable;

    @FXML
    private TableColumn<ObserveableUser, String> name;

    @FXML
    private TableColumn<ObserveableUser, String> dob;

    @FXML
    private TableColumn<ObserveableUser, String> status;

    @FXML
    private TableColumn<ObserveableUser, Integer> daysLeft;

    private Stage primaryStage;

    public Controller() {
        primaryStage = null;
        this.model = new Model();
        model.addUser(new User(User.Role.USER,"Bernhard Fuerst",LocalDate.of(1985,9,3),new Pay(012),12, new EmploymentStatus(012, EmploymentStatus.EStatus.EMPLOYED)));
    }

    @FXML
    void initialize(){
       mainTable.itemsProperty().setValue(model.getUsers());
       name.setCellValueFactory(new PropertyValueFactory<>("name"));
       dob.setCellValueFactory(new PropertyValueFactory<>("dob"));
       status.setCellValueFactory(new PropertyValueFactory<>("status"));
       daysLeft.setCellValueFactory(new PropertyValueFactory<>("dayLeft"));
    }

}
