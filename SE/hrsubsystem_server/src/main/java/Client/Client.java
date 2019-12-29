package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        System.out.println("blah");
        loader.setLocation(getClass().getResource("/Client.fxml"));
        AnchorPane vBox = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(vBox);
        RestClient r = new RestClient();
        System.out.println(r.convertToJson());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
