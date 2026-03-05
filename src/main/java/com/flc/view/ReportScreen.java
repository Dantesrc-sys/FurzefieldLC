package com.flc.view;

import com.flc.config.Theme;
import com.flc.controller.ReportController;
import com.flc.controller.ReportController.AttendanceRow;
import com.flc.controller.ReportController.IncomeRow;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Reports screen — two side-by-side reports:
 *  Left  → Attendance & Rating report (per lesson)
 *  Right → Income report (per exercise type, highest first)
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

        // Highlight card — highest income exercise
        IncomeRow top = reportController.getHighestIncomeExercise();
        if (top != null) {
            JPanel badge = buildHighlightBadge(top);
            panel.add(badge, BorderLayout.EAST);
        }
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
    private JPanel buildAttendanceReport() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);

        panel.add(reportTitle("Attendance & Rating", "Per lesson · sorted by week"),
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

        JTable table = buildReportTable(model);

        // Colour the rating column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                if (s.equals("No reviews"))
                    setForeground(Theme.TEXT_LIGHT);
                else {
                    try {
                        double avg = Double.parseDouble(s.split(" ")[0]);
                        setForeground(avg >= 4.0 ? Theme.TEXT_SUCCESS
                                    : avg >= 3.0 ? Theme.TEXT_WARNING
                                    : Theme.TEXT_ERROR);
                    } catch (Exception e) {
                        setForeground(Theme.TEXT_MID);
                    }
                }
                if (!sel) setBackground(row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                return c;
            }
        });

        // Summary footer
        long totalEnrolled = rows.stream().mapToLong(AttendanceRow::enrolledCount).sum();
        JLabel footer = footerLabel("Total enrolled across all lessons: " + totalEnrolled);

        panel.add(wrapInScroll(table), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    // ── Report 2: Income ──────────────────────────────────────────────────
    private JPanel buildIncomeReport() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);

        panel.add(reportTitle("Income by Exercise", "Highest earning first"),
                BorderLayout.NORTH);

        String[] cols = {"Exercise", "Price/lesson", "Total Enrolled", "Total Income"};
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

        JTable table = buildReportTable(model);

        // Colour income column — top earner highlighted
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(row == 0 ? Theme.FONT_BODY_BOLD : Theme.FONT_TABLE_CELL);
                setForeground(row == 0 ? Theme.ACCENT : Theme.TEXT_DARK);
                if (!sel) setBackground(row == 0 ? Theme.ACCENT_LIGHT
                        : row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                return c;
            }
        });

        // Summary footer
        double grandTotal = rows.stream().mapToDouble(IncomeRow::totalIncome).sum();
        JLabel footer = footerLabel("Grand total income: £" + String.format("%.2f", grandTotal));

        panel.add(wrapInScroll(table), BorderLayout.CENTER);
        panel.add(footer,             BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JTable buildReportTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                else
                    c.setBackground(Theme.TABLE_ROW_SELECTED);
                if (c.getForeground().equals(getForeground()))
                    c.setForeground(Theme.TEXT_DARK);
                return c;
            }
        };
        table.setFont(Theme.FONT_TABLE_CELL);
        table.setRowHeight(Theme.TABLE_ROW_HEIGHT);
        table.setGridColor(Theme.TABLE_GRID);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.TABLE_ROW_SELECTED);

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_TABLE_HEADER);
        header.setBackground(Theme.TABLE_HEADER_BG);
        header.setForeground(Theme.TEXT_MID);
        header.setPreferredSize(new Dimension(0, Theme.TABLE_HEADER_HEIGHT));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        return table;
    }

    private JScrollPane wrapInScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Theme.BG_CARD);
        return scroll;
    }

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