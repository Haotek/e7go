package tw.haotek.util;

/**
 * Created by Palatis on 2015/12/24.
 */
public class Integer {
    // disallow instantiate... this is a utility class.
    private Integer() {
    }

    public static int tryParseInt(String string, int defaultValue) {
        try {
            return java.lang.Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public static int tryParseInt(String string, int radix, int defaultValue) {
        try {
            return java.lang.Integer.parseInt(string, radix);
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public static int parseInt(String string) throws NumberFormatException {
        return java.lang.Integer.parseInt(string);
    }

    public static int parseInt(String string, int radix) throws NumberFormatException {
        return java.lang.Integer.parseInt(string, radix);
    }

    public static int[] parseIntegerArray(String string) {
        String[] soap = string.split(",");
        int count = soap.length;
        int[] array = new int[count];
        for (int i = 0; i < count; ++i)
            array[i] = parseInt(soap[i]);
        return array;
    }

    public static int bound(int input, int lbound, int ubound) {
        if (input > ubound)
            return ubound;
        if (input < lbound)
            return lbound;
        return input;
    }
}
