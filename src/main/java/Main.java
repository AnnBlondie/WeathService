import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;

public class Main {
	
	static String inputFileName =  "cities.csv";
	static String outputFileName = "cities_with_temperature.csv";

	public static void main(String[] args) {

		Map<String, Set<String>> citiesInCountry = new LinkedHashMap<String, Set<String>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
			String currentLine = reader.readLine();
			while (currentLine != null) {
				String[] currentPair = currentLine.split(",");
				if (currentPair.length > 1) {
					if(citiesInCountry.containsKey(currentPair[1])){
						citiesInCountry.get(currentPair[1]).add(currentPair[0]);
					}
					else{
						Set<String> temp = new HashSet<String>();
						temp.add(currentPair[0]);
						citiesInCountry.put(currentPair[1], temp);
					}
				}
				currentLine = reader.readLine();
			}
		} catch (Exception e) {
		}

		WeatherService wetherService = new WeatherServiceImpl(
				"http://api.openweathermap.org/data/2.5/weather?q={cityName},{country}&units=metric");

		for(String country: citiesInCountry.keySet()){
			JsonNode newJson = wetherService.getWeatherForCountry(country, citiesInCountry.get(country));
			System.out.println(newJson);
		}
		
	}
}
