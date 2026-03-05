package com.flc.view;

import com.flc.config.Theme;
import com.flc.controller.ReportController;
import com.flc.controller.ReportController.AttendanceRow;
import com.flc.controller.ReportController.IncomeRow;
import com.flc.util.ModernTable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Reports screen — two side-by-side reports using ModernTable.
 *  Left  → Attendance and Rating (per lesson)
 *  Right → Income (per exercise, highest first)
 */
public class ReportScreen extends JPanel {

    private final ReportController reportController = new ReportController();

    public ReportScreen() {
        setLayout(new BorderLayout(0, Theme.SPACE_LG));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildReports(), BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HEADER
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_MD, 0));

        JLabel sub = new JLabel("Season summary across all 8 weekends");
        sub.setFont(Theme.FONT_SUBTITLE);
        sub.setForeground(Theme.TEXT_MID);
        panel.add(sub, BorderLayout.WEST);

        IncomeRow top = reportController.getHighestIncomeExercise();
        if (top != null) panel.add(buildHighlightBadge(top), BorderLayout.EAST);
        return panel;
    }

    private JPanel buildHighlightBadge(IncomeRow top) {
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_MD, Theme.SPACE_XL, Theme.SPACE_MD, Theme.SPACE_XL));

        JLabel label = new JLabel("TOP EARNER");
        label.setFont(Theme.FONT_PILL);
        label.setForeground(new Color(255, 255, 255, 180));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel(top.exerciseName());
        name.setFont(Theme.FONT_TITLE_SM);
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel income = new JLabel(top.formattedIncome());
        income.setFont(Theme.FONT_STAT_MD);
        income.setForeground(Color.WHITE);
        income.setAlignmentX(Component.CENTER_ALIGNMENT);

        badge.add(label);
        badge.add(Box.createVerticalStrut(Theme.SPACE_XS));
        badge.add(name);
        badge.add(income);
        return badge;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REPORTS — side by side
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildReports() {
        JPanel panel = new JPanel(new GridLayout(1, 2, Theme.SPACE_XL, 0));
        panel.setOpaque(false);
        panel.add(buildAttendanceReport());
        panel.add(buildIncomeReport());
        return panel;
    }

    // ── Report 1: Attendance & Rating ─────────────────────────────────────
    // cols: Week | Day | Time | Exercise | Enrolled | Avg Rating
    private JPanel buildAttendanceReport() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);
        panel.add(reportTitle("Attendance and Rating", "Per lesson   sorted by week"),
                BorderLayout.NORTH);

        String[] cols = {"Week", "Day", "Time", "Exercise", "Enrolled", "Avg Rating"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<AttendanceRow> rows = reportController.getAttendanceReport();
        for (AttendanceRow row : rows) {
            model.addRow(new Object[]{
                "Wk " + row.weekNumber(),
                row.day().getDisplayName(),
                row.timeSlot().getDisplayName(),
                row.exerciseName(),
                row.enrolledCount() + " / 4",
                row.formattedRating()
            });
        }

        // ── Same pattern as TimetableScreen ───────────────────────────────
        JTable table = ModernTable.create(model);
        ModernTable.setColumnWidths(table, 90, 120, 130, 160, 140, 130);
        ModernTable.setWeekColumn    (table, 0);  // chip
        ModernTable.setDayColumn     (table, 1);  // coloured dot
        ModernTable.setExerciseColumn(table, 3);  // coloured dot
        ModernTable.setCapacityColumn(table, 4);  // mini bar

        // Avg Rating — green / amber / red based on value
        java.util.Map<String, Color> ratingBg = new java.util.HashMap<>();
        java.util.Map<String, Color> ratingFg = new java.util.HashMap<>();
        ratingBg.put("No reviews", Theme.BG_ALT);
        ratingFg.put("No reviews", Theme.TEXT_LIGHT);
        ModernTable.setPillColumn(table, 5, ratingBg, ratingFg);
        // Override pill for numeric ratings with dynamic colour
        table.getColumnModel().getColumn(5).setCellRenderer(
                new ModernTable.DefaultRenderer() {
                    @Override public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        String s = val == null ? "" : val.toString();
                        if (s.equals("No reviews")) {
                            setForeground(Theme.TEXT_LIGHT);
                            setFont(Theme.FONT_BODY);
                        } else {
                            try {
                                double avg = Double.parseDouble(s.split(" ")[0]);
                                setForeground(avg >= 4.0 ? Theme.TEXT_SUCCESS
                                            : avg >= 3.0 ? Theme.TEXT_WARNING
                                            : Theme.TEXT_ERROR);
                                setFont(Theme.FONT_BODY_BOLD);
                            } catch (Exception ignored) {
                                setForeground(Theme.TEXT_MID);
                            }
                        }
                        return this;
                    }
                });

        long totalEnrolled = rows.stream().mapToLong(AttendanceRow::enrolledCount).sum();
        panel.add(ModernTable.wrap(table), BorderLayout.CENTER);
        panel.add(footerLabel("Total enrolled: " + totalEnrolled + " across all lessons"),
                BorderLayout.SOUTH);
        return panel;
    }

    // ── Report 2: Income ──────────────────────────────────────────────────
    // cols: Exercise | Price/lesson | Total Enrolled | Total Income
    private JPanel buildIncomeReport() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);
        panel.add(reportTitle("Income by Exercise", "Highest earning first"),
                BorderLayout.NORTH);

        String[] cols = {"Exercise", "Price / lesson", "Total Enrolled", "Total Income"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<IncomeRow> rows = reportController.getIncomeReport();
        for (IncomeRow row : rows) {
            model.addRow(new Object[]{
                row.exerciseName(),
                "£" + String.format("%.2f", row.pricePerLesson()),
                row.totalEnrolled(),
                row.formattedIncome()
            });
        }

        // ── Same pattern as TimetableScreen ───────────────────────────────
        JTable table = ModernTable.create(model);
        ModernTable.setColumnWidths(table, 180, 120, 130, 160);
        ModernTable.setExerciseColumn(table, 0);  // coloured dot
        ModernTable.setPriceColumn   (table, 1);  // green mono right-aligned
        ModernTable.setCentreAligned (table, 2);  // enrolled count centred
        ModernTable.setPriceColumn   (table, 3);  // total income green mono

        double grandTotal = rows.stream().mapToDouble(IncomeRow::totalIncome).sum();
        panel.add(ModernTable.wrap(table), BorderLayout.CENTER);
        panel.add(footerLabel("Grand total income: £" + String.format("%.2f", grandTotal)),
                BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel reportTitle(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_SM, 0));

        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_TITLE_SM);
        t.setForeground(Theme.TEXT_DARK);

        JLabel s = new JLabel(subtitle);
        s.setFont(Theme.FONT_SMALL);
        s.setForeground(Theme.TEXT_LIGHT);

        p.add(t);
        p.add(s);
        return p;
    }

    private JLabel footerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SMALL_BOLD);
        l.setForeground(Theme.ACCENT);
        l.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_SM, 0, 0, 0));
        return l;
    }
}