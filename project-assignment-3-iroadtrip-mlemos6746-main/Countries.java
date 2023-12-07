import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;

// Class uses "Lazy Initialization" to apply the Singleton Pattern
// to share a single instance of the class across the program.
public class Countries {
    private static volatile Countries instance = null;
    public final HashMap<String, Country> countries = new LinkedHashMap<>();
    public final HashMap<String, String> countryCodes = new HashMap<>();

    // Parses countryData, creates a new country object
    // with that data, and creates an entry into the map
    // using the code as the key.
    public void addCountryInfo(String countryData) throws ParseException {
        String [] data = countryData.split("\t");

        Country info;

        int ID = Integer.parseInt(data[0]);
        String code = data[1];
        String name = data[2].split(" \\(")[0].split("/")[0];

        String key = name.toLowerCase();
        //  Key for countryCodes is name, value is the key for countries
        if(!countryCodes.containsKey(key)){
            countryCodes.put(key, code);
        }
        if(!countries.containsKey(countryCodes.get(key))){
            info = new Country();

            info.setID(ID);
            info.setCode(code);
            info.setName(name);

            countries.put(code, info);
        }
        else{
            info = countries.get(key);

            info.setID(ID);
            info.setCode(code);
            info.setName(name);
        }
    }

    // Uses the country code to find country's data in the map
    // Returns data from entry mapped to countryName or null
    public Country findCountry(String countryName) {
        String key = countryCodes.get(countryName.toLowerCase());
        if(countries.containsKey(key)){
            return countries.get(key);
        }
        return null;
    }

    // Returns the instance of the class if it exists, else creates a new one
    // Returns a single instance of the class to an outside user.
    public static Countries getInstance(){
        if(instance == null){
            synchronized (Countries.class){
                if(instance == null){
                    instance = new Countries();
                }
            }
        }
        return instance;
    }
}