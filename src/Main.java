import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Garrick
 * Date: 2/28/13
 * Time: 8:25 PM
 */
public class Main extends Container {
    private JPanel panel1;
    private JButton pictureBeHereButton;
    private JButton saveButton;
    private JList list;
    private JButton refreshButton;
    private JPanel pictureContainer;
    private JLabel imageLabel;
    private JPanel imagePanel;
    private static int i = 0;
    private static PuushList pl;

    public static void main(String[] args) throws MalformedURLException {
        Main m = new Main();
        JFrame frame = new JFrame("Puush Catalog");
        frame.setContentPane(m.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        pl = new PuushList(null, 0);
    }

    private void refreshList()
    {
        list.setListData(pl.toArray());
        list.setSelectedIndex(0);
    }

    private void refresh() throws MalformedURLException {
        JTextField url = new JTextField(5);
        SpinnerModel sm = new SpinnerNumberModel(0, 0, 5000, 5);
        JSpinner fetchNum = new JSpinner(sm);
        fetchNum.setPreferredSize(new Dimension(50, 22));

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Puush URL:"));
        myPanel.add(url);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(new JLabel("Amount to fetch:"));
        myPanel.add(fetchNum);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Refresh Catalog", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && (Integer) fetchNum.getValue() > 0 && url.getText().length() > 0) {
            pl = new PuushList(new Puush(url.getText()), (Integer) fetchNum.getValue());
            list.setListData(pl.toArray());
            list.setSelectedIndex(0);
        }
    }

    public Puush getSelectedPuush() {
        return (Puush) list.getSelectedValue();
    }

    private Puush lastSelected = null;

    public Main() {
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refresh clicked.");
                try {
                    refresh();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Save button clicked.");
                    BufferedImage bi = ((Puush) list.getSelectedValue()).getImage();
                    File outputfile = new File(((Puush) list.getSelectedValue()).getName() + ".png");
                    ImageIO.write(bi, "png", outputfile);
                    JOptionPane.showMessageDialog(null, "Image saved to: " + outputfile.getAbsolutePath() + ".", "Clipboard", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                ;
            }
        });
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Puush selected = (Puush) list.getSelectedValue();
                if (selected != null && selected != lastSelected) {
                    System.out.println("Fetching: " + list.getSelectedValue());
                    try {
                        JPanel data = selected.fetchData();
                        if (!data.equals(null)) {
                            imageLabel.setIcon(rescaleImage(((Puush) list.getSelectedValue()).getURL(), pictureContainer.getSize().height, pictureContainer.getSize().width));
                            revalidate();
                        } else {
                            System.out.println("ERROR: Data was null.");
                            return;
                        }
                        saveButton.setText("Save " + getSelectedPuush().getURL().toString());
                    } catch (Exception e1) {
                        System.out.println("ERROR: Could not retrieve data.");
                        pl.remove(getSelectedPuush());
                        refreshList();
                        //e1.printStackTrace();
                    }
                    lastSelected = selected;
                }

            }
        });
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pl != null && list.getSelectedValue() != null) {
                    System.out.println("Copied to clipboard.");
                    StringSelection selection = null;
                    try {
                        JOptionPane.showMessageDialog(null, "Link to image put into clipboard.");
                        selection = new StringSelection(((Puush) list.getSelectedValue()).getURL().toString());
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }
            }
        });
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        panel1.add(splitPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(400, 250), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setRightComponent(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("save this sweet ass picture");
        panel3.add(saveButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), new Dimension(500, -1), 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pictureContainer = new JPanel();
        pictureContainer.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(pictureContainer, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pictureBeHereButton = new JButton();
        pictureBeHereButton.setText("picture be here");
        pictureContainer.add(pictureBeHereButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel4);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(10, -1), new Dimension(100, -1), null, 0, false));
        list = new JList();
        list.setLayoutOrientation(0);
        list.setSelectionMode(0);
        scrollPane1.setViewportView(list);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(-1, 50), 0, true));
        refreshButton = new JButton();
        refreshButton.setText("Refresh");
        panel5.add(refreshButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 15), new Dimension(-1, 30), new Dimension(-1, 50), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
