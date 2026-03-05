package com.flc.view;

import com.flc.config.Theme;
import com.flc.util.ModernTable;
import com.flc.controller.ReviewController;
import com.flc.data.DataStore;
import com.flc.data.persistence.JsonStore;
import com.flc.model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Reviews screen — members write reviews for lessons they attended.
 * Layout: left = write a review | right = all reviews table
 */
public class ReviewScreen extends JPanel {

    private final ReviewController reviewController = new ReviewController();
    private final DataStore        store            = DataStore.getInstance();

    // ── UI refs ───────────────────────────────────────────────────────────────
    private JComboBox<Member>  memberCombo;
    private JComboBox<Lesson>  lessonCombo;
    private int                selectedRating = 5;
    private JButton[]          starBtns;
    private JTextArea          commentArea;
    private JButton            submitBtn;
    private JLabel             statusLabel;
    private JTable             reviewTable;
    private DefaultTableModel  reviewModel;

    public ReviewScreen() {
        setLayout(new BorderLayout(Theme.SPACE_XL, 0));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        add(buildLeftPanel(),  BorderLayout.WEST);
        add(buildRightPanel(), BorderLayout.CENTER);

        refreshReviewTable();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEFT — write a review
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 0));

        // Title
        JLabel title = new JLabel("Write a Review");
        title.setFont(Theme.FONT_TITLE_SM);
        title.setForeground(Theme.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Member selector
        panel.add(sectionLabel("Member"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        memberCombo = new JComboBox<>();
        store.getMembers().forEach(memberCombo::addItem);
        memberCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, val, idx, sel, focus);
                if (val instanceof Member m) setText(m.getMemberId() + "  —  " + m.getName());
                return this;
            }
        });
        memberCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberCombo.addActionListener(e -> refreshLessonCombo());
        panel.add(memberCombo);
        panel.add(Box.createVerticalStrut(Theme.SPACE_LG));

        // Lesson selector
        panel.add(sectionLabel("Lesson Attended"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        lessonCombo = new JComboBox<>();
        lessonCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> l, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, val, idx, sel, focus);
                if (val instanceof Lesson lesson)
                    setText("Wk" + lesson.getWeekNumber()
                            + " " + lesson.getDay().getDisplayName()
                            + " " + lesson.getTimeSlot().getDisplayName()
                            + " — " + lesson.getExerciseType().getName());
                return this;
            }
        });
        lessonCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lessonCombo);
        panel.add(Box.createVerticalStrut(Theme.SPACE_LG));

        // Star rating
        panel.add(sectionLabel("Rating"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        panel.add(buildStarRating());
        panel.add(Box.createVerticalStrut(Theme.SPACE_LG));

        // Comment
        panel.add(sectionLabel("Comment (optional)"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        commentArea = new JTextArea(4, 0);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setBorder(null);
        panel.add(commentScroll);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Submit button
        submitBtn = buildBtn("Submit Review");
        submitBtn.addActionListener(e -> onSubmit());
        panel.add(submitBtn);
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        // Status
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_MID);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalGlue());

        // Init lesson combo
        refreshLessonCombo();
        return panel;
    }

    // ── Star rating widget ─────────────────────────────────────────────────
    private JPanel buildStarRating() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_XS, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        starBtns = new JButton[5];
        for (int i = 0; i < 5; i++) {
            final int rating = i + 1;
            JButton star = new JButton("★") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(rating <= selectedRating
                            ? Theme.STAR_FILLED : Theme.STAR_EMPTY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, Theme.STAR_SIZE_LG));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("★",
                            (getWidth()  - fm.stringWidth("★")) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            star.setPreferredSize(new Dimension(36, 36));
            star.setContentAreaFilled(false);
            star.setBorderPainted(false);
            star.setFocusPainted(false);
            star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            star.addActionListener(e -> {
                selectedRating = rating;
                refreshStars();
            });
            starBtns[i] = star;
            row.add(star);
        }

        JLabel ratingLbl = new JLabel("  Very Satisfied");
        ratingLbl.setFont(Theme.FONT_SMALL);
        ratingLbl.setForeground(Theme.TEXT_MID);

        // Update label on star click
        for (int i = 0; i < 5; i++) {
            final int r = i + 1;
            starBtns[i].addActionListener(e -> {
                ratingLbl.setText("  " + ratingLabel(r));
            });
        }
        row.add(ratingLbl);
        return row;
    }

    private void refreshStars() {
        for (JButton star : starBtns) star.repaint();
    }

    private String ratingLabel(int r) {
        return switch (r) {
            case 1 -> "Very Dissatisfied";
            case 2 -> "Dissatisfied";
            case 3 -> "Ok";
            case 4 -> "Satisfied";
            case 5 -> "Very Satisfied";
            default -> "";
        };
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RIGHT — all reviews table
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);

        JLabel title = new JLabel("All Reviews");
        title.setFont(Theme.FONT_TITLE_SM);
        title.setForeground(Theme.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_MD, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Member", "Exercise", "Week", "Day", "Rating", "Comment"};
        reviewModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        reviewTable = ModernTable.create(reviewModel);
        ModernTable.setColumnWidths(reviewTable, 160, 160, 100, 120, 140, 0);
        ModernTable.setBoldColumn(reviewTable,     0);  // Member name bold
        ModernTable.setExerciseColumn(reviewTable, 1);  // Exercise dot
        ModernTable.setWeekColumn(reviewTable,     2);  // Week chip
        ModernTable.setDayColumn(reviewTable,      3);  // Day dot

        ModernTable.setStarColumn(reviewTable, 4);

        JScrollPane scroll = ModernTable.wrap(reviewTable);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EVENT HANDLERS
    // ═══════════════════════════════════════════════════════════════════════

    private void refreshLessonCombo() {
        lessonCombo.removeAllItems();
        Member m = (Member) memberCombo.getSelectedItem();
        if (m == null) return;

        // Only show lessons the member attended (is enrolled in)
        store.getLessons().stream()
                .filter(l -> l.hasMember(m))
                .forEach(lessonCombo::addItem);
    }

    private void onSubmit() {
        Member member = (Member) memberCombo.getSelectedItem();
        Lesson lesson = (Lesson) lessonCombo.getSelectedItem();

        if (member == null) { setStatus("✗ Please select a member", Theme.TEXT_ERROR); return; }
        if (lesson == null) { setStatus("✗ No lessons available for this member", Theme.TEXT_ERROR); return; }

        try {
            reviewController.addReview(member, lesson, selectedRating, commentArea.getText());
            JsonStore.save();
            setStatus("✓ Review submitted — " + ratingLabel(selectedRating), Theme.TEXT_SUCCESS);
            commentArea.setText("");
            refreshReviewTable();
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    private void refreshReviewTable() {
        reviewModel.setRowCount(0);
        for (Review r : store.getReviews()) {
            reviewModel.addRow(new Object[]{
                r.getMember().getName(),
                r.getLesson().getExerciseType().getName(),
                "Week " + r.getLesson().getWeekNumber(),
                r.getLesson().getDay().getDisplayName(),
                r.getRating(),
                r.getComment()
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STYLING HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.ACCENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }


    private JButton buildBtn(String label) {
        JButton btn = new JButton(label) {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? Theme.BTN_HOVER : Theme.BTN_PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BTN_SM);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}