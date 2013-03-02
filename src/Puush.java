import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class Puush extends PuushURL {

    public Puush(String url) throws MalformedURLException
    {
        super(url);
    }

    public Puush(int[] url) throws MalformedURLException
    {
        super(url);
    }

    public Puush getNext() throws MalformedURLException
    {
        int[] ID = getID();
        ID[ID.length - 1]++;
        for(int i = ID.length - 1; i >= 0; i--)
            if(ID[i] >= 62) {
                if(i == 0) {
                    int[] newID = new int[ID.length + 1];
                    for(int x = 0; x < ID.length; x++)
                        newID[x] = ID[x];
                    newID[ID.length] = 0;
                    ID = newID;
                } else
                    ID[i-1]++;
                ID[i] = ID[i] - 62;

            }
        return new Puush(ID);
    }

    public JPanel fetchData() throws IOException {
        JPanel panel = new JPanel();
        ImageIcon icon = new ImageIcon( getImage() );
        JLabel picLabel = new JLabel(icon);
        panel.add(picLabel);
            System.out.println(icon.getDescription());
        return panel;
    }

    public boolean isExisting() throws IOException {
        try{
        URL url = getURL();
        BufferedReader in = new BufferedReader(
        new InputStreamReader(url.openStream()));
        int i = in.read();
        in.close();
        return i > 100; //It is over 100 if there is an image.
        } catch (Exception e) { return false; }
    }

    public BufferedImage getImage() throws IOException {
        return  ImageIO.read(this.getURL());
    }

}
