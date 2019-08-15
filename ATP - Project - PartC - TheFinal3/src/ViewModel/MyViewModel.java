package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Position;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.KeyCode;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    public ObservableValue<? extends String> characterPositionRow;
    private IModel model;
    private int characterRowIndex;
    private int characterColumnIndex;
    private ArrayList<Position> solutionPath;

    public MyViewModel(IModel m_model){ model = m_model; }

    public void generateMaze(int width, int height) { model.generateMaze(width, height); }

    public void solveMaze() {
        model.solveMaze();
        solutionPath = model.getSolution();
    }

    public void hint (){
        model.solveMaze();
        solutionPath = model.getSolution();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable == model){
            characterRowIndex = model.getCharacterPositionRow();
            characterColumnIndex = model.getCharacterPositionColumn();
            setChanged();
            notifyObservers();
        }
    }

    public void save(File file) {
        model.save(file);
    }

    public void moveCharacter(KeyCode move) { model.moveCharacter(move); }

    public int getCharacterPositionRow() { return characterRowIndex; }

    public int getCharacterPositionColumn() { return characterColumnIndex; }

    public Position getGoalPosition() { return model.getGoalPosition(); }

    public int[][] getMaze() { return model.getMaze(); }

    public ArrayList<Position> getSolutionPath(){ return solutionPath; }

    public void exit() { model.exit(); }

    public void load(File chosen) { model.load(chosen); }
}