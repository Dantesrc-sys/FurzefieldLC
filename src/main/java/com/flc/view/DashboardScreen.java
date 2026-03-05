package com.flc.view;

import com.flc.config.Theme;
import com.flc.data.DataStore;
import com.flc.data.SampleData;
import com.flc.data.persistence.JsonStore;
import com.flc.view.components.Sidebar;
import com.flc.view.components.TopBar;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard screen shown after the home screen.
 * Layout: Sidebar (left) + Content area (centre).
 * Content area shows a top bar + the current screen panel.
 */
public class DashboardScreen extends JPanel {

    private final Sidebar   sidebar;
    private final JPanel    contentArea;
    private       TopBar    topBar;

    public DashboardScreen() {
        // Load from JSON if exists, otherwise load sample data
        if (!JsonStore.load()) {
            SampleData.load();
            JsonStore.save(); // create initial save file
        }

        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        sidebar     = new Sidebar();
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Theme.BG);

        // Wire sidebar navigation
        sidebar.addNavListener(item -> navigateTo(item));

        // Root layout: sidebar left, content right
        add(sidebar,     BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        // Show dashboard home by default
        navigateTo(Sidebar.NavItem.DASHBOARD);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════

    private void navigateTo(Sidebar.NavItem item) {
        sidebar.setActiveItem(item);
        contentArea.removeAll();

        topBar = switch (item) {
            case DASHBOARD -> new TopBar("Dashboard",  "Home");
            case TIMETABLE -> new TopBar("Timetable",  "Home › Timetable");
            case BOOKINGS  -> new TopBar("Bookings",   "Home › Bookings");
            case MEMBERS   -> new TopBar("Members",    "Home › Members");
            case REVIEWS   -> new TopBar("Reviews",    "Home › Reviews");
            case REPORTS   -> new TopBar("Reports",    "Home › Reports");
        };

        JPanel screenPanel = switch (item) {
            case DASHBOARD -> buildDashboardHome();
            case TIMETABLE -> new TimetableScreen();
            case BOOKINGS  -> new BookingScreen();
            case MEMBERS   -> new MemberScreen();
            case REVIEWS   -> new ReviewScreen();
            case REPORTS   -> new ReportScreen();
            default        -> buildComingSoon(item.label);
        };

        contentArea.add(topBar,       BorderLayout.NORTH);
        contentArea.add(screenPanel,  BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DASHBOARD HOME — stat cards overview
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildDashboardHome() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(Theme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        // Welcome line
        JLabel welcome = new JLabel("Good morning — here's your overview");
        welcome.setFont(Theme.FONT_SUBTITLE);
        welcome.setForeground(Theme.TEXT_MID);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(welcome);
        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Stat cards row
        DataStore store = DataStore.getInstance();
        JPanel cards = new JPanel(new GridLayout(1, 4, Theme.SPACE_MD, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        cards.add(buildStatCard("Members",    String.valueOf(store.getTotalMembers()),       Theme.ACCENT));
        cards.add(buildStatCard("Lessons",    String.valueOf(store.getTotalLessons()),        Theme.ACCENT_MID));
        cards.add(buildStatCard("Bookings",   String.valueOf(store.getTotalBookings()),       new Color(0x6C63FF)));
        cards.add(buildStatCard("Reviews",    String.valueOf(store.getTotalReviews()),        new Color(0xE67E22)));

        page.add(cards);
        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Second row
        JPanel row2 = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row2.add(buildStatCard("Weekends",    "8",                                           new Color(0x27AE60)));
        row2.add(buildStatCard("Exercise Types", String.valueOf(store.getTotalExerciseTypes()), Theme.ACCENT_DARK));
        page.add(row2);

        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Quick nav section
        JLabel quickNav = new JLabel("Quick Actions");
        quickNav.setFont(Theme.FONT_TITLE_SM);
        quickNav.setForeground(Theme.TEXT_DARK);
        quickNav.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(quickNav);
        page.add(Box.createVerticalStrut(Theme.SPACE_MD));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Sidebar.NavItem item : new Sidebar.NavItem[]{
                Sidebar.NavItem.TIMETABLE,
                Sidebar.NavItem.BOOKINGS,
                Sidebar.NavItem.MEMBERS,
                Sidebar.NavItem.REPORTS}) {
            actions.add(buildQuickActionBtn(item));
        }
        page.add(actions);
        page.add(Box.createVerticalGlue());

        return page;
    }

    // ── Stat card ─────────────────────────────────────────────────────────────
    private JPanel buildStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                // Top accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.fillRect(0, 2, getWidth(), 2);
                // Shadow
                g2.setColor(Theme.SHADOW);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_MD, Theme.SPACE_LG));

        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_STAT_MD);
        val.setForeground(Theme.TEXT_DARK);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MID);

        card.add(val);
        card.add(Box.createVerticalStrut(Theme.SPACE_XS));
        card.add(lbl);
        return card;
    }

    // ── Quick action button ────────────────────────────────────────────────────
    private JButton buildQuickActionBtn(Sidebar.NavItem item) {
        JButton btn = new JButton(item.icon + "  " + item.label) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? Theme.ACCENT_LIGHT : Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.setColor(Theme.BORDER);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BTN_SM);
        btn.setForeground(Theme.ACCENT);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigateTo(item));
        return btn;
    }

    // ── Coming soon placeholder ────────────────────────────────────────────────
    private JPanel buildComingSoon(String name) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BG);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel icon = new JLabel("◎", SwingConstants.CENTER);
        icon.setFont(Theme.FONT_STAT_LG);
        icon.setForeground(Theme.ACCENT_LIGHT);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(name + " Screen", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE_MD);
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Coming in Phase 5", SwingConstants.CENTER);
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(Theme.TEXT_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(Theme.SPACE_MD));
        inner.add(title);
        inner.add(Box.createVerticalStrut(Theme.SPACE_XS));
        inner.add(sub);

        p.add(inner);
        return p;
    }
}