package enums;

public enum Weather {
    NORMAL("Normal"),
    STORM("Storm"),
    TURBULENCE("Turbulence");

    private final String name;

    Weather(String name) {
        this.name = name;
    }

    public static Weather fromByte(byte value) {
        return Weather.values()[value];
    }

    public static byte[] toBytes(Weather weather) {
        return new byte[]{(byte) weather.ordinal()};
    }

    @Override
    public String toString() {
        return name;
    }
}
