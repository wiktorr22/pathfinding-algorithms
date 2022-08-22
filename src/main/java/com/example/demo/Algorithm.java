package com.example.demo;

import javafx.util.Pair;

import java.util.*;

public class Algorithm {


    public static Pair<List<Node>, List<Node>> dijkstra(Node[][] nodes, Node start, Node goal) {

        Map<Node, Integer> currentCost = new HashMap<>();
        List<Node> seen = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        currentCost.put(start, start.getCost());
        Queue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(currentCost::get));
        queue.add(start);
        Node current = null;
        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current == goal) {
                System.out.println("found");
                break;
            }
            if (visited.contains(current) || current.getId().equals("obstacle")) {
                continue;
            }
            visited.add(current);
            if (current != start) {
                seen.add(current);
            }
            List<Node> neighbours = new ArrayList<>();
            addNeighbours(current, neighbours, nodes, false);

            int currentValue = currentCost.get(current);
            for (Node node : neighbours) {
                if (visited.contains(node)) {
                    continue;
                }
                if (!currentCost.containsKey(node)) {
                    currentCost.put(node, node.getCost() + currentValue);
                    node.setParent(current);
                    queue.add(node);
                } else {
                    int newValue = node.getCost() + currentValue;
                    if (newValue < currentCost.get(node)) {
                        currentCost.put(node, newValue);
                        queue.add(node);
                        node.setParent(current);
                    }
                }
            }
        }
        List<Node> list = new ArrayList<>();
        if (current != goal) {
            return null;
        }
        Node temp = goal;
        while (temp != start) {
            if (!temp.getId().equals("goal")) {

            }
            list.add(temp);
            temp = temp.getPar();
        }
        list.add(temp);
        Collections.reverse(list);

        return new Pair<>(list, seen);
    }


    public static Pair<List<Node>, List<Node>> breadthFirstSearch(Node[][] nodes, Node start, Node goal) {

        Queue<Node> queue = new LinkedList<>();
        Set<Node> seen = new HashSet<>();
        List<Node> visited = new ArrayList<>();
        queue.add(start);
        Node current = null;
        while (!queue.isEmpty()) {
            current = queue.poll();
            if (current == goal) {
                break;
            }
            if (seen.contains(current) || current.getId().equals("obstacle")) {
                continue;
            }

            seen.add(current);
            if (current != start)
                visited.add(current);

            List<Node> neighbours = new ArrayList<>();
            addNeighbours(current, neighbours, nodes,false);

            for (Node node : neighbours) {
                if (seen.contains(node)) {
                    continue;
                }
                queue.add(node);
                node.setParent(current);
            }
        }

        if (current != goal) {
            return null;
        }

        List<Node> list = new ArrayList<>();
        Node temp = goal;

        while (temp != start) {
            list.add(temp);
            temp = temp.getPar();
        }

        list.add(temp);
        Collections.reverse(list);

        return new Pair<>(list, visited);
    }

    public static Pair<List<Node>, List<Node>> depthFirstSearch(Node[][] nodes, Node start, Node goal) {

        Stack<Node> stack = new Stack<>();
        Set<Node> seen = new HashSet<>();
        List<Node> visited = new ArrayList<>();
        Set<Node> checked = new HashSet<>();
        stack.push(start);
        Node current = null;
        while (!stack.isEmpty()) {
            current = stack.pop();
            if (current == goal) {
                break;
            }
            if (seen.contains(current) || current.getId().equals("obstacle")) {
                continue;
            }

            seen.add(current);
            if (current != start)
                visited.add(current);

            List<Node> neighbours = new ArrayList<>();
            addNeighbours(current, neighbours, nodes,false);

            for (Node node : neighbours) {
                if (seen.contains(node) || checked.contains(node)) {
                    continue;
                }
                if (node == goal) {
                    node.setParent(current);
                    current = node;
                    break;
                }

                checked.add(node);
                stack.push(node);
                node.setParent(current);
            }
            if (current == goal) {
                break;
            }

        }

        if (current != goal) {
            return null;
        }

        List<Node> list = new ArrayList<>();
        Node temp = goal;

        while (temp != start) {
            list.add(temp);
            temp = temp.getPar();
        }

        list.add(temp);
        Collections.reverse(list);

        return new Pair<>(list, visited);
    }



    public static Pair<List<Node>, List<Node>> aStar(Node[][] nodes, Node start, Node goal) {
        start.gCost = 0;
        getDistanceToGoal(start, goal);

        List<Node> seen = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        Set<Node> checked = new HashSet<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(Node::getfCost).thenComparing(Node::getgCost));
        queue.add(start);
        Node current = null;
        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current == goal) {
                System.out.println("found");
                break;
            }
            if (visited.contains(current) || current.getId().equals("obstacle")) {
                continue;
            }
            visited.add(current);
            if (current != start) {
                seen.add(current);
            }

            List<Node> neighbours = new ArrayList<>();
            addNeighbours(current, neighbours, nodes,true);

            for (Node node : neighbours) {
                if (visited.contains(node)) {
                    continue;
                }
                if (!checked.contains(node)) {
                    node.setParent(current);
                    if (node.isCornerNode) {
                        node.gCost = current.gCost + 14;
                    } else {
                        node.gCost = current.gCost + 10;
                    }
                    getDistanceToGoal(node, goal);
                    queue.add(node);
                    checked.add(node);
                    continue;
                }

                int newDistanceToStart = 0;
                if (node.isCornerNode) {
                    newDistanceToStart = current.gCost + 14;
                } else {
                    newDistanceToStart = current.gCost + 10;
                }

                if (node.gCost > newDistanceToStart) {
                    node.setParent(current);
                    node.gCost = newDistanceToStart;
                    getDistanceToGoal(node, goal);
                }
            }
        }
        List<Node> list = new ArrayList<>();
        if (current != goal) {
            return null;
        }
        Node temp = goal;
        while (temp != start) {
            list.add(temp);
            temp = temp.getPar();
        }
        list.add(temp);
        Collections.reverse(list);
        return new Pair<>(list, seen);
    }

    /**
     * calculates a distance which a given node has to goal node,
     * also assigns a fCost of the node
     * @param node
     * @param goal
     */
    public static void getDistanceToGoal(Node node, Node goal) {
        int colDistance = Math.abs(node.col - goal.col);
        int rowDistance = Math.abs(node.row - goal.row);
        node.hCost = Math.abs(colDistance - rowDistance) * 10 + Math.min(colDistance, rowDistance) * 14;
        node.fCost = node.hCost + node.gCost;

    }


    /**
     * finds all neighbours of the given node and put them into the list
     * also sets if a specific node is a corner or side neighbour
     * @param node main node
     * @param list list of neighbours
     * @param nodes grid of nodes
     */
    public static void addNeighbours(Node node, List<Node> list, Node[][] nodes,boolean aStar) {
        int col = node.col;
        int row = node.row;
        if (row > 0) {
            list.add(nodes[col][row - 1]);
            nodes[col][row - 1].isCornerNode = false;
        }
        if (row < nodes[0].length - 1) {
            list.add(nodes[col][row + 1]);
            nodes[col][row + 1].isCornerNode = false;
        }
        if (col > 0) {
            list.add(nodes[col - 1][row]);
            nodes[col - 1][row].isCornerNode = false;
        }
        if (col < nodes.length - 1) {
            list.add(nodes[col + 1][row]);
            nodes[col + 1][row].isCornerNode = false;
        }
        if (aStar) {
            if (row > 0 && col > 0) {
                list.add(nodes[col - 1][row - 1]);
                nodes[col - 1][row - 1].isCornerNode = true;
            }
            if (row < nodes[0].length - 1 && col > 0) {
                list.add(nodes[col - 1][row + 1]);
                nodes[col - 1][row + 1].isCornerNode = true;
            }
            if (row > 0 && col < nodes.length - 1) {
                list.add(nodes[col + 1][row - 1]);
                nodes[col + 1][row - 1].isCornerNode = true;
            }
            if (row < nodes[0].length - 1 && col < nodes.length - 1) {
                list.add(nodes[col + 1][row + 1]);
                nodes[col + 1][row + 1].isCornerNode = true;
            }
        }
    }

    /**
     *finds the best node in the array based on fCost and gCost (this method is used when A* algorithm
     * uses "open" list to store node instead of priority queue
     * @param open
     * @return index of the found node
     */
    public static int findTheBestIndex(List<Node> open) {
        int bestCost = Integer.MAX_VALUE;
        int bestNodeIndex = 0;
        for (int i = 0; i < open.size(); i++) {
            if (open.get(i).getfCost() < bestCost) {
                bestCost = open.get(i).getfCost();
                bestNodeIndex = i;
            } else if (open.get(i).getfCost() == bestCost
                    && open.get(i).getgCost() < open.get(bestNodeIndex).getgCost()) {
                bestNodeIndex = i;
            }
        }
        return bestNodeIndex;
    }

}
