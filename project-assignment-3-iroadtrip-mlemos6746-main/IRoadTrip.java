import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IRoadTrip {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final Countries map = Countries.getInstance();
    // Gets the singular instance of the collection of countries
    private static final Scanner scan = new Scanner(System.in);
    private static final Dictionary<String, String> knownFiles = new Hashtable<>() {{
        put("borders", "borders.txt");
        put("capDist", "capdist.csv");
        put("stateNames", "state_name.tsv");
    }};
    private static BufferedReader reader;

    // Constructs initial program. Checks args to ensure all files are present
    // Reads files and inputs necessary data into 'Countries.countries' and 'Countries.countryCodes'
    public IRoadTrip (String[] args) throws IOException {

        // Uses small map to check if all files are present
        Map<String, Boolean> hasFile = new HashMap<>(3) {{
            put("borders", false);
            put("capDist", false);
            put("stateNames", false);
        }};

        // Parses args to check for all necessary files and updates knownFiles
        for (String arg : args) {
            if (arg.contains(knownFiles.get("borders"))) {
                knownFiles.put("borders", arg);
                hasFile.put("borders", true);
            }
            if (arg.contains(knownFiles.get("capDist"))) {
                knownFiles.put("capDist", arg);
                hasFile.put("capDist", true);
            }
            if (arg.contains(knownFiles.get("stateNames"))) {
                knownFiles.put("stateNames", arg);
                hasFile.put("stateNames", true);
            }
        }


        // End if missing any files
        if (hasFile.containsValue(false)) {
            for (String key : hasFile.keySet()) {
                if (!hasFile.get(key)) {
                    System.out.println("Error! Missing file: " + knownFiles.get(key));
                }
            }
            System.out.println("Ending execution. Provide necessary files to run program.");
            System.exit(1);
        }

        readStateNames(knownFiles.get("stateNames"));
        setBorders(readBorders(knownFiles.get("borders")), readCapDistance(knownFiles.get("capDist")));
    }

    // Main driver loop for the program. Creates initial objects/data structures

    public static void main (String[] args) throws IOException, InterruptedException {

        args = new String[]{"borders.txt", "capdist.csv", "state_name.tsv"};

        IRoadTrip ml = new IRoadTrip(args);

        ml.acceptUserInput();
    }

    // Reads user input to find a path between two countries. Rejects invalid country names.
    @SuppressWarnings("BusyWait")
    public void acceptUserInput () throws IOException, InterruptedException {
        while (true) {
            String start = "", end = "";
            int tries = 0;

            // Ensure both start and end are valid countries
            while (map.findCountry(start) == null) {
                Runtime.getRuntime().exec("clear");
                if (tries++ > 1) {
                    System.out.println(start + " is not a valid country. Please try again.");
                }

                System.out.print("Please enter the name of the first country (type exit to quit): ");
                start = scan.nextLine();
                if (start.matches("exit")) {
                    return;
                }
            }

            tries = 0;

            while (map.findCountry(end) == null) {
                if (tries++ > 1) {
                    System.out.println(end + " is not a valid country. Please try again.");
                }
                System.out.print("Please enter the name of the second country (type exit to quit): ");
                end = scan.nextLine();
                if (start.matches("exit")) {
                    return;
                }
            }

            System.out.println("Great! I am calculating the best path. Please wait...");

            List<String> shortestPath = findPath(start, end);

            System.out.println("Route from " + start + " to " + end + ":");
            for (int i = 1; i < shortestPath.size(); i++) {
                int dist = getDistance(shortestPath.get(i - 1), shortestPath.get(i));

                System.out.println("* " + shortestPath.get(i - 1) + " –> " + shortestPath.get(i) + " (" + dist + " kilometers.)");
            }

            //  Sleep program for 0.5s
            Thread.sleep(500);
        }
    }


    // Retrieves the distance in km from each country's capital city
    // Returns distance from country A to country B

    public int getDistance (String countryA, String countryB) {
        Country orig = map.findCountry(countryA.toLowerCase());
        return orig.getNeighborDist(countryB);
    }

    // Returns the shortest distance between the 2 countries
    // Returns list of 'jumps' (edges) to get from country A to country B
    public List<String> findPath (String countryA, String countryB) {
        PathFinder pf = new PathFinder();

        return pf.dijkstra(countryA, countryB);
    }

    // Reads the stateNames file and adds data to both 'Countries.countryCodes' and 'Countries.countries'

    public void readStateNames (String filename) throws IOException {
        try {
            reader = new BufferedReader(new FileReader(filename));

            // Skip first line
            String line = reader.readLine();
            line = reader.readLine();

            while (line != null) {
                String[] countryInfo = line.split("\t");
                // Ensures exclusion of 'not current' countries
                if (!sdf.parse(countryInfo[4]).before(sdf.parse("2020-12-31"))) {
                    map.addCountryInfo(line);
                }
                line = reader.readLine();
            }

            reader.close();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        System.out.println(filename + " has been read and processed.");
    }


     // Reads borders.txt and parses all data into pairs of borders
     // Then creates and returns a list of pairs.
    public List<Dictionary<String, List<String>>> readBorders (String filename) throws RuntimeException {

        List<Dictionary<String, List<String>>> allBorderPairings = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            while (line != null) {

                Dictionary<String, List<String>> entry = new Hashtable<>();

                String[] borderInfo = line.split(" = ");


                // If no bordering countries, skip
                if (borderInfo.length > 1) {
                    String[] borders = borderInfo[1].split("; ");
                    String origCountry = borderInfo[0];
                    List<String> orig = new ArrayList<>() {{
                        add(origCountry);
                    }};
                    List<String> borderList = new ArrayList<>();

                    for (String country : borders) {
                        String countryName = country.split(" ")[0];

                        borderList.add(countryName);
                    }

                    if (orig.get(0).equalsIgnoreCase("United States")) {
                        System.out.println(" ");
                    }
                    entry.put("origin", orig);
                    entry.put("borders", borderList);

                    allBorderPairings.add(entry);
                }
                line = reader.readLine();
            }
            reader.close();
            return allBorderPairings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Reads the specified capdist file then parses the data into (ida, idb, kmdist)
    // Returns a list of all pairs processed
    public List<Dictionary<String, String>> readCapDistance (String filename) {

        try {
            List<Dictionary<String, String>> capDistInfo = new ArrayList<>();

            reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            line = reader.readLine();

            while (line != null) {
                Dictionary<String, String> pairs = new Hashtable<>();

                String[] info = line.split(",");
                if (info[1].matches("UKG")) {
                    info[1] = "UK";
                }
                pairs.put("origin", info[1]);
                pairs.put("dest", info[3]);
                pairs.put("distance", info[4]);

                capDistInfo.add(pairs);

                line = reader.readLine();
            }

            reader.close();

            // Sort pairs by origin then by dest
            capDistInfo.sort((o1, o2) -> {
                int origCompVal = o1.get("origin").compareTo(o2.get("origin"));
                if (origCompVal == 0) {
                    return o1.get("dest").compareTo(o2.get("dest"));
                }
                else {
                    return origCompVal;
                }
            });
            return capDistInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

     // Goes through borderPairs list and sets all valid connections to bordering countries
    public void setBorders (List<Dictionary<String, List<String>>> borderPairs, List<Dictionary<String, String>> capDistInfo) {
        Countries map = Countries.getInstance();
        Dictionary<String, String> distInfo;

        for (Dictionary<String, List<String>> pair : borderPairs) {
            String origin = pair.get("origin").get(0);
            List<String> borders = pair.get("borders");

            //  Edge cases due to inconsistent naming conventions
            if (origin.contains("United States")) {
                origin = "United States of America";
            }
            if (origin.contains("Cameroon")) {
                origin = "Cameroun";
            }
            Country start = map.findCountry(origin);

            // Skips non-existent countries
            if (start != null) {
                for (String border : borders) {
                    if (border.equalsIgnoreCase("USA")) {
                        border = "United States of America";
                    }
                    Country end = map.findCountry(border);
                    if (end != null) {
                        // Retrieves info from capDistInfo list
                        distInfo = biSearchCapDist(capDistInfo, 0, capDistInfo.size() - 1, start.getCode(), end.getCode());

                        if (distInfo != null) {
                            // Converts String dist to int
                            int distance = Integer.parseInt(distInfo.get("distance"));

                            // Adds link to both countries
                            start.addNeighbor(border, distance);
                            end.addNeighbor(origin, distance);
                        }
                    }
                }
            }
        }
    }

    // Performs binary search on borderDistances list
    private Dictionary<String, String> biSearchCapDist (List<Dictionary<String, String>> capDistInfo,
                                                        int left, int right, String origin, String dest) {
        int mid = ((right - left) / 2) + left;

        Dictionary<String, String> entry = capDistInfo.get(mid);

        String entryOrigin = entry.get("origin"), entryDest = entry.get("dest");

        int origMatch = entryOrigin.compareToIgnoreCase(origin);
        int destMatch = entryDest.compareToIgnoreCase(dest);

        if (right - left < 0) {
            return null;
        }
        // Matching Dictionary found, return entry
        if (origMatch == 0 && destMatch == 0) {
            return entry;
        }

        if (origMatch > 0) {
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        if (origMatch < 0) {
            return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
        }

        // If origin is found, check for matching dest
        // If entryDest is larger than "dest", move left
        if (destMatch > 0) {
            return biSearchCapDist(capDistInfo, left, mid - 1, origin, dest);
        }
        return biSearchCapDist(capDistInfo, mid + 1, right, origin, dest);
    }
}