package enums;

public enum TailFlagAction {
    NORMAL("Normal"),
    AWAY("Away");

    private final String name;

    TailFlagAction(String name) {
        this.name = name;
    }

    public static TailFlagAction fromByte(byte value) {
        return TailFlagAction.values()[value];
    }

    public static byte[] toBytes(TailFlagAction action) {
        return new byte[]{(byte) action.ordinal()};
    }

    @Override
    public String toString() {
        return name;
    }
}
