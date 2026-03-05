package com.flc.view;

import com.flc.config.Theme;
import com.flc.controller.BookingController;
import com.flc.controller.MemberController;
import com.flc.data.DataStore;
import com.flc.model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Bookings screen — create a new booking, change an existing one, or cancel.
 * Layout: left panel (select member + lesson) | right panel (member's bookings)
 */
public class BookingScreen extends JPanel {

    private final BookingController bookingController = new BookingController();
    private final MemberController  memberController  = new MemberController();
    private final DataStore         store             = DataStore.getInstance();

    // ── Selected state ────────────────────────────────────────────────────────
    private Member  selectedMember  = null;
    private Lesson  selectedLesson  = null;
    private Booking selectedBooking = null;

    // ── UI refs ───────────────────────────────────────────────────────────────
    private JComboBox<Member>  memberCombo;
    private JComboBox<Day>     dayCombo;
    private JComboBox<Integer> weekCombo;
    private JTable             lessonTable;
    private DefaultTableModel  lessonModel;
    private JTable             bookingTable;
    private DefaultTableModel  bookingModel;
    private JButton            bookBtn;
    private JButton            changeBtn;
    private JButton            cancelBtn;
    private JLabel             statusLabel;

    public BookingScreen() {
        setLayout(new BorderLayout(Theme.SPACE_LG, 0));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        add(buildLeftPanel(),  BorderLayout.WEST);
        add(buildRightPanel(), BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEFT PANEL — member selector + lesson picker
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(380, 0));

        // ── Step 1: Select member ──────────────────────────────────────────
        panel.add(buildSectionLabel("Step 1 — Select Member"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        memberCombo = new JComboBox<>();
        store.getMembers().forEach(memberCombo::addItem);
        memberCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                if (val instanceof Member m)
                    setText(m.getMemberId() + "  —  " + m.getName());
                return this;
            }
        });
        styleCombo(memberCombo);
        memberCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberCombo.addActionListener(e -> onMemberSelected());
        panel.add(memberCombo);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // ── Step 2: Filter lessons ─────────────────────────────────────────
        panel.add(buildSectionLabel("Step 2 — Filter Lessons"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        dayCombo = new JComboBox<>(Day.values());
        styleCombo(dayCombo);
        dayCombo.setPreferredSize(new Dimension(130, 36));

        Integer[] weeks = {0,1,2,3,4,5,6,7,8};
        weekCombo = new JComboBox<>(weeks);
        weekCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setText((Integer)val == 0 ? "All Weeks" : "Week " + val);
                return this;
            }
        });
        styleCombo(weekCombo);
        weekCombo.setPreferredSize(new Dimension(120, 36));

        dayCombo.addActionListener(e  -> refreshLessons());
        weekCombo.addActionListener(e -> refreshLessons());

        filterRow.add(dayCombo);
        filterRow.add(weekCombo);
        panel.add(filterRow);
        panel.add(Box.createVerticalStrut(Theme.SPACE_MD));

        // ── Step 3: Pick lesson ────────────────────────────────────────────
        panel.add(buildSectionLabel("Step 3 — Pick a Lesson"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));
        panel.add(buildLessonTable());
        panel.add(Box.createVerticalStrut(Theme.SPACE_LG));

        // ── Action buttons ─────────────────────────────────────────────────
        panel.add(buildActionButtons());
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        // ── Status message ─────────────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_MID);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);

        refreshLessons();
        return panel;
    }

    private JScrollPane buildLessonTable() {
        String[] cols = {"Time", "Exercise", "Price", "Spaces"};
        lessonModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        lessonTable = new JTable(lessonModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                else
                    c.setBackground(Theme.TABLE_ROW_SELECTED);
                c.setForeground(Theme.TEXT_DARK);
                return c;
            }
        };
        styleTable(lessonTable);
        lessonTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onLessonSelected();
        });

        JScrollPane scroll = new JScrollPane(lessonTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHT));
        scroll.setPreferredSize(new Dimension(380, 180));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.getViewport().setBackground(Theme.BG_CARD);
        return scroll;
    }

    private JPanel buildActionButtons() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_SM, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookBtn   = buildBtn("Book",   Theme.BTN_PRIMARY,  Theme.BTN_HOVER);
        changeBtn = buildBtn("Change", new Color(0x6C63FF), new Color(0x5A52D5));
        cancelBtn = buildBtn("Cancel", Theme.BTN_DANGER,   Theme.BTN_DANGER_HOVER);

        bookBtn.setEnabled(false);
        changeBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

        bookBtn.addActionListener(e   -> onBook());
        changeBtn.addActionListener(e -> onChange());
        cancelBtn.addActionListener(e -> onCancel());

        row.add(bookBtn);
        row.add(changeBtn);
        row.add(cancelBtn);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RIGHT PANEL — member's current bookings
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Current Bookings");
        title.setFont(Theme.FONT_TITLE_SM);
        title.setForeground(Theme.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_MD, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Week", "Day", "Time", "Exercise", "Price"};
        bookingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        bookingTable = new JTable(bookingModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                else
                    c.setBackground(Theme.TABLE_ROW_SELECTED);
                c.setForeground(Theme.TEXT_DARK);
                return c;
            }
        };
        styleTable(bookingTable);
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onBookingSelected();
        });

        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Theme.BG_CARD);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EVENT HANDLERS
    // ═══════════════════════════════════════════════════════════════════════

    private void onMemberSelected() {
        selectedMember  = (Member) memberCombo.getSelectedItem();
        selectedBooking = null;
        refreshBookings();
        updateButtons();
    }

    private void onLessonSelected() {
        int row = lessonTable.getSelectedRow();
        if (row < 0) { selectedLesson = null; updateButtons(); return; }

        Day     day  = (Day)     dayCombo.getSelectedItem();
        int     week = (Integer) weekCombo.getSelectedItem();

        List<Lesson> lessons = getLessonsForFilter(day, week);
        if (row < lessons.size()) selectedLesson = lessons.get(row);
        updateButtons();
    }

    private void onBookingSelected() {
        int row = bookingTable.getSelectedRow();
        if (row < 0 || selectedMember == null) {
            selectedBooking = null; updateButtons(); return;
        }
        List<Booking> bookings = store.findBookingsByMember(selectedMember);
        if (row < bookings.size()) selectedBooking = bookings.get(row);
        updateButtons();
    }

    private void onBook() {
        if (selectedMember == null || selectedLesson == null) return;
        try {
            bookingController.createBooking(selectedMember, selectedLesson);
            setStatus("✓ Booked " + selectedLesson.getExerciseType().getName()
                    + " — Week " + selectedLesson.getWeekNumber(), Theme.TEXT_SUCCESS);
            refreshAll();
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    private void onChange() {
        if (selectedBooking == null || selectedLesson == null) return;
        try {
            bookingController.changeBooking(selectedBooking, selectedLesson);
            setStatus("✓ Booking changed to " + selectedLesson.getExerciseType().getName()
                    + " — Week " + selectedLesson.getWeekNumber(), Theme.TEXT_SUCCESS);
            refreshAll();
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    private void onCancel() {
        if (selectedBooking == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking for " + selectedBooking.getLesson().getExerciseType().getName()
                + "?\nWeek " + selectedBooking.getLesson().getWeekNumber()
                + " — " + selectedBooking.getLesson().getDay().getDisplayName(),
                "Confirm Cancel", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            bookingController.cancelBooking(selectedBooking);
            selectedBooking = null;
            setStatus("✓ Booking cancelled", Theme.TEXT_SUCCESS);
            refreshAll();
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REFRESH HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private void refreshLessons() {
        lessonModel.setRowCount(0);
        selectedLesson = null;

        Day day  = (Day)     dayCombo.getSelectedItem();
        int week = (Integer) weekCombo.getSelectedItem();

        for (Lesson l : getLessonsForFilter(day, week)) {
            lessonModel.addRow(new Object[]{
                l.getTimeSlot().getDisplayName(),
                l.getExerciseType().getName(),
                "£" + String.format("%.2f", l.getPrice()),
                l.getAvailableSpaces() + " / 4"
            });
        }
        updateButtons();
    }

    private void refreshBookings() {
        bookingModel.setRowCount(0);
        if (selectedMember == null) return;
        for (Booking b : store.findBookingsByMember(selectedMember)) {
            Lesson l = b.getLesson();
            bookingModel.addRow(new Object[]{
                b.getBookingId(),
                "Week " + l.getWeekNumber(),
                l.getDay().getDisplayName(),
                l.getTimeSlot().getDisplayName(),
                l.getExerciseType().getName(),
                "£" + String.format("%.2f", l.getPrice())
            });
        }
    }

    private void refreshAll() {
        refreshLessons();
        refreshBookings();
        updateButtons();
    }

    private List<Lesson> getLessonsForFilter(Day day, int week) {
        return store.getLessons().stream()
                .filter(l -> l.getDay() == day)
                .filter(l -> week == 0 || l.getWeekNumber() == week)
                .toList();
    }

    private void updateButtons() {
        boolean hasMember  = selectedMember  != null;
        boolean hasLesson  = selectedLesson  != null;
        boolean hasBooking = selectedBooking != null;
        boolean lessonNotFull = hasLesson && !selectedLesson.isFull();

        bookBtn.setEnabled(hasMember && hasLesson && lessonNotFull);
        changeBtn.setEnabled(hasMember && hasLesson && hasBooking && lessonNotFull);
        cancelBtn.setEnabled(hasMember && hasBooking);
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STYLING HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private JLabel buildSectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.ACCENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_TABLE_CELL);
        t.setRowHeight(Theme.TABLE_ROW_HEIGHT);
        t.setGridColor(Theme.TABLE_GRID);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(Theme.TABLE_ROW_SELECTED);
        t.setSelectionForeground(Theme.TEXT_DARK);
        t.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader h = t.getTableHeader();
        h.setFont(Theme.FONT_TABLE_HEADER);
        h.setBackground(Theme.TABLE_HEADER_BG);
        h.setForeground(Theme.TEXT_MID);
        h.setPreferredSize(new Dimension(0, Theme.TABLE_HEADER_HEIGHT));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
    }

    private <T> void styleCombo(JComboBox<T> combo) {
        combo.setFont(Theme.FONT_INPUT);
        combo.setBackground(Theme.BG_CARD);
        combo.setPreferredSize(new Dimension(160, 36));
    }

    private JButton buildBtn(String label, Color bg, Color hover) {
        JButton btn = new JButton(label) {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(!isEnabled() ? Theme.BTN_DISABLED : hov ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_BTN_SM);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(90, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}