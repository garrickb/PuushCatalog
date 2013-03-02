import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class Puush extends PuushURL {

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
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        int size = content.getContentLength();
        Type type = getType();
        JLabel picLabel = null;
        System.out.println("TYPE: " + type);
        System.out.println("SIZE: " + size);
        if (getSize() < 150000) {
            switch(type) {
                case IMAGE:
                    ImageIcon icon = new ImageIcon(getImage());
                    picLabel = new JLabel(icon);
                    picLabel.setIcon(rescaleImage(getURL(), dimension.height, dimension.width));
                    break;
                case TEXT:
                    String text = "<html>";
                    BufferedReader in = new BufferedReader(new InputStreamReader(getURL().openStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        text += "<br>"+inputLine;
                    in.close();
                    text+="</html>";
                    picLabel = new JLabel(text);
                    break;
                default:
                    picLabel = new JLabel("Type not supported.");
                    break;
            }
            return picLabel;
        } else {
            System.out.println("FILE TOO LARGE");
            return new JLabel("File is too large: " + size + "kb");
        }
    }

    public ImageIcon rescaleImage(URL source, int maxHeight, int maxWidth) {
        int newHeight = 0, newWidth = 0;        // Variables for the new height and width
        int priorHeight = 0, priorWidth = 0;
        BufferedImage image = null;
        ImageIcon sizeImage;

        try {
            image = ImageIO.read(source);        // get the image
        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Picture upload attempted & failed");
        }

        sizeImage = new ImageIcon(image);

        if (sizeImage != null) {
            priorHeight = sizeImage.getIconHeight();
            priorWidth = sizeImage.getIconWidth();
        }

        // Calculate the correct new height and width
        if ((float) priorHeight / (float) priorWidth > (float) maxHeight / (float) maxWidth) {
            newHeight = maxHeight;
            newWidth = (int) (((float) priorWidth / (float) priorHeight) * (float) newHeight);
        } else {
            newWidth = maxWidth;
            newHeight = (int) (((float) priorHeight / (float) priorWidth) * (float) newWidth);
        }


        // Resize the image

        // 1. Create a new Buffered Image and Graphic2D object
        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();

        // 2. Use the Graphic object to draw a new image to the image in the buffer
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2.dispose();

        // 3. Convert the buffered image into an ImageIcon for return
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
        if(type.contains("image"))
            return Type.IMAGE;
        else if(type.contains("text"))
            return Type.TEXT;
        else
            return Type.NULL;
    }

    public Type getType() throws IOException {
        HttpURLConnection content = (HttpURLConnection) getURL().openConnection();
        content.setRequestMethod("HEAD");
        String type = content.getContentType();
        if(type.contains("image"))
            return Type.IMAGE;
        else if(type.contains("text"))
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
