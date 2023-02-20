package enums;

public enum CabinPressure {
    LOW("Low"),
    NORMAL("Normal");

    private final String name;

    CabinPressure(String value) {
        this.name = value;
    }

    public static CabinPressure fromByte(byte value) {
        return CabinPressure.values()[value];
    }

    public static byte[] toBytes(CabinPressure cabinPressure) {
        return new byte[]{(byte) cabinPressure.ordinal()};
    }

    @Override
    public String toString() {
        return name;
    }
}
