package View;

import algorithms.mazeGenerators.Position;

public interface IView {
    void displayMaze(int[][] maze, Position goal);
}
