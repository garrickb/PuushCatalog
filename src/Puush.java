import java.net.MalformedURLException;

class Puush extends PuushURL {

    public Puush(String url) throws MalformedURLException {
        super(url);
    }

    public Puush(int[] url) throws MalformedURLException {
        super(url);
    }

    public Puush getNext() throws MalformedURLException {
        int[] ID = super.getID();
        ID[ID.length - 1]++;
        for(int i = ID.length - 1; i > 0; i--)
            if(ID[i] >= 62 && i != 0) {
                ID[i-1]++;
                ID[i] = 0;
            }
        return new Puush(ID);
    }

}
