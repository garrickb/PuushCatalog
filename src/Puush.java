import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

class Puush extends PuushURL {

    public static LinkedHashMap<String, Object> data = new LinkedHashMap<>();

    public Puush(String url) throws MalformedURLException {
        super(url);
    }

    public Puush(int[] url) throws MalformedURLException {
        super(url);
    }

    public Puush getNext() throws MalformedURLException {
        int[] ID = getID();
        ID[ID.length - 1]++;
        for (int i = ID.length - 1; i >= 0; i--)
            if (ID[i] >= 62) {
                if (i == 0) {
                    int[] newID = new int[ID.length + 1];
                    for (int x = 0; x < ID.length; x++)
                        newID[x] = ID[x];
                    newID[ID.length] = 0;
                    ID = newID;
                } else
                    ID[i - 1]++;
                ID[i] = ID[i] - 62;

            }
        return new Puush(ID);
    }

    public JLabel fetchData(Dimension dimension) throws IOException {
        if (data.containsKey(getName())) {   //Check the hashmap for an instance of the data
            System.out.println("Data already exists!");
            JLabel label;
            switch (getType()) {
                case IMAGE:
                    label = new JLabel();
                    label.setIcon(rescaleImage((BufferedImage) data.get(getName()), dimension.height, dimension.width));
                    return label;
                case TEXT:
                    label = new JLabel();
                    label.setText((String) data.get(getName()));
                    return label;
            }
        }

        if (data.size() >= 10) { //Only keeps data for last 10 accessed items
            Object key = data.keySet().iterator().next();
            data.remove(key);
        }

        HttpURLConnection content = (HttpURLConnection) getURL().openConnection(); //No data found, get the data from webpage
        long start;
        content.setRequestMethod("HEAD");
        JLabel picLabel = null;
        Type type = getType(content);

        start = System.currentTimeMillis();
        switch (type) {
            case IMAGE:
                if(getFileType().toLowerCase().equals("gif")) {
                    picLabel = new JLabel("GIFs are not supported.");
                    break;
                }
                BufferedImage im = getImage();
                picLabel = new JLabel();
                data.put(getName(), im);
                picLabel.setIcon(rescaleImage(im, dimension.height, dimension.width));
                //picLabel.setIcon(new ImageIcon(im));
                break;
            case TEXT:
                String text = "<html>";
                BufferedReader in = new BufferedReader(new InputStreamReader(getURL().openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    text += "<br>" + inputLine;
                in.close();
                text += "</html>";
                data.put(getName(), text);
                picLabel = new JLabel(text);
                break;
            default:
                picLabel = new JLabel("Type not supported.");
                break;
        }
        System.out.println("TOOK: " + (System.currentTimeMillis() - start) + " ms");

        return picLabel;
    }

    public static ImageIcon rescaleImage(BufferedImage im, int maxHeight, int maxWidth) {
        int newHeight = 0, newWidth = 0;
        int priorHeight = 0, priorWidth = 0;
        ImageIcon sizeImage;
        sizeImage = new ImageIcon(im);

        if (sizeImage != null) {
            priorHeight = sizeImage.getIconHeight();
            priorWidth = sizeImage.getIconWidth();
        }

        if ((float) priorHeight / (float) priorWidth > (float) maxHeight / (float) maxWidth) {
            newHeight = maxHeight;
            newWidth = (int) (((float) priorWidth / (float) priorHeight) * (float) newHeight);
        } else {
            newWidth = maxWidth;
            newHeight = (int) (((float) priorHeight / (float) priorWidth) * (float) newWidth);
        }

        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(im, 0, 0, newWidth, newHeight, null);
        g2.dispose();

        return (new ImageIcon(resizedImg));
    }

    private String getDate(HttpURLConnection content) throws IOException {
        Date date = new Date(content.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/YYYY h:mm:ss a");
        return sdf.format(date).toUpperCase();
    }

    public String getDate() throws IOException {
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        Date date = new Date(content.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/DD/YYYY h:mm:ss a");
        return sdf.format(date).toUpperCase();
    }

    private int getSize(HttpURLConnection content) throws IOException {
        return content.getContentLength();
    }

    public int getSize() throws IOException {
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        return content.getContentLength();
    }

    public boolean isExisting() {
        try {
            return getURL().openConnection().getContentLength() != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public BufferedImage getImage() throws IOException {
        return ImageIO.read(this.getURL());
    }

    private Type getType(HttpURLConnection content) throws IOException {
        String type = content.getContentType();
        if (type.contains("image"))
            return Type.IMAGE;
        else if (type.contains("text"))
            return Type.TEXT;
        else
            return Type.NULL;
    }

    public Type getType() throws IOException {
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        String type = content.getContentType();
        if (type.contains("image"))
            return Type.IMAGE;
        else if (type.contains("text"))
            return Type.TEXT;
        else
            return Type.NULL;
    }

    public String getFileType() throws IOException {
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        String type = content.getContentType();
        return type.split("/")[1];
    }

    public enum Type {
        NULL,
        IMAGE,
        TEXT
    }

}
