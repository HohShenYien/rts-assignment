package utils;

import java.nio.ByteBuffer;
import java.util.Random;

public class Functions {
    public static String formatColorReset(String string) {
        return string + Colors.RESET;
    }

    public static String formatColorReset(String string, int tabs) {
        return "\t".repeat(tabs) + string + Colors.RESET;
    }

    public static short bytesToShort(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getShort();
    }

    public static int bytesToInt(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        return wrapped.getInt();
    }

    public static String center(String str, int length) {
        int half = (length - str.length()) / 2;
        return " ".repeat(Math.max(0, half)) +
                str +
                " ".repeat(Math.max(0, length - str.length() - half));
    }

    public static byte[] shortToBytes(short number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2);
        byteBuffer.putShort(number);
        return byteBuffer.array();
    }

    public static byte[] intToBytes(int number) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(number);
        return byteBuffer.array();
    }

    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(min, max);
    }

    public static String higherOrLower(int difference) {
        return difference > 0 ? "higher" : "lower";
    }

    public static String raisingOrReducing(int difference) {
        return difference > 0 ? "reducing" : "raising";
    }

    public static byte[] concatenateByteArrays(byte[] first, byte[] second) {
        ByteBuffer bb = ByteBuffer.allocate(first.length + second.length);
        bb.put(first);
        bb.put(second);
        return bb.array();
    }
}
