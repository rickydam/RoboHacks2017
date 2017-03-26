import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.BoundedRangeModel;
import javax.swing.text.JTextComponent;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GUI extends JPanel {
    private static final String LEFT = "Left";
    private static final String RIGHT = "Right";
    private static final String UP = "Up";
    private static final String DOWN = "Down";
    private BufferedImage image;
    private JPanel canvas;
    private JButton leftButton;
    private JButton rightButton;
	ConstellationFinder cFinder;
	
    public GUI() {
    	cFinder = new ConstellationFinder();
        try {
            this.image = ImageIO.read(new URL("https://amazingsky.files.wordpress.com/2013/07/reesor-ranch-night-sky-panorama.jpg"));
        } catch(IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };

        canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        JScrollPane scrollPane = new JScrollPane(canvas);

        InputMap inputmap = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), LEFT);
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), RIGHT);
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), UP);
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DOWN);

        int scrollableIncrement = 10;
        ActionMap actmap = canvas.getActionMap();
        actmap.put(LEFT, new LeftRightAction(LEFT, scrollPane.getHorizontalScrollBar().getModel(), scrollableIncrement));
        actmap.put(RIGHT, new LeftRightAction(RIGHT, scrollPane.getHorizontalScrollBar().getModel(), scrollableIncrement));
        actmap.put(UP, new UpDownAction(UP, scrollPane.getVerticalScrollBar().getModel(), scrollableIncrement));
        actmap.put(DOWN, new UpDownAction(DOWN, scrollPane.getVerticalScrollBar().getModel(), scrollableIncrement));

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    // Action for vertical key binding to perform when bound event occurs
    private class UpDownAction extends AbstractAction {
        private BoundedRangeModel vScrollBarModel;
        private int scrollableIncrement;
        public UpDownAction(String name, BoundedRangeModel model, int scrollableIncrement) {
            super(name);
            this.vScrollBarModel = model;
            this.scrollableIncrement = scrollableIncrement;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            String name = getValue(AbstractAction.NAME).toString();
            int value = vScrollBarModel.getValue();
            if (name.equals(UP)) {
                cFinder.writeToFile("0 1", "data.txt");
                value -= scrollableIncrement;
                vScrollBarModel.setValue(value);
            } else if (name.equals(DOWN)) {
                cFinder.writeToFile("0 -1", "data.txt");
                value += scrollableIncrement;
                vScrollBarModel.setValue(value);
            }
        }
    }
    
    // Action for horizontal key binding to perform when bound event occurs
    private class LeftRightAction extends AbstractAction {
        private BoundedRangeModel vScrollBarModel;
        private int scrollableIncrement;
        public LeftRightAction(String name, BoundedRangeModel model, int scrollableIncrement) {
            super(name);
            this.vScrollBarModel = model;
            this.scrollableIncrement = scrollableIncrement;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            String name = getValue(AbstractAction.NAME).toString();
            int value = vScrollBarModel.getValue();
            if (name.equals(LEFT)) {
                cFinder.writeToFile("-1 0", "data.txt");
                value -= scrollableIncrement;
                vScrollBarModel.setValue(value);
            } else if (name.equals(RIGHT)) {
                cFinder.writeToFile("1 0", "data.txt");
                value += scrollableIncrement;
                vScrollBarModel.setValue(value);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JPanel panel = new GUI();
                JFrame frame = new JFrame();
                frame.setContentPane(panel);
                frame.setSize(1500, 895);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
