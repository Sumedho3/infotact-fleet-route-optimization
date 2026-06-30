package com.infotact.fleet.service.routing;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class NearestNeighborTspSolver {

    /**
     * Solves the Travelling Salesman Problem using the Nearest Neighbor heuristic.
     * It sequences the stops to minimize overall drive time.
     *
     * @param costMatrix The encapsulated graph containing distance and duration metrics between nodes.
     * @return An ordered list of Integers representing the optimized sequence of node indexes (e.g., [0, 2, 1, 3]).
     */
    public List<Integer> computeOptimizedSequence(DistanceMatrix costMatrix) {
        int numberOfNodes = costMatrix.getNumberOfNodes();
        List<Integer> optimizedSequence = new ArrayList<>();
        boolean[] visited = new boolean[numberOfNodes];

        // 🎯 RULE 1: Always start the journey at the first node (Index 0 = Warehouse/Depot)
        int currentNode = 0;
        optimizedSequence.add(currentNode);
        visited[currentNode] = true;

        // Loop until every single node has been added to our delivery sequence path
        while (optimizedSequence.size() < numberOfNodes) {
            int nextNode = -1;
            double minimumDuration = Double.MAX_VALUE;

            // Scan all possible destination nodes to find the closest unvisited neighbor
            for (int targetNode = 0; targetNode < numberOfNodes; targetNode++) {
                if (!visited[targetNode]) {
                    double durationToTarget = costMatrix.getDuration(currentNode, targetNode);

                    if (durationToTarget < minimumDuration) {
                        minimumDuration = durationToTarget;
                        nextNode = targetNode;
                    }
                }
            }

            // Move to the nearest neighbor found
            currentNode = nextNode;
            optimizedSequence.add(currentNode);
            visited[currentNode] = true;
        }

        return optimizedSequence;
    }

    /**
     * Helper method to calculate the cumulative physical distance of the completed route sequence.
     */
    public double calculateTotalDistance(List<Integer> sequence, DistanceMatrix costMatrix) {
        double totalDistance = 0.0;
        for (int i = 0; i < sequence.size() - 1; i++) {
            totalDistance += costMatrix.getDistance(sequence.get(i), sequence.get(i + 1));
        }
        return totalDistance;
    }

    /**
     * Helper method to calculate the cumulative transit duration of the completed route sequence.
     */
    public double calculateTotalDuration(List<Integer> sequence, DistanceMatrix costMatrix) {
        double totalDuration = 0.0;
        for (int i = 0; i < sequence.size() - 1; i++) {
            totalDuration += costMatrix.getDuration(sequence.get(i), sequence.get(i + 1));
        }
        return totalDuration;
    }
}