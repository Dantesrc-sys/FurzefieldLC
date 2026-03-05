package com.flc.view;

import com.flc.config.Theme;
import com.flc.util.ModernTable;
import com.flc.controller.BookingController;
import com.flc.data.DataStore;
import com.flc.model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Timetable screen — browse lessons by Day or by Exercise name.
 * Shows a filterable table of lessons with capacity indicators.
 */
public class TimetableScreen extends JPanel {

    private final BookingController bookingController = new BookingController();
    private final DataStore         store             = DataStore.getInstance();

    // ── Filter state ──────────────────────────────────────────────────────────
    private String   filterMode  = "DAY";   // "DAY" or "EXERCISE"
    private Day      selectedDay = Day.SATURDAY;
    private String   selectedExercise = "Yoga";

    // ── UI refs ───────────────────────────────────────────────────────────────
    private JPanel      tableWrapper;
    private JTable      table;
    private DefaultTableModel tableModel;

    public TimetableScreen() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        add(buildFilterBar(), BorderLayout.NORTH);
        tableWrapper = buildTableWrapper();
        add(tableWrapper,     BorderLayout.CENTER);

        refresh();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FILTER BAR
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setOpaque(false);

        // ── Mode toggle row ────────────────────────────────────────────────
        JPanel modeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        modeRow.setOpaque(false);

        JLabel modeLabel = new JLabel("Browse by:");
        modeLabel.setFont(Theme.FONT_LABEL);
        modeLabel.setForeground(Theme.TEXT_MID);
        modeRow.add(modeLabel);
        modeRow.add(Box.createHorizontalStrut(Theme.SPACE_XS));

        JButton btnDay  = buildToggleBtn("Day",      true);
        JButton btnExer = buildToggleBtn("Exercise", false);

        btnDay.addActionListener(e -> {
            filterMode = "DAY";
            setToggleActive(btnDay, btnExer);
            refresh();
        });
        btnExer.addActionListener(e -> {
            filterMode = "EXERCISE";
            setToggleActive(btnExer, btnDay);
            refresh();
        });

        modeRow.add(btnDay);
        modeRow.add(btnExer);
        bar.add(modeRow);
        bar.add(Box.createVerticalStrut(Theme.SPACE_MD));

        // ── Value selector row ─────────────────────────────────────────────
        JPanel selRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        selRow.setOpaque(false);

        JLabel selLabel = new JLabel("Filter:");
        selLabel.setFont(Theme.FONT_LABEL);
        selLabel.setForeground(Theme.TEXT_MID);
        selRow.add(selLabel);

        // Day selector
        JComboBox<Day> dayCombo = new JComboBox<>(Day.values());
        dayCombo.setSelectedItem(selectedDay);
        dayCombo.addActionListener(e -> {
            selectedDay = (Day) dayCombo.getSelectedItem();
            if (filterMode.equals("DAY")) refresh();
        });

        // Exercise selector
        List<String> names = store.getExerciseTypes().stream()
                .map(ExerciseType::getName).toList();
        JComboBox<String> exerCombo = new JComboBox<>(names.toArray(new String[0]));
        exerCombo.setSelectedItem(selectedExercise);
        exerCombo.addActionListener(e -> {
            selectedExercise = (String) exerCombo.getSelectedItem();
            if (filterMode.equals("EXERCISE")) refresh();
        });

        // Week filter
        JLabel weekLabel = new JLabel("Week:");
        weekLabel.setFont(Theme.FONT_LABEL);
        weekLabel.setForeground(Theme.TEXT_MID);

        Integer[] weeks = {0, 1, 2, 3, 4, 5, 6, 7, 8}; // 0 = All
        JComboBox<Integer> weekCombo = new JComboBox<>(weeks);
        weekCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setText((Integer) value == 0 ? "All Weeks" : "Week " + value);
                return this;
            }
        });

        selRow.add(dayCombo);
        selRow.add(exerCombo);
        selRow.add(Box.createHorizontalStrut(Theme.SPACE_SM));
        selRow.add(weekLabel);
        selRow.add(weekCombo);

        // Refresh on week change
        weekCombo.addActionListener(e -> {
            int week = (Integer) weekCombo.getSelectedItem();
            refreshWithWeek(week);
        });

        bar.add(selRow);
        bar.add(Box.createVerticalStrut(Theme.SPACE_LG));

        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TABLE
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildTableWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        String[] cols = {"Week", "Day", "Time", "Exercise", "Price", "Enrolled", "Spaces", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = ModernTable.create(tableModel);
        ModernTable.setColumnWidths(table, 72, 100, 110, 130, 72, 120, 0, 120);
        ModernTable.hideColumn(table, 6);          // hide raw spaces — capacity replaces it
        ModernTable.setWeekColumn(table,     0);   // Week — chip
        ModernTable.setDayColumn(table,      1);   // Day — coloured dot
        ModernTable.setTimeColumn(table,      2);  // Time — coloured dot
        ModernTable.setExerciseColumn(table, 3);   // Exercise — coloured dot
        ModernTable.setPriceColumn(table,    4);   // Price — green mono
        ModernTable.setCapacityColumn(table, 5);   // Enrolled — bar
        // Status pill
        java.util.Map<String, Color> pillBg = new java.util.HashMap<>();
        java.util.Map<String, Color> pillFg = new java.util.HashMap<>();
        pillBg.put("Full",         Theme.STATUS_BG_RED);
        pillBg.put("1 space left", Theme.STATUS_BG_ORANGE);
        pillFg.put("Full",         Theme.STATUS_FULL);
        pillFg.put("1 space left", Theme.STATUS_ALMOST_FULL);
        ModernTable.setPillColumn(table, 7, pillBg, pillFg);

        JScrollPane scroll = ModernTable.wrap(table);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DATA LOADING
    // ═══════════════════════════════════════════════════════════════════════

    private void refresh() {
        refreshWithWeek(0);
    }

    private void refreshWithWeek(int weekFilter) {
        tableModel.setRowCount(0);

        List<Lesson> lessons = filterMode.equals("DAY")
                ? bookingController.getLessonsByDay(selectedDay)
                : bookingController.getLessonsByExerciseName(selectedExercise);

        for (Lesson l : lessons) {
            if (weekFilter != 0 && l.getWeekNumber() != weekFilter) continue;

            int spaces = l.getAvailableSpaces();
            String status = l.isFull()    ? "Full"
                          : spaces == 1   ? "1 space left"
                          : spaces + " spaces";

            tableModel.addRow(new Object[]{
                "Week " + l.getWeekNumber(),
                l.getDay().getDisplayName(),
                l.getTimeSlot().getDisplayName(),
                l.getExerciseType().getName(),
                "£" + String.format("%.2f", l.getPrice()),
                l.getEnrolledCount() + " / 4",
                spaces,
                status
            });
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STYLING HELPERS
    // ═══════════════════════════════════════════════════════════════════════


    private JButton buildToggleBtn(String label, boolean active) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean on = getBackground().equals(Theme.ACCENT);
                g2.setColor(on ? Theme.ACCENT : Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SM, Theme.RADIUS_SM);
                if (!on) {
                    g2.setColor(Theme.BORDER);
                    g2.setStroke(Theme.STROKE_THIN);
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, Theme.RADIUS_SM, Theme.RADIUS_SM);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BTN_SM);
        btn.setForeground(active ? Color.WHITE : Theme.TEXT_MID);
        btn.setBackground(active ? Theme.ACCENT : Theme.BG_CARD);
        btn.setPreferredSize(new Dimension(100, 34));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setToggleActive(JButton active, JButton inactive) {
        active.setBackground(Theme.ACCENT);
        active.setForeground(Color.WHITE);
        inactive.setBackground(Theme.BG_CARD);
        inactive.setForeground(Theme.TEXT_MID);
        active.repaint();
        inactive.repaint();
    }
}