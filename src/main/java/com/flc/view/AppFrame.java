/**
 * Furzefield Leisure Centre (FLC) - Group Exercise Booking Management System
 *
 * A self-contained desktop application for managing group exercise bookings
 * across an 8-weekend season. Runs entirely locally - no server, no login,
 * no internet connection required.
 *
 * Module: 7COM1025 - University of Hertfordshire - Season 2025/26
 *
 * @author  Sandesh Karki (Dantesrc-sys)
 * @email   dashysandesh@gmail.com
 * @version 1.0
 * @since   2026
 */

package com.flc.view;

import com.flc.config.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * The single application window for Furzefield Leisure Centre.
 *
 * <p>Uses a {@link java.awt.CardLayout} to swap between {@link HomeScreen} and
 * {@link DashboardScreen} without opening additional windows. The active card is
 * controlled via {@link #showHome()} and {@link #showDashboard()}.</p>
 *
 * <p>A static singleton reference ({@link #get()}) allows any screen to trigger
 * navigation without needing a direct reference to the frame.</p>
 */
public class AppFrame extends JFrame {

    private static final String CARD_HOME = "HOME";
    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final Logger logger = LoggerFactory.getLogger(AppFrame.class);

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Singleton so any screen can call AppFrame.get().showDashboard()
    private static AppFrame instance;

    public static AppFrame get() {
        return instance;
    }

    public AppFrame() {
        instance = this;

        setTitle("Furzefield");
        // load application icon and use it for the frame (taskbar + window title bar)
        Image icon = loadAppIcon();
        if (icon != null) {
            setIconImage(icon);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(Theme.WINDOW_MIN);
        setPreferredSize(Theme.WINDOW_SIZE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new HomeScreen(), CARD_HOME);
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

    /**
     * Attempt to load the logo image from the classpath so it can be used as the window/taskbar icon. Returns null if
     * the resource is missing or could not be read.
     */
    private Image loadAppIcon() {
        // assume the same logo used on HomeScreen
        URL url = getClass().getClassLoader().getResource("assets/logo.png");
        if (url == null) {
            logger.warn("App icon not found in resources");
            return null;
        }
        return new ImageIcon(url).getImage();
    }
}