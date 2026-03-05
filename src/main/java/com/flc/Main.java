package com.flc;

import com.flc.view.AppFrame;
import javax.swing.*;

/**
 * Entry point for Furzefield Leisure Centre Booking System.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new AppFrame().setVisible(true);
        });
    }
}