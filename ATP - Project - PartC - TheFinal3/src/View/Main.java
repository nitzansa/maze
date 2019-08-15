package View;
import Model.*;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        primaryStage.setTitle("Sapir&Nitzan's Super Maze");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("OpenWindow.fxml").openStream());
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        OpenController view = fxmlLoader.getController();
        String path = new File("Resources/justice_song.mp3").getAbsolutePath();
        Media song = new Media(new File(path).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(song);
        mediaPlayer.setVolume(0.8);
        view.setMediaPlayer(mediaPlayer);
        view.setSong(song);
        //view.setResizeEvent(scene);
        FXMLLoader fxmlLoader2 = new FXMLLoader();
        Parent root2 = fxmlLoader2.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene2 = new Scene(root2, 800, 700);
        MyViewController view2 = fxmlLoader2.getController();
        view2.setResizeEvent(scene2);
        view.setViewModel(viewModel);
        view.mediaPlayer.play();
        viewModel.addObserver(view);
        SetStageCloseEvent(primaryStage, model);
        primaryStage.show();
    }

    private void SetStageCloseEvent(Stage primaryStage, MyModel myModel) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    myModel.exit();
                    primaryStage.close();
                } else {
                    windowEvent.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//package View;
//import Model.*;
//import ViewModel.MyViewModel;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.event.Event;
//import javafx.event.EventHandler;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.ButtonType;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//
//import java.util.Optional;
//
//public class Main extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        MyModel model = new MyModel();
//        model.startServers();
//        MyViewModel viewModel = new MyViewModel(model);
//        model.addObserver(viewModel);
//        primaryStage.setTitle("Sapir&Nitzan's Super Maze");
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        Parent root = fxmlLoader.load(getClass().getResource("OpenWindow.fxml").openStream());
//        Scene scene = new Scene(root, 1100, 600);
//        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
//        primaryStage.setScene(scene);
//        OpenController view = fxmlLoader.getController();
//        view.setViewModel(viewModel);
//        viewModel.addObserver(view);
//        primaryStage.show();
//        EventHandler close = new EventHandler() {
//            @Override
//            public void handle(Event event) {
//                viewModel.exit();
//                Platform.exit();
//            }
//        };
//        primaryStage.setOnCloseRequest(close);
//    }
//
//    private void SetStageCloseEvent(Stage primaryStage) {
//
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
