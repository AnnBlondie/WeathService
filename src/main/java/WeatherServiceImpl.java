import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.web.client.RestTemplate;

public class WeatherServiceImpl implements WeatherService {
	
	private String url;

	WeatherServiceImpl(String URL){
		this.url = URL;
	}
	
	public JsonNode getWeatherForCountry(String country,
			Set<String> cities) {
		double commonTemperature = 0;
		int dipsosableCities = 0;
		String jsonResult = "{";
		for (String cityName : cities) {
			double currentTemperature = getCityTemperature(cityName, country);
			commonTemperature += currentTemperature;
			dipsosableCities++;
			jsonResult += "\"" + cityName + "\": " + currentTemperature + ", ";
		}

		jsonResult += "\"average temperature for disposable cities\": "
				+ new BigDecimal(commonTemperature / dipsosableCities)
						.setScale(2, RoundingMode.HALF_UP);
		jsonResult += "}";

		try {
			return new ObjectMapper().readTree(jsonResult);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public double getCityTemperature(String cityName, String country) {
		RestTemplate restTemplate = new RestTemplate();

		String json = null;
		JsonNode rootNode = null;
		JsonNode resultNode = null;
		try {
			json = restTemplate.getForObject(url, String.class, cityName, country);
			rootNode = readJson(json);
			try {
				resultNode = rootNode.get("main").get("temp");
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resultNode != null) {
			return Double.parseDouble("" + resultNode);
		} else {
			return Double.MIN_VALUE;
		}
	}

	public JsonNode readJson(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readValue(json, JsonNode.class);
		JsonParser jp = mapper.getJsonFactory().createJsonParser(json);
		rootNode = mapper.readTree(jp);
		return rootNode;
	}
}