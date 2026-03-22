package com.flc;

import com.flc.view.AppFrame;
import javax.swing.*;

/**
 * Application entry point for the Furzefield Leisure Centre Booking System.
 *
 * <p>Bootstraps the Swing UI on the Event Dispatch Thread using
 * {@link javax.swing.SwingUtilities#invokeLater(Runnable)}. The system look
 * and feel is applied before the main window is shown so the application
 * matches the host operating system's native appearance.</p>
 *
 * <p>All data is managed in memory by {@link com.flc.data.DataStore} and
 * persisted automatically to {@code flc-data.json} after every mutating
 * operation via {@link com.flc.data.persistence.JsonStore}.</p>
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new AppFrame().setVisible(true);
        });
    }
}