import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
@SpringBootApplication
public class WeatherForecastApplication {
    // Map to store cached forecasts
    private static final Map<String, CacheEntry> cache = new HashMap<>();
    public static void main(String[] args) {
        SpringApplication.run(WeatherForecastApplication.class, args);
    }
    @RestController
    public static class WeatherController {
        @GetMapping("/weather")
        public Map<String, Object> getWeather(@RequestParam String zipcode) {
            Map<String, Object> response = new HashMap<>();
            if (zipcode == null || zipcode.isEmpty()) {
                response.put("error", "Zipcode parameter is missing.");
                return response;
            }
            CacheEntry cachedForecast = cache.get(zipcode);
            if (cachedForecast != null && !cachedForecast.isExpired()) {
                response.put("forecast", cachedForecast.getData());
                response.put("fromCache", true);
                return response;
            }
            // Fetch weather data (replace with actual API call)
            WeatherData weatherData = fetchWeatherData(zipcode);
            // Construct forecast
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("currentTemp", weatherData.getCurrentTemp());
            // Additional points: high and low temperature
            forecast.put("highTemp", weatherData.getHighTemp());
            forecast.put("lowTemp", weatherData.getLowTemp());
            // Cache the forecast
            cache.put(zipcode, new CacheEntry(forecast));
            response.put("forecast", forecast);
            response.put("fromCache", false);
            return response;
        }
        // Simulated method to fetch weather data from an external API
        private WeatherData fetchWeatherData(String zipcode) {
            // In a real application, make an API call to get weather data
            // This is just a placeholder
            double currentTemp = Math.random() * 30 + 10; // Random temperature between 10 and 40 Celsius
            double highTemp = Math.random() * 10 + 30; // Random high temperature between 30 and 40 Celsius
            double lowTemp = Math.random() * 10 + 5; // Random low temperature between 5 and 15 Celsius
            return new WeatherData(currentTemp, highTemp, lowTemp);
        }
    }
    private static class CacheEntry {
        private final Map<String, Object> data;
        private final LocalDateTime expiry;
        public CacheEntry(Map<String, Object> data) {
            this.data = data;
            this.expiry = LocalDateTime.now().plus(30, ChronoUnit.MINUTES);
        }
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
        public Map<String, Object> getData() {
            return data;
        }
    }
    private static class WeatherData {
        private final double currentTemp;
        private final double highTemp;
        private final double lowTemp;
        public WeatherData(double currentTemp, double highTemp, double lowTemp) {
            this.currentTemp = currentTemp;
            this.highTemp = highTemp;
            this.lowTemp = lowTemp;
        }
        public double getCurrentTemp() {
            return currentTemp;
        }
        public double getHighTemp() {
            return highTemp;
        }
        public double getLowTemp() {
            return lowTemp;
        }
    }
}
