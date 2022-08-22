package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

public class RandomMap extends Application {

    final int maxCol = 40;
    final int maxRow = 30;
    final int nodeSize = 25;
    final int screenWidth = nodeSize * maxCol;
    final int screenHeight = nodeSize * maxRow;
    GridPane gridPane;
    Stage stage;
    Node goal;
    Node start;

    Pair<List<Node>,List<Node>> pair;
    List<Node> solution;
    List<Node> seenNodes;
    int routeIndex = 0;
    int seenIndex = 0;

    Node[][] nodes = new Node[maxCol][maxRow];

    Timeline routeTimeLine;
    Timeline seenTimeLine;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;
        gridPane = setGridPane();

        setNodes();

        Scene scene = new Scene(gridPane, screenWidth, screenHeight);
        scene.getStylesheets().add("/gridStyle.css");
        stage.setTitle("Hello!");
        stage.setScene(scene);
        this.routeTimeLine = new Timeline(new KeyFrame(Duration.millis(45), this::showRoad));
        this.seenTimeLine= new Timeline(new KeyFrame(Duration.millis(10), this::showSeenNodes));

        openWindow();
    }

    /**
     * chooses the algorithm to run, also you can decide to hide values of the nodes.
     * Afterwords check if algorithm has found a connections
     * and shows seen nodes and the best route
     */
    public void openWindow() {
        createObstacle();
        //showNodesValue(); // decide if nodes have a value or not

        //pair= Algorithm.breadthFirstSearch(nodes, start, goal);
        //pair= Algorithm.depthFirstSearch(nodes, start, goal);
        //pair = Algorithm.dijkstra(nodes, start, goal);
        pair = Algorithm.aStar(nodes, start, goal);

        checkMap();
        solution = pair.getKey();
        seenNodes = pair.getValue();
        stage.show();

        this.routeIndex = 0;
        this.seenIndex = 0;
        this.routeTimeLine.setCycleCount(solution.size());
        this.seenTimeLine.setCycleCount(seenNodes.size()+ 1);
        startSeenTimeLine();
    }


    /**
     * paint cells which are on the shortest road
     * between start and goal
     * if finished stops the timeLine
     */
    public void showRoad(ActionEvent event) {
        if (routeIndex < solution.size()) {
            Node node = solution.get(routeIndex);
            if (!node.getId().equals("goal") && !node.getId().equals("start")) {
                node.setId("checked");
            }
            routeIndex++;
        }
        else {
            stopRouteTimeLine();
        }

    }

    /**
     * paint cells which have been seen
     * to find a path between star and goal
     * if finished stops the timeLine and start showing the optimal route
     */
    private void showSeenNodes(ActionEvent event) {
        if (seenIndex < seenNodes.size()) {
            Node node = seenNodes.get(seenIndex);
            if (!node.getId().equals("goal") && !node.getId().equals("start")) {
                node.setId("seen");
            }
            seenIndex++;
        }
        else {
            stopSeenTimeLine();
            startRouteTimeLine();
        }
    }


    public void startRouteTimeLine() {
        routeTimeLine.play();
    }

    public  void stopRouteTimeLine() {
        this.routeTimeLine.stop();
    }

    public void startSeenTimeLine() {
        seenTimeLine.play();
    }

    public  void stopSeenTimeLine() {
        this.seenTimeLine.stop();
    }

    /**
     * checks if there is a route from start to road
     * otherwise creates a new map and repeats a process
     */
    public void checkMap() {
        if (pair == null) {
            System.out.println("new try");
            setNodes();
            openWindow();
        }
    }

    /**
     * create randomly obstacle on the map.
     * the size varies from allCells/3 to allCells/2;
     * also check if a given cell is neither goal cell nor start cell
     */
    public void createObstacle() {
        Random random = new Random();
        int number = random.nextInt(maxRow * maxCol / 3, maxRow * maxCol / 2);

        for (int i = 0; i < number; i++) {
            int randomCol = random.nextInt(maxCol);
            int randomRow = random.nextInt(maxRow);
            if (randomCol == start.col && randomRow == start.row
                    || randomCol == goal.col && randomRow == goal.row) {
                continue;
            }
            Node node = nodes[randomCol][randomRow];
            node.setId("obstacle");
            node.setText("");
            node.setCost(1000);
        }


    }

    /**
     * set cost of every node in the map to 1,
     * so they are equal;
     */
    public void showNodesValue() {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j].setText(String.valueOf(nodes[i][j].getCost()));
            }
        }
    }

    /**
     * Find a cell on right side of the map
     * and set is as a goal node
     * @return Goal node
     */
    public Node setGoal() {
        Random random = new Random();
        int randomRow = random.nextInt(maxRow);
        int randomCol = random.nextInt(maxCol);
        while (randomCol < maxCol / 2 + 1) {
            randomCol = random.nextInt(maxCol);
        }
        Node node = nodes[randomCol][randomRow];
        node.setId("goal");
        node.setText("goal");
        return node;
    }

    /**
     * Finds a cell on left side of the map
     * and additionally checks if it is not
     * a goal cell
     * @return Start node
     */
    public Node setStart() {
        Random random = new Random();
        int randomRow = random.nextInt(maxRow);
        int randomCol = random.nextInt(maxCol);
        while (randomCol > maxCol / 2 - 1) {
            randomCol = random.nextInt(maxCol);
        }
        Node node = nodes[randomCol][randomRow];

        while (node.getId().equals("goal")) {
            randomRow = random.nextInt(maxRow);
            randomCol = random.nextInt(maxCol);
            node = nodes[randomCol][randomRow];
        }

        node.setId("start");
        node.setText("start");
        return node;
    }

    /**
     * creates a node map based on the maxCol and maxRow
     * modifying a given gridPane
     * also generates a maxCol*maxRow number of nodes (one for each cell)
     *
     * setting their cost from 0 to 15(can be changed in the node class)
     * (relevant for Dijkstra algorithm)
     */
    public void setNodes() {

        int col = 0;
        int row = 0;

        while (col < maxCol && row < maxRow) {
            Node node = new Node(col, row);
            nodes[col][row] = node;
            nodes[col][row].setId("open");

            node.setMinSize(screenWidth / maxCol, screenHeight / maxRow);
            node.setStyle("-fx-border-color: black;");

            GridPane.setConstraints(nodes[col][row], col, row);
            gridPane.getChildren().add(nodes[col][row]);
            col++;
            if (col == maxCol) {
                col = 0;
                row++;
            }
        }
        goal = setGoal();
        start = setStart();

    }

    /**
     * sets up a gridPane
     * @return created grid pane
     */
    public GridPane setGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPrefHeight(screenHeight);
        gridPane.setPrefWidth(screenWidth);
        gridPane.setStyle("-fx-base: white");
        gridPane.setGridLinesVisible(true);
        for (int i = 0; i < maxCol; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setFillWidth(true);
            colConst.setMinWidth(screenWidth / maxCol);
            gridPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < maxRow; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setFillHeight(true);
            rowConst.setMinHeight(screenHeight / maxRow);
            gridPane.getRowConstraints().add(rowConst);
        }
        gridPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("N")) {
                setNodes();
                openWindow();
            }
        });
        return gridPane;
    }
}