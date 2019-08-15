package View;

import algorithms.mazeGenerators.Position;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.canvas.Canvas;

public class OpenController extends Canvas implements Observer, IView{
    @FXML
    private MyViewModel viewModel;
    public javafx.scene.control.Button btn_start;
    public MediaPlayer mediaPlayer;
    public Media song;


    public void setViewModel(MyViewModel myViewModel) {
        viewModel = myViewModel;
    }

    @Override
    public void displayMaze(int[][] maze, Position goal) { }

    @Override
    public void update(Observable observable, Object arg) { }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void changeSceneButton (ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent New = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene sceneNew = new Scene(New, 1000, 650);
        MyViewController controller = fxmlLoader.getController();
        controller.setViewModel(viewModel);
        viewModel.addObserver(controller);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(sceneNew);
        window.show();
        controller.setResizeEvent(sceneNew);
        EventHandler close = new EventHandler() {
            @Override
            public void handle(Event event) {
                viewModel.exit();
                Platform.exit();
            }
        };
        controller.setMediaPlayer(mediaPlayer);
        controller.setSong(song);
        window.setOnCloseRequest(close);
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }

    public void setSong(Media song) { this.song = song; }
}
