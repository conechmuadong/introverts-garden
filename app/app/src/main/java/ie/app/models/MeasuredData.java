package ie.app.models;

public class MeasuredData {
    public float air_humidity = 0;
    public float radiation = 0;
    public float soil_humidity = 0;
    public float temperature = 0;

    @Override
    public String toString() {
        return "Measured data: " + radiation + "\n";
    }
}
