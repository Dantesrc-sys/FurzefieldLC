package com.flc.view.components;

import com.flc.config.AppConfig;
import com.flc.config.Theme;
import com.flc.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Left sidebar navigation panel.
 * Uses PNG images from assets/ — no unicode or special characters.
 */
public class Sidebar extends JPanel {

    public enum NavItem {
        DASHBOARD ("Dashboard", "assets/dashboard.png"),
        TIMETABLE ("Timetable", "assets/timetable.png"),
        BOOKINGS  ("Bookings",  "assets/bookings.png"),
        MEMBERS   ("Members",   "assets/members.png"),
        REVIEWS   ("Reviews",   "assets/reviews.png"),
        REPORTS   ("Reports",   "assets/reports.png");

        public final String label;
        public final String iconPath;
        NavItem(String label, String iconPath) {
            this.label    = label;
            this.iconPath = iconPath;
        }
    }

    private NavItem activeItem = NavItem.DASHBOARD;
    private final List<NavListener> listeners = new ArrayList<>();

    public interface NavListener {
        void onNavSelected(NavItem item);
    }

    public Sidebar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        setBackground(Theme.ACCENT_DARK);
        setOpaque(true);
        rebuild();
    }

    private void rebuild() {
        removeAll();
        add(buildBrand(),   BorderLayout.NORTH);
        add(buildNav(),     BorderLayout.CENTER);
        add(buildVersion(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    // ── Brand ─────────────────────────────────────────────────────────────────
    private JPanel buildBrand() {
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL, Theme.SPACE_MD));

        ImageIcon logoIcon = ImageUtil.load("assets/logo.png", 36, 36);
        JLabel logo;
        if (logoIcon != null) {
            logo = new JLabel(logoIcon);
        } else {
            logo = new JLabel() {
                @Override public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.ACCENT_MID);
                    g2.fillOval(0, 0, 36, 36);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = AppConfig.APP_SHORT;
                    g2.drawString(t, (36 - fm.stringWidth(t)) / 2, 36 / 2 + fm.getAscent() / 2 - 2);
                    g2.dispose();
                }
                @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
            };
        }
        logo.setPreferredSize(new Dimension(36, 36));

        JLabel name = new JLabel(AppConfig.APP_SHORT_FULL);
        name.setFont(Theme.FONT_BODY_BOLD);
        name.setForeground(Color.WHITE);

        brand.add(logo);
        brand.add(name);
        return brand;
    }

    // ── Nav items ─────────────────────────────────────────────────────────────
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_SM, 0, 0, 0));

        for (NavItem item : NavItem.values()) {
            nav.add(buildNavRow(item));
        }
        return nav;
    }

    private JPanel buildNavRow(NavItem item) {
        boolean active = item == activeItem;

        Color dimColour  = new Color(255, 255, 255, 160);
        Color fullWhite  = Color.WHITE;
        Color iconColour = active ? fullWhite : dimColour;

        // Icon label
        ImageIcon icon = ImageUtil.loadTinted(item.iconPath, 18, 18, iconColour);
        JLabel iconLabel = icon != null ? new JLabel(icon) : new JLabel();
        iconLabel.setPreferredSize(new Dimension(26, 18));

        // Text label
        JLabel textLabel = new JLabel(item.label);
        textLabel.setFont(active ? Theme.FONT_MENU_BOLD : Theme.FONT_MENU);
        textLabel.setForeground(iconColour);

        // Row panel — fixed height, items vertically centred via FlowLayout CENTER_Y
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 13)) {
            @Override protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(Theme.SPACE_SM, 2,
                            getWidth() - Theme.SPACE_SM * 2, getHeight() - 4,
                            Theme.RADIUS_SM, Theme.RADIUS_SM);
                    g2.setColor(Theme.ACCENT_MID);
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 44));
        row.setBorder(BorderFactory.createEmptyBorder(0, Theme.SPACE_MD, 0, Theme.SPACE_MD));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.add(iconLabel);
        row.add(textLabel);

        // Hover + click
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (item != activeItem) {
                    ImageIcon hov = ImageUtil.loadTinted(item.iconPath, 18, 18, fullWhite);
                    if (hov != null) iconLabel.setIcon(hov);
                    textLabel.setForeground(fullWhite);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (item != activeItem) {
                    ImageIcon dim = ImageUtil.loadTinted(item.iconPath, 18, 18, dimColour);
                    if (dim != null) iconLabel.setIcon(dim);
                    textLabel.setForeground(dimColour);
                }
            }
            @Override public void mouseClicked(MouseEvent e) {
                setActiveItem(item);
                listeners.forEach(l -> l.onNavSelected(item));
            }
        });

        return row;
    }

    // ── Version ───────────────────────────────────────────────────────────────
    private JPanel buildVersion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 20)),
                BorderFactory.createEmptyBorder(Theme.SPACE_SM, Theme.SPACE_MD, Theme.SPACE_SM, 0)
        ));
        JLabel v = new JLabel(AppConfig.APP_FOOTER_L);
        v.setFont(Theme.FONT_TINY);
        v.setForeground(new Color(255, 255, 255, 60));
        p.add(v);
        return p;
    }

    // ── API ───────────────────────────────────────────────────────────────────
    public void setActiveItem(NavItem item) {
        this.activeItem = item;
        rebuild();
    }

    public NavItem getActiveItem() { return activeItem; }

    public void addNavListener(NavListener listener) { listeners.add(listener); }
}