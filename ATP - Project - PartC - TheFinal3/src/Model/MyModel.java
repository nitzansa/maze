package Model;

import IO.MyCompressorOutputStream;
import Server.*;
import Client.*;
import java.io.*;
import Client.Client;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {
    private Maze myMaze;
    private int characterRow;
    private int characterColumn;
    private ArrayList<Position> solutionPath;
    private ExecutorService threadPool;
    private static Server mazeGeneratorServer;
    private static Server mazeSolveServer;
    private ServerStrategySolveSearchProblem solveStrategy;
    private ServerStrategyGenerateMaze generateStrategy;
    private Position goalPosition;


    public MyModel(){
        threadPool = Executors.newCachedThreadPool();
        myMaze = new Maze(0, 0, new Position(0,0), new Position(0,0));
        solutionPath = new ArrayList<>();
        mazeGeneratorServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        mazeSolveServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    @Override
    public void generateMaze(int width, int height) {
        clientGenerateMaze(width, height);
    }

    @Override
    public void solveMaze() {
        clientSolveMaze();
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case NUMPAD8:
                if (myMaze.getM_maze()[characterRow - 1][characterColumn] != 1)
                    characterRow--;
                break;
            case NUMPAD2:
                if (myMaze.getM_maze()[characterRow + 1][characterColumn] != 1)
                    characterRow++;
                break;
            case NUMPAD6:
                if (myMaze.getM_maze()[characterRow][characterColumn + 1] != 1)
                    characterColumn++;
                break;
            case NUMPAD4:
                if (myMaze.getM_maze()[characterRow][characterColumn - 1] != 1)
                    characterColumn--;
                break;
            case NUMPAD9: //up and right
                if (myMaze.getM_maze()[characterRow - 1][characterColumn + 1] != 1 &&
                        (myMaze.getM_maze()[characterRow - 1][characterColumn] == 0 ||
                                myMaze.getM_maze()[characterRow][characterColumn + 1] == 0)){
                    characterRow--;
                    characterColumn++;
                }
                break;
            case NUMPAD3: //right and down
                if (myMaze.getM_maze()[characterRow + 1][characterColumn + 1] != 1 &&
                        (myMaze.getM_maze()[characterRow + 1][characterColumn] == 0 ||
                                myMaze.getM_maze()[characterRow][characterColumn + 1] == 0)) {
                    characterRow++;
                    characterColumn++;
                }
                break;
            case NUMPAD1:
                if (myMaze.getM_maze()[characterRow + 1][characterColumn - 1] != 1 &&
                        (myMaze.getM_maze()[characterRow + 1][characterColumn] == 0 ||
                                myMaze.getM_maze()[characterRow][characterColumn - 1] == 0)){
                    characterRow++;
                    characterColumn--;
                }
                break;

            case NUMPAD7:
                if (myMaze.getM_maze()[characterRow - 1][characterColumn - 1] != 1 &&
                        (myMaze.getM_maze()[characterRow - 1][characterColumn] == 0 ||
                                myMaze.getM_maze()[characterRow][characterColumn - 1] == 0)) {
                    characterColumn--;
                    characterRow--;
                }
                break;
        }
        setChanged();
        notifyObservers();
    }

    public void startServers() {
        generateStrategy = new ServerStrategyGenerateMaze();
        solveStrategy = new ServerStrategySolveSearchProblem();
        mazeGeneratorServer = new Server(5400,1000, generateStrategy);
        mazeSolveServer = new Server(5401,1000, solveStrategy);
        mazeGeneratorServer.start();
        mazeSolveServer.start();
    }

    private void clientGenerateMaze (int rows, int columns){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, columns};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = ((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(rows * columns) + 20 + 2 * rows + 2 * columns + 4];
                        is.read(decompressedMaze);
                        myMaze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            characterRow = myMaze.getStartPosition().getRowIndex();
            characterColumn = myMaze.getStartPosition().getColumnIndex();
            goalPosition = myMaze.getGoalPosition();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }

    private void clientSolveMaze (){
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        myMaze.setM_startPosition(new Position(characterRow, characterColumn));
                        toServer.writeObject(myMaze);
                        toServer.flush();
                        Solution mazeSolution = (Solution)fromServer.readObject();
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        solutionPath.clear();
                        for(int i = 0; i < mazeSolutionSteps.size(); ++i) {
                            String [] position = mazeSolution.getSolutionPath().get(i).getState().split(",");
                            int row = Integer.parseInt(position[0].substring(1));
                            int column = Integer.parseInt(position[1].substring(0,position[1].length() - 1));
                            solutionPath.add(new Position(row, column));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }

    @Override
    public void save(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStream os = new MyCompressorOutputStream(fileOutputStream);
            os.write(myMaze.toByteArray());
            os.flush();
            os.close();
        } catch (IOException ex) { }
    }

    @Override
    public void load(File chosen) {
        byte savedMazeBytes[] = new byte[0];
        try {
            InputStream inputStream = new FileInputStream(chosen.getPath());
            InputStream is = new MyDecompressorInputStream(inputStream);
            savedMazeBytes = new byte[myMaze.toByteArray().length];
            is.read(savedMazeBytes);
            is.close();
            inputStream.close();
        } catch (FileNotFoundException e) { System.out.println("FileNotFoundException"); }
        catch (IOException e) { System.out.println("IOException"); }
        myMaze = new Maze(savedMazeBytes);
        characterRow = myMaze.getStartPosition().getRowIndex();
        characterColumn = myMaze.getStartPosition().getColumnIndex();
        setChanged();
        notifyObservers();
    }

    @Override
    public void exit() {
        mazeGeneratorServer.stop();
        mazeSolveServer.stop();
        threadPool.shutdown();
        System.exit(0);
    }


    @Override
    public int[][] getMaze() { return myMaze.getM_maze(); }

    public Maze getObjectMaze() { return myMaze; }


    @Override
    public int getCharacterPositionRow() { return characterRow; }

    @Override
    public int getCharacterPositionColumn() { return characterColumn; }

    public Position getGoalPosition () {return myMaze.getGoalPosition();}

    @Override
    public ArrayList<Position> getSolution() { return solutionPath; }
}