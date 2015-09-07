import java.util.ArrayList;
import java.util.Set;

import org.codehaus.jackson.JsonNode;

public interface WeatherService {

	public JsonNode getWeatherForCountry(String country, Set<String> cities);
}
