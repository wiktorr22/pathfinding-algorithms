package com.example.demo;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.util.Random;

public class Node extends Button {

    private Node Parent;
    int col;
    int row;
    private int cost;
    int gCost;
    int hCost;
    int fCost;
    boolean isCornerNode = false;

    public static boolean mouseMovedActive = false;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
        setCost();
    }

    public int getgCost() {
        return gCost;
    }


    public int getfCost() {
        return fCost;
    }

    private void setCost() {
        Random random = new Random();
        int num = random.nextInt(1,15);
        this.cost = num;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Node getPar() {
        return Parent;
    }

    public void setParent(Node parent) {
        Parent = parent;
    }

    public void setActionActive() {
            this.setOnMouseMoved(new EventHandler() {
                @Override
                public void handle(Event event) {
                    if (mouseMovedActive) {
                        setId("obstacle");
                    }
                }
            });
    }

    public void setMovedActive() {
        this.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!mouseMovedActive)
                    mouseMovedActive = true;
                else
                    mouseMovedActive = false;
            }
        });
    }
}





