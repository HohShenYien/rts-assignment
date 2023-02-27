package utils;

public record Log(byte[] message, String key, boolean received) {
    public Log logReceived() {
        return new Log(message, key, true);
    }
}
