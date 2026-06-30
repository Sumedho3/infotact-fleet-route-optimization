package com.infotact.fleet.service.routing;

import java.util.Arrays;

/**
 * 🎯 ALGORITHMIC DATA STRUCTURE:
 * Acts as an encapsulated graph representation, holding computed edge costs
 * (distances in kilometers and durations in minutes) between multiple delivery stops.
 */
public class DistanceMatrix {

    private final double[][] distances;
    private final double[][] durations;
    private final int numberOfNodes;

    /**
     * Constructs a new graph matrix cost container.
     *
     * @param distances Square matrix of physical road distances
     * @param durations Square matrix of estimated transit time values
     */
    public DistanceMatrix(double[][] distances, double[][] durations) {
        if (distances == null || durations == null) {
            throw new IllegalArgumentException("Cost matrices cannot be null.");
        }
        if (distances.length != distances[0].length || durations.length != durations[0].length) {
            throw new IllegalArgumentException("Routing matrices must be rectangular square graphs.");
        }
        if (distances.length != durations.length) {
            throw new IllegalArgumentException("Distance and duration matrix dimensions must match perfectly.");
        }

        this.numberOfNodes = distances.length;

        // Deep copy the internal arrays to ensure the data structure remains immutable
        this.distances = deepCopyMatrix(distances);
        this.durations = deepCopyMatrix(durations);
    }

    /**
     * Retrieves the physical distance cost to travel from a source stop to a destination stop.
     */
    public double getDistance(int fromNode, int toNode) {
        validateBounds(fromNode, toNode);
        return distances[fromNode][toNode];
    }

    /**
     * Retrieves the estimated time duration cost to travel from a source stop to a destination stop.
     */
    public double getDuration(int fromNode, int toNode) {
        validateBounds(fromNode, toNode);
        return durations[fromNode][toNode];
    }

    /**
     * Returns the total number of locations/nodes tracked within this routing grid.
     */
    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }

    private void validateBounds(int fromNode, int toNode) {
        if (fromNode < 0 || fromNode >= numberOfNodes ||
                toNode < 0 || toNode >= numberOfNodes) {

            throw new IndexOutOfBoundsException(
                    String.format(
                            "Invalid node path sequence query: from %d to %d. Matrix size is %d.",
                            fromNode,
                            toNode,
                            numberOfNodes
                    )
            );
        }
    }

    private double[][] deepCopyMatrix(double[][] source) {
        double[][] destination = new double[source.length][];

        for (int i = 0; i < source.length; i++) {
            destination[i] = Arrays.copyOf(source[i], source[i].length);
        }

        return destination;
    }
}