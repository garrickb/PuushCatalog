import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;

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
        JLabel picLabel = new JLabel(new ImageIcon( getImage() ));
        panel.add(picLabel);
        return panel;
    }

    public BufferedImage getImage() throws IOException {
        return  ImageIO.read(this.getURL());
    }

}
