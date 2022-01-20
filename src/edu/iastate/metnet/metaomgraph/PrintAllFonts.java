package edu.iastate.metnet.metaomgraph;

import java.awt.*;

public class PrintAllFonts {
    public static void main(String[] args) {
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (int i = 0; i < fonts.length; i++) {
            System.out.println(fonts[i]);
        }
    }
}
