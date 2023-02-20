package enums;

public enum OxygenMaskMode {
    DISTRIBUTE("distributed"),
    KEEP("kept back");

    private final String name;

    OxygenMaskMode(String name) {
        this.name = name;
    }

    public static OxygenMaskMode fromByte(byte value) {
        return OxygenMaskMode.values()[value];
    }

    public static byte[] toBytes(OxygenMaskMode mode) {
        return new byte[]{(byte) mode.ordinal()};
    }

    @Override
    public String toString() {
        return name;
    }
}
