import java.util.*;

// Implements BFS Algorithm using an Adjacency List to find optimal path
public class PathFinder {
    private final Countries map = Countries.getInstance();
    private final Set<String> visited;
    private final Dictionary<String, Integer> distance;
    private final Dictionary<String, String> parent;
    private final PriorityQueue<Node> nodes;

    // Initializes structures to calculate and store optimal path
    PathFinder () {
        nodes = new PriorityQueue<>();
        visited = new HashSet<>();
        distance = new Hashtable<>();
        parent = new Hashtable<>();
    }

    // Uses Dijkstra's Algorithm to find the shortest path between start and end
    // Returns an ordered list of all countries visited
    public List<String> dijkstra (String start, String end) {
        nodes.add(new Node(start, 0));
        for (Country temp : map.countries.values()) {
            distance.put(temp.getName(), Integer.MAX_VALUE);
        }
        distance.put(start, 0);

        while (!nodes.isEmpty()) {
            Node curr = nodes.remove();

            visited.add(curr.name);

            // Path found
            if (curr.name.equalsIgnoreCase(end)) {
                break;
            }

            List<Country.Neighbor> neighborList = map.findCountry(curr.name.toLowerCase()).getNeighbors();

            for (Country.Neighbor neighbor : neighborList) {
                if (!visited.contains(neighbor.getName())) {
                    int totalDist = distance.get(curr.name) + neighbor.getDistToCaps();

                    if (totalDist < distance.get(neighbor.getName())) {
                        distance.put(neighbor.getName(), totalDist);
                        parent.put(neighbor.getName(), curr.name);
                        nodes.add(new Node(neighbor.getName(), totalDist));
                    }
                }
            }
        }
        List<String> bestPath = new ArrayList<>();
        String current = end;

        while (current != null) {
            bestPath.add(0, current);
            current = parent.get(current);
        }
        return bestPath;
    }

     // Node object to store information about each visit made and its cost
    private static class Node implements Comparable<Node> {
        private final int distFromSource;
        private final String name;


        // Creates new Node object with source and total distance from source
        Node (String source, int distFromSource) {
            name = source;
            this.distFromSource = distFromSource;
        }

        // Orders Priority Queue by distFromSource
        // Returns -1, 0, 1
        @Override
        public int compareTo (Node m) {
            int compOutcome = Integer.compare(this.distFromSource, m.distFromSource);
            if (compOutcome == 1 || compOutcome == -1) {
                return compOutcome;
            }
            return 0;
        }
    }
}