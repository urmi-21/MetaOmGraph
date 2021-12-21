package edu.iastate.metnet.simpleui;

import javax.swing.*;

/**
 * Factory constructs prefabricated JFrames under a standard width, height, and title prefix.
 */
public class LayoutFactory {

    public          String         m_titlePrefix = "MetaOmGraph";
    final protected AbstractLayout layout;

    public LayoutFactory(AbstractLayout layout) {
        this.layout = layout;
    }

    /**
     * Constructs a basic JFrame window.
     *
     * @param title name of the window (default prefix: MetaOmGraph - )
     * @return Pop-up JFrame window.
     */
    public JFrame make(String title) {
        JFrame frame = this.makeFrame(title);
        frame.setVisible(true);

        return frame;
    }

    /**
     * Grabs a formatted prefix, if applicable. Otherwise, returns '%s'.
     *
     * @return window title prefix
     */
    protected String getFormattedTitle() {
        if (0 < this.m_titlePrefix.length()) {
            return this.m_titlePrefix + " - %s";
        } else {
            return "%s";
        }
    }

    /**
     * Constructs a prefab'd basic JFrame window.
     *
     * @param title  name of the window (default prefix: MetaOmGraph - )
     * @param width  window width
     * @param height widow height
     * @return Invisible prefab JFrame window
     */
    public JFrame makeFrame(String title, int width, int height) {
        JFrame frame = new JFrame(String.format(this.getFormattedTitle(), title));
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> {
            this.layout.fill(frame.getContentPane());
        });

        return frame;
    }

    final public static int DEFAULT_WIDTH  = 705;
    final public static int DEFAULT_HEIGHT = 300;

    /**
     * Constructs a prefab'd basic JFrame window.
     *
     * @param title name of the window (default prefix: MetaOmGraph - )
     * @return Invisible prefab JFrame window
     */
    public JFrame makeFrame(String title) {
        return this.makeFrame(title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
