package enums;

public enum PlaneMode {
    CRUISE("Cruise Mode"),
    LANDING("Landing Mode");

    private final String name;

    PlaneMode(String name) {
        this.name = name;
    }

    public static PlaneMode fromByte(byte value) {
        return PlaneMode.values()[value];
    }

    public static byte[] toBytes(PlaneMode planeMode) {
        return new byte[]{(byte) planeMode.ordinal()};
    }

    @Override
    public String toString() {
        return name;
    }
}
