import java.util.ArrayList;
import java.util.List;


// Keeps track of all neighboring countries and
// the distance between capitals.
public class Country {
    private final List<Neighbor> neighbors;

   // Private Properties
    private String name;
    private String code;
    private int ID;

    // Utility Functions
    public Country () {
        neighbors = new ArrayList<>();
    }

    public String getCode () {
        return code;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public List<Neighbor> getNeighbors () {
        return neighbors;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setID (int ID) {
        this.ID = ID;
    }

    // Adds a new neighbor to the country, without duplicating entries

    public void addNeighbor (String countryName, int distBetweenCaps) {
        // If neighbors not empty, check for duplicate values
        if (!neighbors.isEmpty()) {
            for (Neighbor entry : neighbors) {
                assert entry.name != null;
                if (entry.name.equalsIgnoreCase(countryName)) {
                    return;
                }
            }
        }

        Neighbor newAddition = new Neighbor(countryName, distBetweenCaps);

        // Check for proper instantiation before adding to list
        if (newAddition.distToCaps != -1) {
            neighbors.add(newAddition);
        }
    }

    // Searches neighbor list and returns the distance in km to reach the next country
    // Returns distance of route to countryName
    public int getNeighborDist (String countryName) {
        int dist = Integer.MAX_VALUE;
        for (Neighbor elem : neighbors) {
            assert elem.name != null;
            if (elem.name.equalsIgnoreCase(countryName)) {
                dist = elem.distToCaps;
            }
        }
        return dist;
    }


    // Stores information about distance to a neighboring country.

    protected static class Neighbor {

    // Private Properties
        private final String name;
        private final int distToCaps;

        // Creates a new Neighbor object if the country has already been recorded
        public Neighbor (String countryName, int distToCaP) {
            // Neighbor must have a Country object to establish the link
            if (Countries.getInstance().findCountry(countryName) != null) {
                name = countryName;
                distToCaps = distToCaP;
            }
            else {
                // Set all to error values
                name = null;
                distToCaps = -1;

                System.out.println("Country not found on the map. Please add country before assigning neighbors.");
            }
        }


        // Util Access Functions

        public String getName () {
            return name;
        }

        public int getDistToCaps () {
            return distToCaps;
        }
    }
}