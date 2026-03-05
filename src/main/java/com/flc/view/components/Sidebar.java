package com.flc.view.components;

import com.flc.config.AppConfig;
import com.flc.config.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Left sidebar navigation panel.
 * Displays nav items, highlights the active one, fires callbacks on click.
 */
public class Sidebar extends JPanel {

    public enum NavItem {
        DASHBOARD  ("Dashboard",  "⊞"),
        TIMETABLE  ("Timetable",  "◫"),
        BOOKINGS   ("Bookings",   "✦"),
        MEMBERS    ("Members",    "◉"),
        REVIEWS    ("Reviews",    "★"),
        REPORTS    ("Reports",    "▤");

        public final String label;
        public final String icon;
        NavItem(String label, String icon) { this.label = label; this.icon = icon; }
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private NavItem activeItem = NavItem.DASHBOARD;
    private final List<NavListener> listeners = new ArrayList<>();

    public interface NavListener {
        void onNavSelected(NavItem item);
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public Sidebar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        setBackground(Theme.ACCENT_DARK);
        setOpaque(true);
        rebuild();
    }

    // ── Build ─────────────────────────────────────────────────────────────────
    private void rebuild() {
        removeAll();

        // Top — brand
        add(buildBrand(),   BorderLayout.NORTH);

        // Centre — nav items
        add(buildNav(),     BorderLayout.CENTER);

        // Bottom — version
        add(buildVersion(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel buildBrand() {
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_MD, 0));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL, Theme.SPACE_MD));

        // Circle logo
        JLabel logo = new JLabel() {
            final int sz = 36;
            @Override public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT_MID);
                g2.fillOval(0, 0, sz, sz);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String t = AppConfig.APP_SHORT;
                g2.drawString(t, (sz - fm.stringWidth(t)) / 2,
                        sz / 2 + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
        };

        JLabel name = new JLabel(AppConfig.APP_SHORT_FULL);
        name.setFont(Theme.FONT_BODY_BOLD);
        name.setForeground(Color.WHITE);

        brand.add(logo);
        brand.add(name);
        return brand;
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_SM, 0, 0, 0));

        // Section label
        JLabel section = new JLabel("NAVIGATION");
        section.setFont(Theme.FONT_PILL);
        section.setForeground(new Color(255, 255, 255, 80));
        section.setBorder(BorderFactory.createEmptyBorder(0, Theme.SPACE_LG, Theme.SPACE_SM, 0));
        section.setHorizontalAlignment(SwingConstants.LEFT);
        nav.add(section);

        for (NavItem item : NavItem.values()) {
            nav.add(buildNavRow(item));
        }

        return nav;
    }

    private JPanel buildNavRow(NavItem item) {
        boolean active = item == activeItem;

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Active pill background
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRoundRect(Theme.SPACE_SM, 2,
                            getWidth() - Theme.SPACE_SM * 2, getHeight() - 4,
                            Theme.RADIUS_SM, Theme.RADIUS_SM);
                    // Left accent bar
                    g2.setColor(Theme.ACCENT_MID);
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setBorder(BorderFactory.createEmptyBorder(0, Theme.SPACE_MD, 0, Theme.SPACE_MD));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon
        JLabel icon = new JLabel(item.icon + "  ");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        icon.setForeground(active ? Color.WHITE : new Color(255, 255, 255, 160));
        icon.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        // Label
        JLabel label = new JLabel(item.label);
        label.setFont(active ? Theme.FONT_MENU_BOLD : Theme.FONT_MENU);
        label.setForeground(active ? Color.WHITE : new Color(255, 255, 255, 160));

        row.add(icon);
        row.add(label);

        // Hover effect
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (item != activeItem) {
                    icon.setForeground(Color.WHITE);
                    label.setForeground(Color.WHITE);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (item != activeItem) {
                    icon.setForeground(new Color(255, 255, 255, 160));
                    label.setForeground(new Color(255, 255, 255, 160));
                }
            }
            @Override public void mouseClicked(MouseEvent e) {
                setActiveItem(item);
                listeners.forEach(l -> l.onNavSelected(item));
            }
        });

        return row;
    }

    private JPanel buildVersion() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 20)),
                BorderFactory.createEmptyBorder(Theme.SPACE_SM, Theme.SPACE_MD, Theme.SPACE_SM, 0)
        ));
        JLabel v = new JLabel("v1.0  ·  7COM1025");
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

    public void addNavListener(NavListener listener) {
        listeners.add(listener);
    }
}