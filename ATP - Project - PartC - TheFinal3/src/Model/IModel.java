package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.util.ArrayList;

public interface IModel {
    int[][] getMaze();
    Maze getObjectMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void generateMaze(int height, int width);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    void save(File chosen);
    void load(File chosen);
    void exit();
    ArrayList<Position> getSolution();
    Position getGoalPosition();
}