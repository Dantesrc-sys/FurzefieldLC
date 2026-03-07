package com.flc.view;

import com.flc.config.AppConfig;
import com.flc.config.Theme;
import com.flc.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Home screen — first impression when the app opens. Uses ImageUtil for all image loading and tinting. No special
 * characters, dashes, or emoji.
 */
public class HomeScreen extends JPanel {

    public HomeScreen() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        add(buildRoot(), BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ROOT
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(Theme.BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Soft decorative arc — top right
                g2.setColor(new Color(Theme.ACCENT_MID.getRed(), Theme.ACCENT_MID.getGreen(),
                        Theme.ACCENT_MID.getBlue(), Theme.OPACITY_DECO_CIRCLE));
                int arc = 500;
                g2.fillOval(getWidth() - arc + 120, -arc / 2 + 20, arc, arc);

                // Inner arc — layered depth
                g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 12));
                int arc2 = 320;
                g2.fillOval(getWidth() - arc2 + 60, -arc2 / 3, arc2, arc2);

                // Dot grid — bottom left
                g2.setColor(Theme.DOT_GRID);
                for (int x = 48; x < 300; x += 20)
                    for (int y = getHeight() - 260; y < getHeight() - 40; y += 20)
                        g2.fillOval(x, y, 3, 3);

                // Thin rule below top bar
                int ruleY = Theme.TOPBAR_HEIGHT + Theme.SPACE_XL;
                g2.setColor(Theme.BORDER_LIGHT);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawLine(Theme.SPACE_XXL, ruleY, getWidth() - Theme.SPACE_XXL, ruleY);

                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.setBackground(Theme.BG);
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TOP BAR
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(
                BorderFactory.createEmptyBorder(Theme.SPACE_XL, Theme.SPACE_XXL, Theme.SPACE_XL, Theme.SPACE_XXL));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        brand.setOpaque(false);
        brand.add(buildLogoImage());
        brand.add(buildWordmark());
        bar.add(brand, BorderLayout.WEST);
        bar.add(buildVersionPill(), BorderLayout.EAST);
        return bar;
    }

    private JLabel buildLogoImage() {
        // ImageUtil.iconLabel returns an empty JLabel gracefully if not found
        ImageIcon icon = ImageUtil.load("assets/logo.png", 36, 36);
        if (icon != null) {
            JLabel l = new JLabel(icon);
            l.setPreferredSize(new Dimension(36, 36));
            return l;
        }
        // Fallback — painted green circle
        return new JLabel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillOval(0, 0, 36, 36);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 36);
            }
        };
    }

    private JLabel buildWordmark() {
        JLabel l = new JLabel(AppConfig.APP_SHORT_FULL);
        l.setFont(Theme.FONT_BODY_BOLD);
        l.setForeground(Theme.TEXT_DARK);
        return l;
    }

    private JLabel buildVersionPill() {
        JLabel l = new JLabel("2025 / 26", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_PILL);
        l.setForeground(Theme.ACCENT);
        l.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        l.setOpaque(false);
        return l;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BODY
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.add(buildHero());
        return body;
    }

    private JPanel buildHero() {
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);

        hero.add(centreX(buildEyebrow()));
        hero.add(Box.createVerticalStrut(Theme.SPACE_XL));
        hero.add(centreX(buildHeadline()));
        hero.add(Box.createVerticalStrut(Theme.SPACE_LG));
        hero.add(centreX(buildSubtitle()));
        hero.add(Box.createVerticalStrut(Theme.SPACE_XXXL));
        hero.add(centreX(buildCTA()));
        hero.add(Box.createVerticalStrut(Theme.SPACE_MD));
        hero.add(centreX(buildReassurance()));
        hero.add(Box.createVerticalStrut(Theme.SPACE_XXL));
        hero.add(centreX(buildFeatureRow()));

        return hero;
    }

    private JLabel buildEyebrow() {
        JLabel l = new JLabel(AppConfig.APP_BADGE, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_PILL);
        l.setForeground(Theme.ACCENT);
        l.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        l.setOpaque(false);
        return l;
    }

    private JLabel buildHeadline() {
        String html = "<html><div style='text-align:center; line-height:1.15;'>" + "Furzefield<br>Leisure Centre"
                + "</div></html>";
        JLabel h = new JLabel(html, SwingConstants.CENTER);
        h.setFont(Theme.FONT_HEADLINE);
        h.setForeground(Theme.TEXT_DARK);
        return h;
    }

    private JLabel buildSubtitle() {
        JLabel s = new JLabel(AppConfig.APP_SUBTITLE, SwingConstants.CENTER);
        s.setFont(Theme.FONT_SUBTITLE);
        s.setForeground(Theme.TEXT_MID);
        return s;
    }

    private JButton buildCTA() {
        JButton btn = new JButton("Open Dashboard") {
            private boolean hovered = false;
            private boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }

                    public void mousePressed(MouseEvent e) {
                        pressed = true;
                        repaint();
                    }

                    public void mouseReleased(MouseEvent e) {
                        pressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = pressed ? Theme.BTN_PRESSED : hovered ? Theme.BTN_HOVER : Theme.BTN_PRIMARY;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BTN);
        btn.setForeground(Theme.BTN_TEXT);
        btn.setPreferredSize(new Dimension(220, 54));
        btn.setMaximumSize(new Dimension(220, 54));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> onGetStarted());
        return btn;
    }

    private JLabel buildReassurance() {
        JLabel l = new JLabel("Self-contained    No login required    All data stored locally", SwingConstants.CENTER);
        l.setFont(Theme.FONT_TINY);
        l.setForeground(Theme.TEXT_LIGHT);
        return l;
    }

    // ── Feature chips — icon + label via ImageUtil ─────────────────────────
    private JPanel buildFeatureRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.SPACE_SM, 0));
        row.setOpaque(false);
        row.add(buildChip("bookings.png", "Bookings"));
        row.add(buildChip("members.png", "Members"));
        row.add(buildChip("reviews.png", "Reviews"));
        row.add(buildChip("reports.png", "Reports"));
        return row;
    }

    private JPanel buildChip(String iconFile, String label) {
        JPanel chip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SM, Theme.RADIUS_SM);
                g2.setColor(Theme.BORDER);
                g2.setStroke(Theme.STROKE_THIN);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.RADIUS_SM, Theme.RADIUS_SM);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setLayout(new FlowLayout(FlowLayout.CENTER, Theme.SPACE_XS, 0));
        chip.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        // Load and tint via ImageUtil — no local image methods needed
        JLabel iconLabel = ImageUtil.tintedLabel("assets/" + iconFile, 18, 18, Theme.ACCENT);
        chip.add(iconLabel);

        JLabel text = new JLabel(label);
        text.setFont(Theme.FONT_SMALL);
        text.setForeground(Theme.TEXT_MID);
        chip.add(text);

        return chip;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FOOTER
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(Theme.SPACE_SM, Theme.SPACE_XXL, Theme.SPACE_SM, Theme.SPACE_XXL)));
        footer.add(footerLabel(AppConfig.APP_FOOTER_L), BorderLayout.WEST);
        footer.add(footerLabel(AppConfig.APP_FOOTER_R), BorderLayout.EAST);
        return footer;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JLabel footerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TINY);
        l.setForeground(Theme.TEXT_LIGHT);
        return l;
    }

    private JPanel centreX(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(comp);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════

    private void onGetStarted() {
        AppFrame.get().setExtendedState(AppFrame.get().getExtendedState() | java.awt.Frame.MAXIMIZED_BOTH);

        AppFrame.get().showDashboard();
    }
}