package View;

import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MazeDisplayer extends Canvas {
    private int[][] maze;
    private int characterRow;
    private int characterColumn;
    private Position goalPosition;
    private StringProperty ImageFileNameWonderWoman = new SimpleStringProperty();
    private StringProperty ImageFileNameSuperman = new SimpleStringProperty();
    private StringProperty ImageFileNameBatman = new SimpleStringProperty();
    private StringProperty ImageFileNameAres = new SimpleStringProperty();
    private StringProperty ImageFileNameJoker = new SimpleStringProperty();
    private StringProperty ImageFileNameLexLuthor = new SimpleStringProperty();
    private StringProperty ImageFileNameWonderHint = new SimpleStringProperty();
    private StringProperty ImageFileNameSuperHint = new SimpleStringProperty();
    private StringProperty ImageFileNameBatHint = new SimpleStringProperty();
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private ImageView gif;
    private Image myHint;
    private Image myCharacter;
    private Image myGoal;
    private Image myWall;

    public void defaultPlayer(){
        try {
            myCharacter = new Image(new FileInputStream(ImageFileNameWonderWoman.get()));
            myGoal = new Image(new FileInputStream(ImageFileNameAres.get()));
            myHint = new Image(new FileInputStream(ImageFileNameWonderHint.get()));
            myWall = new Image(new FileInputStream(ImageFileNameWall.get()));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    public void setPlayer (StringProperty character, StringProperty goal, StringProperty hint){
        try {
            myCharacter = new Image(new FileInputStream(character.get()));
            myGoal = new Image(new FileInputStream(goal.get()));
            myHint = new Image(new FileInputStream(hint.get()));
            myWall = new Image(new FileInputStream(ImageFileNameWall.get()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void WonderWoman() {
        setPlayer (ImageFileNameWonderWoman, ImageFileNameAres, ImageFileNameWonderHint);
        redraw();
    }

    public void Superman() {
        setPlayer (ImageFileNameSuperman, ImageFileNameLexLuthor, ImageFileNameSuperHint);
        redraw();
    }

    public void Batman() {
        setPlayer (ImageFileNameBatman, ImageFileNameJoker, ImageFileNameBatHint);
        redraw();
    }


    public void redraw() {
        if (myCharacter == null)
            defaultPlayer();
        double canvasHeight = getWidth();
        double canvasWidth = getHeight();
        double cellHeight = canvasHeight / maze[0].length;
        double cellWidth = canvasWidth / maze.length;
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        //Draw Maze
        for (int i = 0; i < maze[0].length; i++) {
            for (int j = 0; j < maze.length; j++) {
                if (maze[j][i] == 1)
                    gc.drawImage(myWall, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                else{
                    gc.setFill(Color.SILVER);
                    gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                }
                if (j == goalPosition.getRowIndex() && i == goalPosition.getColumnIndex())
                    gc.drawImage(myGoal, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
            }
        }
        gc.drawImage(myCharacter, getCharacterPositionColumn() * cellHeight, getCharacterPositionRow() * cellWidth, cellHeight, cellWidth);
    }

    public void redrawSol(ArrayList<Position> solutionPath) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        double canvasHeight = getWidth();
        double canvasWidth = getHeight();
        double cellHeight = canvasHeight / maze[0].length;
        double cellWidth = canvasWidth / maze.length;
        redraw();
        for (int i = 1; i < solutionPath.size() - 1; i++)
            gc.drawImage(myHint, solutionPath.get(i).getColumnIndex() * cellHeight, solutionPath.get(i).getRowIndex() * cellWidth, cellHeight, cellWidth);
    }

    public void redrawHint(ArrayList<Position> solutionPath) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getHeight(), getWidth());
        double canvasHeight = getWidth();
        double canvasWidth = getHeight();
        double cellHeight = canvasHeight / maze[0].length;
        double cellWidth = canvasWidth / maze.length;
        redraw();
        gc.drawImage(myHint, solutionPath.get(1).getColumnIndex() * cellHeight, solutionPath.get(1).getRowIndex() * cellWidth, cellHeight, cellWidth);
    }

    /*
    Getters
     */
    public int getCharacterPositionRow() {
        return characterRow;
    }

    public int getCharacterPositionColumn() {
        return characterColumn;
    }

    public void setCharacterPosition(int characterPositionRow, int characterPositionColumn) {
        characterRow = characterPositionRow;
        characterColumn = characterPositionColumn;
        redraw();
    }

    public void setGoalPosition (Position position){
        goalPosition = new Position(position.getRowIndex(), position.getColumnIndex());
    }

    public void setMaze(int[][] m_maze) {
        maze = m_maze;
        redraw();
    }

    public String getImageFileNameWonderWoman() { return ImageFileNameWonderWoman.get(); }

    public String getImageFileNameSuperman() { return ImageFileNameSuperman.get(); }

    public String getImageFileNameBatman() { return ImageFileNameBatman.get(); }

    public String getImageFileNameAres() { return ImageFileNameAres.get(); }

    public String getImageFileNameJoker() { return ImageFileNameJoker.get(); }

    public String getImageFileNameLexLuthor() { return ImageFileNameLexLuthor.get(); }

    public String getImageFileNameWonderHint() { return ImageFileNameWonderHint.get(); }

    public String getImageFileNameSuperHint() { return ImageFileNameSuperHint.get(); }

    public String getImageFileNameBatHint() { return ImageFileNameBatHint.get(); }

    public String getImageFileNameWall() { return ImageFileNameWall.get(); }

    public StringProperty imageFileNameWonderWomanProperty() { return ImageFileNameWonderWoman; }
    public StringProperty imageFileNameSupermanProperty() { return ImageFileNameSuperman; }
    public StringProperty imageFileNameBatmanProperty() { return ImageFileNameBatman; }
    public StringProperty imageFileNameAresProperty() { return ImageFileNameAres; }
    public StringProperty imageFileNameJokerProperty() { return ImageFileNameJoker; }
    public StringProperty imageFileNameLexLuthorProperty() { return ImageFileNameLexLuthor; }
    public StringProperty imageFileNameWonderHintProperty() { return ImageFileNameWonderHint; }
    public StringProperty imageFileNameSuperHintProperty() { return ImageFileNameSuperHint; }
    public StringProperty imageFileNameBatHintProperty() { return ImageFileNameBatHint; }
    public StringProperty imageFileNameWallProperty() { return ImageFileNameWall; }

    public void setImageFileNameWonderWoman(String imageFileNameWonderWoman) { this.ImageFileNameWonderWoman.set(imageFileNameWonderWoman); }

    public void setImageFileNameSuperman(String imageFileNameSuperman) { this.ImageFileNameSuperman.set(imageFileNameSuperman); }

    public void setImageFileNameBatman(String imageFileNameBatman) { this.ImageFileNameBatman.set(imageFileNameBatman); }

    public void setImageFileNameAres(String imageFileNameAres) { this.ImageFileNameAres.set(imageFileNameAres); }

    public void setImageFileNameJoker(String imageFileNameJoker) { this.ImageFileNameJoker.set(imageFileNameJoker); }

    public void setImageFileNameLexLuthor(String imageFileNameLexLuthor) { this.ImageFileNameLexLuthor.set(imageFileNameLexLuthor); }

    public void setImageFileNameWonderHint(String imageFileNameWonderHint) { this.ImageFileNameWonderHint.set(imageFileNameWonderHint); }

    public void setImageFileNameSuperHint(String imageFileNameSuperHint) { this.ImageFileNameSuperHint.set(imageFileNameSuperHint); }

    public void setImageFileNameBatHint(String imageFileNameBatHint) { this.ImageFileNameBatHint.set(imageFileNameBatHint); }

    public void setImageFileNameWall(String imageFileNameWall) { this.ImageFileNameWall.set(imageFileNameWall); }
}