package com.wootion.robot;

public class MemWeatherStatus {
    private double temp;
    private double hum;
    private double rainfall;
    private double windSpeed;
    private int isRain;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHum() {
        return hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getIsRain() {
        return isRain;
    }

    public void setIsRain(int isRain) {
        this.isRain = isRain;
    }

    @Override
    public String toString() {
        return "MemWeatherStatus{" +
                "temp=" + temp +
                ", hum=" + hum +
                ", rainfall=" + rainfall +
                ", windSpeed=" + windSpeed +
                ", isRain=" + isRain +
                '}';
    }
}
