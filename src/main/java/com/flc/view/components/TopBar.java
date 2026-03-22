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

package com.flc.view.components;

import com.flc.config.Theme;
import com.flc.util.ImageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Top bar displayed above every screen inside the dashboard.
 *
 * <p>Shows a breadcrumb label in small caps above the screen title in a larger
 * bold font, both left-aligned. A tinted icon corresponding to the current
 * screen is displayed on the right.</p>
 *
 * <p>The title and breadcrumb can be updated at runtime via {@link #setTitle(String)}
 * and {@link #setBreadcrumb(String)}.</p>
 */
public class TopBar extends JPanel {

    private final JLabel titleLabel;
    private final JLabel breadcrumbLabel;

    public TopBar(String title, String breadcrumb) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        setPreferredSize(new Dimension(0, Theme.TOPBAR_HEIGHT));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, Theme.SPACE_XL, 0, Theme.SPACE_XL)));

        // Left - breadcrumb above title
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        breadcrumbLabel = new JLabel(breadcrumb.toUpperCase());
        breadcrumbLabel.setFont(Theme.FONT_PILL);
        breadcrumbLabel.setForeground(Theme.TEXT_LIGHT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE_MD);
        titleLabel.setForeground(Theme.TEXT_DARK);

        left.add(Box.createVerticalGlue());
        left.add(breadcrumbLabel);
        left.add(titleLabel);
        left.add(Box.createVerticalGlue());

        add(left, BorderLayout.WEST);

        // Right - screen icon
        String iconPath = "assets/" + (breadcrumb.toLowerCase().equals("home") ? "dashboard" : breadcrumb.toLowerCase())
                + ".png";
        ImageIcon icon = ImageUtil.loadTinted(iconPath, 20, 20, Theme.TEXT_LIGHT);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, Theme.SPACE_SM));
            add(iconLabel, BorderLayout.EAST);
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setBreadcrumb(String breadcrumb) {
        breadcrumbLabel.setText(breadcrumb.toUpperCase());
    }
}