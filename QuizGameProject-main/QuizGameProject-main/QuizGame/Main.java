import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class Main extends Application {
    public static String userId;

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("ইউজার ID");
        dialog.setHeaderText("Quiz শুরু করার আগে আপনার ID লিখুন:");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            userId = result.get();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz.fxml"));
            primaryStage.setTitle("ইতিহাস কুইজ");
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
