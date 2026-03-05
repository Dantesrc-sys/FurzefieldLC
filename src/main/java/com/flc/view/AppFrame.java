package com.flc.view;

import com.flc.config.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Single application window.
 * Uses CardLayout to swap between HomeScreen and DashboardScreen
 * without ever opening a new window.
 */
public class AppFrame extends JFrame {

    private static final String CARD_HOME      = "HOME";
    private static final String CARD_DASHBOARD = "DASHBOARD";

    private final CardLayout cardLayout;
    private final JPanel     cardPanel;

    // Singleton so any screen can call AppFrame.get().showDashboard()
    private static AppFrame instance;
    public  static AppFrame get() { return instance; }

    public AppFrame() {
        instance = this;

        setTitle("Furzefield Leisure Centre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(Theme.WINDOW_MIN);
        setPreferredSize(Theme.WINDOW_SIZE);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);

        cardPanel.add(new HomeScreen(),      CARD_HOME);
        cardPanel.add(new DashboardScreen(), CARD_DASHBOARD);

        setContentPane(cardPanel);
        pack();
        setLocationRelativeTo(null);

        showHome(); // start on home
    }

    public void showHome() {
        cardLayout.show(cardPanel, CARD_HOME);
    }

    public void showDashboard() {
        cardLayout.show(cardPanel, CARD_DASHBOARD);
    }
}