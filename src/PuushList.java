import java.net.MalformedURLException;
import java.util.ArrayList;

public class PuushList extends ArrayList<Puush> {

    public PuushList(Puush starter, int amount) throws MalformedURLException {
        Puush temp = starter;
        for(int i = 0; i < amount; i++) {
            super.add(temp);
            temp = temp.getNext();
        }
    }

    public PuushList() {}

    public boolean add(Puush p)
    {
        super.add(p);
        return false;
    }

    public String toString()
    {
        String retStr = "PuushList: ";
        for(Object pu: super.toArray())
            retStr += pu.toString() + ", ";
        return retStr;
    }

}
