package View;

import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import algorithms.mazeGenerators.Position;


public class MyViewController implements Observer, IView, Initializable {
    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Button btn_generateMazeNew;
    public javafx.scene.control.Button btn_solveMazeNew;
    public javafx.scene.control.MenuItem item_newMaze;
    public javafx.scene.control.MenuItem item_save;
    public javafx.scene.control.MenuItem item_help;
    public javafx.scene.control.MenuItem item_about;
    public javafx.scene.control.Button btn_hint;
    public javafx.scene.control.Button btn_quit;
    private MediaPlayer mediaPlayer;
    private Media song;
    public BorderPane borderPane;
    public AnchorPane anchorPane;
    public ComboBox<String> comboBox;
    public ObservableList<String> players = FXCollections.observableArrayList("Wonderwoman", "Superman", "Batman");
    public VBox vBox;

    public void setViewModel(MyViewModel myViewModel) { viewModel = myViewModel;}


    @Override
    public void displayMaze(int[][] maze, Position goal) {
        mazeDisplayer.setGoalPosition(goal);
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        if (viewModel.getGoalPosition() != null) {
            if (viewModel.getGoalPosition().getRowIndex() == characterPositionRow && viewModel.getGoalPosition().getColumnIndex() == characterPositionColumn) {
                mediaPlayer.stop();
                String path = new File("Resources/song_to_maze.mp3").getAbsolutePath();
                song = new Media(new File(path).toURI().toString());
                mediaPlayer = new MediaPlayer(song);
                mediaPlayer.setVolume(0.8);
                mediaPlayer.play();
//                FXMLLoader fxmlLoader = new FXMLLoader();
//                Parent root = null;
//                try {
//                    root = fxmlLoader.load(getClass().getResource("End.fxml").openStream());
//                } catch (IOException e) {
//                }
//                Scene scene = new Scene(root, 570, 315);
//                Stage stage = new Stage();
//                stage.setScene(scene);
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.show();
                String content = "Start a new game, load a game or exit";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Congratulations - You help to save the world!");
                alert.setHeaderText(null);
                alert.setContentText(content);
                alert.show();
            }
        }
    }


    public void generateMaze() {
        if (comboBox.getItems().size() != 3)
            initialize();
        int height = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMazeNew.setDisable(true);
        item_save.setDisable(false);
        btn_solveMazeNew.setDisable(false);
        btn_hint.setDisable(false);
        viewModel.generateMaze(height, width);
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
        mazeDisplayer.redrawSol(viewModel.getSolutionPath());
    }

    public void hint() {
        viewModel.hint();
        mazeDisplayer.redrawHint(viewModel.getSolutionPath());
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable == viewModel) {
            displayMaze(viewModel.getMaze(), viewModel.getGoalPosition());
            btn_generateMazeNew.setDisable(false);
        }
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void changeScene(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent view = fxmlLoader.load(getClass().getResource("OpenWindow.fxml").openStream());
        Scene scene = new Scene(view, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        OpenController controller = fxmlLoader.getController();
        controller.setViewModel(viewModel);
        viewModel.addObserver(controller);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
        EventHandler close = new EventHandler() {
            @Override
            public void handle(Event event) {
                viewModel.exit();
                Platform.exit();
            }
        };
        window.setOnCloseRequest(close);
    }

//    public void changeSceneMenueItem(ActionEvent event) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        Parent view = fxmlLoader.load(getClass().getResource("OpenWindow.fxml").openStream());
//        Scene sceneNew = new Scene(view);
//        OpenController controller = fxmlLoader.getController();
//        controller.setViewModel(viewModel);
//        viewModel.addObserver(controller);
//        Stage window = (Stage) menuBar.getScene().getWindow();
//        window.setScene(sceneNew);
//        window.show();
//    }

    public void save() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Saving the maze");
        fc.setInitialDirectory(null);
        File file = fc.showSaveDialog((Stage) mazeDisplayer.getScene().getWindow());
        if (file != null)
            viewModel.save(file);
    }

    public void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File chosen = fileChooser.showOpenDialog((Stage) mazeDisplayer.getScene().getWindow());
        if (chosen != null) {
            generateMaze();
            viewModel.load(chosen);
            mazeDisplayer.redraw();
        }
    }


    public void properties() {
        String text = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("Resources/config.properties"));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line + ",");
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            text = stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException e) {
        }
        String[] split = text.split(",");
        String content = "";
        content = "The solving maze algorithm is: " + splitLine(split[0] + "\n");
        content += "The generator algorithm is: " + splitLine(split[1].substring(4) + "\n");
        content += "The default Number of threads is: " + splitLine(split[2].substring(4) + "\n");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game properties");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    private String splitLine(String s) {
        String[] splitLine = s.split("=");
        return splitLine[1];
    }

    public void About(ActionEvent actionEvent) {
        String content = "Our names are Nitzan Sabag and Sapir Ratzon and we created this amazing supermaze.\n" +
                "In this game we used a few algorithms:\n" +
                "first, the algorithm for the generation of the maze is: Randomized Prim's algorithm.\n" +
                "second, the algorithm to solve the maze is: Best First Search algorithm.";
        Alert alert = new  Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Little about us..");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();

    }

    public void help(){
        String content = "You can choose a superHero and help him to catch his enemy for a better world! \n" +
                "This is the rules:\n -You can't get out of the frame.\n" +
                "-You can only move up, down, right, left or in diagonals.\n" +
                "-No stepping on the walls.\n" +
                "-The most important - Have fun and tell about us to your friends! :)";
        Alert alert = new  Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    public void exit(){
        viewModel.exit();
        Platform.exit();
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.redraw();
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_solveMazeNew.setDisable(true);
        btn_hint.setDisable(true);
    }


//    private void getChoice(ChoiceBox<String> choiceBox){ String choice = choiceBox.getValue(); }
//
//    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
//
//    public Media getSong() { return song; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) { this.mediaPlayer = mediaPlayer; }

    public void setSong(Media song) { this.song = song; }

    @FXML
    public void initialize (){
        comboBox.getItems().addAll("Wonderwoman", "Superman", "Batman");
        comboBox.setDisable(false);
    }

    @FXML
    public void comboBoxChoice() {
        if (comboBox.getValue().equals("Wonderwoman"))
            WonderWoman();
        else if (comboBox.getValue().equals("Superman"))
            Superman();
        else if (comboBox.getValue().equals("Batman"))
            Batman();
    }

    public void WonderWoman() {
        generateMaze();
        mazeDisplayer.WonderWoman(); }

    public void Superman() {
        generateMaze();
        mazeDisplayer.Superman(); }

    public void Batman() {
        generateMaze();
        mazeDisplayer.Batman(); }
}
