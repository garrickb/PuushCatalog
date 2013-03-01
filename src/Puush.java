import java.util.ArrayList;

abstract class Puush {
    final String PUUSH_URL = "puu.sh/";
    public final String URL;
    private static int numPuushs = 0;
    private final int[] ID;

    public Puush(String url)
    {
        numPuushs++;
        this.URL = url;
        ID = decode(url);
    }

    public Puush(int[] id)
    {
        numPuushs++;
        this.URL = encode(id);
        ID = id;
    }

    protected int[] getNext()
    {
        ID[ID.length - 1]++;
        for(int i = ID.length - 1; i > 0; i --)
            if(ID[i] == 62 && i != 0) {
                ID[i-1]++;
                ID[i] = 0;
            }
        return ID;
    }

    protected static int[] getNext(int[] ID)
    {
       ID[ID.length - 1]++;
        for(int i = ID.length; i > 0; i --)
            if(ID[i] == 62 && i != 0) {
                ID[i-1]++;
                ID[i] = 0;
            }
        return ID;
    }

    public static int[] decode(String s)
    {
        ArrayList<Integer> array = new ArrayList<Integer>();
        for(Character c : s.toCharArray())
            array.add(decode(c));
        int[] returnArray = new int[array.size()];
        for(int i = 0; i < array.size(); i ++)
            returnArray[i] = array.get(0);
       return returnArray;
    }

    public static int decode(Character c)
    {
        if(Character.isDigit(c))
            return Integer.parseInt(String.valueOf(c));
        else
            return CODE.valueOf(String.valueOf(c)).ordinal() + 10;
    }

    public static String encode(int[] ints)
    {
        ArrayList<Character> array = new ArrayList<Character>();
        for(int i : ints)
            array.add(encode(i));
        String s = "";
        for(Character c : array)
            s+=c;
        return s;
    }

    public static Character encode(int i)
    {
        if(i < 10)
            return String.valueOf(i).charAt(0);
        else
            return CODE.values()[i-10].name().charAt(0);
    }

    private enum CODE {
        a,b,c,d,e,f,g,h,i,j,
        k,l,m,n,o,p,q,r,s,t,
        u,v,w,x,y,z,
        A,B,C,D,E,F,G,H,I,J,
        K,L,M,N,O,P,Q,R,S,T,
        U,V,W,X,Y,Z;
    }

    public int[] getID()
    {
        return ID;
    }

    public String toString()
    {
        return URL;
    }
}
