import java.util.ArrayList;

public class PuushList extends ArrayList<Puush> {

    public boolean add(Puush p)
    {
        if(super.size() > 15)
            super.remove(0);
        super.add(p);
        return false;
    }

}
