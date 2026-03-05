package com.flc.view;

import com.flc.config.Theme;
import com.flc.data.DataStore;
import com.flc.data.SampleData;
import com.flc.data.persistence.JsonStore;
import com.flc.util.ImageUtil;
import com.flc.view.components.Sidebar;
import com.flc.view.components.TopBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main dashboard screen — no unicode or special characters anywhere.
 * All icons loaded from assets/ via ImageUtil.
 */
public class DashboardScreen extends JPanel {

    private final Sidebar sidebar;
    private final JPanel  contentArea;
    private       TopBar  topBar;

    public DashboardScreen() {
        if (!JsonStore.load()) {
            SampleData.load();
            JsonStore.save();
        }

        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        sidebar     = new Sidebar();
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Theme.BG);

        sidebar.addNavListener(this::navigateTo);

        add(sidebar,     BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        navigateTo(Sidebar.NavItem.DASHBOARD);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════

    private void navigateTo(Sidebar.NavItem item) {
        sidebar.setActiveItem(item);
        contentArea.removeAll();

        topBar = switch (item) {
            case DASHBOARD -> new TopBar("Dashboard", "Home");
            case TIMETABLE -> new TopBar("Timetable", "Timetable");
            case BOOKINGS  -> new TopBar("Bookings",  "Bookings");
            case MEMBERS   -> new TopBar("Members",   "Members");
            case REVIEWS   -> new TopBar("Reviews",   "Reviews");
            case REPORTS   -> new TopBar("Reports",   "Reports");
        };

        JPanel screen = switch (item) {
            case DASHBOARD -> buildDashboardHome();
            case TIMETABLE -> new TimetableScreen();
            case BOOKINGS  -> new BookingScreen();
            case MEMBERS   -> new MemberScreen();
            case REVIEWS   -> new ReviewScreen();
            case REPORTS   -> new ReportScreen();
        };

        contentArea.add(topBar,  BorderLayout.NORTH);
        contentArea.add(screen,  BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DASHBOARD HOME
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildDashboardHome() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(Theme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        // Welcome
        JLabel welcome = new JLabel("Welcome back, here is your overview");
        welcome.setFont(Theme.FONT_SUBTITLE);
        welcome.setForeground(Theme.TEXT_MID);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(welcome);
        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Stat cards row 1
        DataStore store = DataStore.getInstance();
        JPanel row1 = new JPanel(new GridLayout(1, 4, Theme.SPACE_MD, 0));
        row1.setOpaque(false);
        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row1.add(buildStatCard(
            "Members",        
            String.valueOf(store.getTotalMembers()),        
            Theme.ACCENT,               
            "members.png"));
            
        row1.add(buildStatCard(
            "Lessons",         
            String.valueOf(store.getTotalLessons()),         
            Theme.ACCENT_MID,           
            "timetable.png"));
            
        row1.add(buildStatCard(
            "Bookings",        
            String.valueOf(store.getTotalBookings()),        
            Theme.BOOKING_ICON_COLOR,        
            "bookings.png"));
            
        row1.add(buildStatCard(
            "Reviews",         
            String.valueOf(store.getTotalReviews()),        
            Theme.REVIEWS_ICON_COLOR,        
            "reviews.png"));
            
        page.add(row1);
        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Stat cards row 2
        JPanel row2 = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row2.add(buildStatCard(
            "Weekends",        
            "8",                                            
            Theme.REPORTS_ICON_COLOR,        
            "reports.png"));
        row2.add(buildStatCard("Exercise Types",  String.valueOf(store.getTotalExerciseTypes()),   Theme.ACCENT_DARK,          "dashboard.png"));
        page.add(row2);
        page.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Quick actions
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
    private JPanel buildStatCard(String label, String value, Color accent, String iconFile) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 4, Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.fillRect(0, 2, getWidth(), 2);
                g2.setColor(Theme.SHADOW);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_MD, Theme.SPACE_LG));

        // Left: value + label stacked
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel val = new JLabel(value);
        val.setFont(Theme.FONT_STAT_MD);
        val.setForeground(Theme.TEXT_DARK);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_MID);

        text.add(val);
        text.add(Box.createVerticalStrut(Theme.SPACE_XS));
        text.add(lbl);
        card.add(text, BorderLayout.WEST);

        // Right: icon tinted to accent
        ImageIcon icon = ImageUtil.loadTinted("assets/" + iconFile, 24, 24,
                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120));
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, Theme.SPACE_SM));
            iconLabel.setVerticalAlignment(SwingConstants.CENTER);
            card.add(iconLabel, BorderLayout.EAST);
        }

        return card;
    }

    // ── Quick action button ────────────────────────────────────────────────────
    private JButton buildQuickActionBtn(Sidebar.NavItem item) {
        // Load icon tinted to accent
        ImageIcon icon = ImageUtil.loadTinted(item.iconPath, 16, 16, Theme.ACCENT);

        JButton btn = new JButton(item.label) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? Theme.ACCENT_LIGHT : Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.setColor(Theme.BORDER);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        if (icon != null) btn.setIcon(icon);
        btn.setFont(Theme.FONT_BTN_SM);
        btn.setForeground(Theme.ACCENT);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setIconTextGap(Theme.SPACE_XS);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigateTo(item));
        return btn;
    }
}