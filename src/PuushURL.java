import java.net.MalformedURLException;
import java.net.URL;

public class PuushURL {
    private final static String PUUSH_URL = "http://www.puu.sh/";
    private final String name;
    private final int[] ID;

    public PuushURL(String spec) {
        String[] s = spec.split("/");
        this.name = s[s.length - 1];
        this.ID = decode(name);
    }

    public PuushURL(int[] code) {
        this.name = encode(code);
        this.ID = code;
    }

    public URL getURL() throws MalformedURLException {
        return new URL(PUUSH_URL + name);
    }

    public int[] getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    private static int[] decode(String s) {
        int[] returnArray = new int[s.length()];
        for (int i = 0; i < s.length(); i++)
            returnArray[i] = decode(s.charAt(i));
        return returnArray;
    }

    private static int decode(char c) {
        if (Character.isDigit(c))
            return Integer.parseInt(String.valueOf(c));
        else if (Character.isUpperCase(c))
            return Character.getNumericValue(c) + 26;
        else
            return Character.getNumericValue(c);
    }

    private static String encode(int[] ints) {
        StringBuilder s = new StringBuilder();
        for (int i : ints) {
            s.append(encode(i));
        }
        return s.toString();
    }

    private static char encode(int i) {
        if (i <= 9) {
            return String.valueOf(i).charAt(0);
        } else {
            return CODE.values()[i - 10].name().charAt(0);
        }
    }

    private enum CODE {
        a, b, c, d, e, f, g, h, i, j,
        k, l, m, n, o, p, q, r, s, t,
        u, v, w, x, y, z,
        A, B, C, D, E, F, G, H, I, J,
        K, L, M, N, O, P, Q, R, S, T,
        U, V, W, X, Y, Z
    }

    public String toString() {
        return name;
    }
}
