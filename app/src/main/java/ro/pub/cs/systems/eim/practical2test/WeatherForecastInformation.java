package ro.pub.cs.systems.eim.practical2test;

public class WeatherForecastInformation {

    private final String temperature;
    private String windSpeed;
    private final String condition;
    private final String pressure;
    private final String humidity;

    public WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return "WeatherForecastInformation{" + "temperature='" + temperature + '\'' + ", windSpeed='" + windSpeed + '\'' + ", condition='" + condition + '\'' + ", pressure='" + pressure + '\'' + ", humidity='" + humidity + '\'' + '}';
    }
}
