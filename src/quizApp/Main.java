package quizApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("StartPage.fxml"));
        primaryStage.setTitle("Quiz App");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        Security.addProvider(new BouncyCastleProvider());



    }


    public static void main(String[] args) {
        launch(args);

    }
}
