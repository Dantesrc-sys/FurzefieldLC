package com.flc.view;

import com.flc.config.Theme;
import com.flc.controller.BookingController;
import com.flc.controller.MemberController;
import com.flc.data.DataStore;
import com.flc.model.Booking;
import com.flc.model.Member;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Members screen — view all members, add new ones, edit name/phone.
 * Layout: left = member list table | right = detail + edit panel
 */
public class MemberScreen extends JPanel {

    private final MemberController  memberController  = new MemberController();
    private final BookingController bookingController = new BookingController();
    private final DataStore         store             = DataStore.getInstance();

    // ── Selected state ────────────────────────────────────────────────────────
    private Member selectedMember = null;

    // ── UI refs ───────────────────────────────────────────────────────────────
    private JTable            memberTable;
    private DefaultTableModel memberModel;
    private JLabel            detailName;
    private JLabel            detailId;
    private JLabel            detailPhone;
    private JLabel            detailBookings;
    private JTextField        editNameField;
    private JTextField        editPhoneField;
    private JButton           saveBtn;
    private JButton           addBtn;
    private JLabel            statusLabel;

    public MemberScreen() {
        setLayout(new BorderLayout(Theme.SPACE_LG, 0));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));

        add(buildLeftPanel(),  BorderLayout.CENTER);
        add(buildRightPanel(), BorderLayout.EAST);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEFT — member list
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        panel.setOpaque(false);

        // ── Top bar: title + add button ────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("All Members");
        title.setFont(Theme.FONT_TITLE_SM);
        title.setForeground(Theme.TEXT_DARK);
        top.add(title, BorderLayout.WEST);

        addBtn = buildPrimaryBtn("+ Add Member", Theme.ACCENT, Theme.ACCENT_DARK);
        addBtn.addActionListener(e -> showAddMemberDialog());
        top.add(addBtn, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        // ── Search bar ────────────────────────────────────────────────────
        JTextField searchField = new JTextField();
        searchField.setFont(Theme.FONT_INPUT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText().trim());
            }
        });
        panel.add(searchField, BorderLayout.CENTER);

        // ── Table ─────────────────────────────────────────────────────────
        String[] cols = {"ID", "Name", "Phone", "Bookings"};
        memberModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        memberTable = new JTable(memberModel) {
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
        styleTable(memberTable);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onMemberSelected();
        });

        JScrollPane scroll = new JScrollPane(memberTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHT));
        scroll.getViewport().setBackground(Theme.BG_CARD);
        panel.add(scroll, BorderLayout.SOUTH);

        // Give scroll the most space
        panel.setLayout(new BorderLayout(0, Theme.SPACE_MD));
        panel.add(top,         BorderLayout.NORTH);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(scroll,      BorderLayout.SOUTH);

        // Make scroll fill available space
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchField, scroll);
        split.setDividerSize(0);
        split.setResizeWeight(0.0);
        split.setBorder(null);
        split.setOpaque(false);

        panel.removeAll();
        panel.setLayout(new BorderLayout(0, Theme.SPACE_MD));
        panel.add(top,    BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(0, Theme.SPACE_SM));
        mid.setOpaque(false);
        mid.add(searchField, BorderLayout.NORTH);
        mid.add(scroll,      BorderLayout.CENTER);
        panel.add(mid, BorderLayout.CENTER);

        refreshTable();
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RIGHT — member detail + edit
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, Theme.SPACE_LG, 0, 0)
        ));

        // ── Detail card ───────────────────────────────────────────────────
        JPanel detailCard = buildDetailCard();
        panel.add(detailCard);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // ── Edit form ─────────────────────────────────────────────────────
        JLabel editTitle = new JLabel("Edit Member");
        editTitle.setFont(Theme.FONT_TITLE_SM);
        editTitle.setForeground(Theme.TEXT_DARK);
        editTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(editTitle);
        panel.add(Box.createVerticalStrut(Theme.SPACE_MD));

        panel.add(formLabel("Name"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        editNameField = buildTextField();
        panel.add(editNameField);
        panel.add(Box.createVerticalStrut(Theme.SPACE_MD));

        panel.add(formLabel("Phone"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        editPhoneField = buildTextField();
        panel.add(editPhoneField);
        panel.add(Box.createVerticalStrut(Theme.SPACE_LG));

        saveBtn = buildPrimaryBtn("Save Changes", Theme.ACCENT, Theme.ACCENT_DARK);
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(e -> onSave());
        panel.add(saveBtn);
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.TEXT_MID);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel buildDetailCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_CARD, Theme.RADIUS_CARD);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        detailName     = detailLine("Member Name", Theme.FONT_TITLE_SM, Theme.TEXT_DARK);
        detailId       = detailLine("ID: M000",       Theme.FONT_SMALL, Theme.TEXT_MID);
        detailPhone    = detailLine("Phone: 00000000000",    Theme.FONT_SMALL, Theme.TEXT_MID);
        detailBookings = detailLine("Bookings: 00", Theme.FONT_SMALL, Theme.ACCENT);

        card.add(detailName);
        card.add(Box.createVerticalStrut(Theme.SPACE_XS));
        card.add(detailId);
        card.add(detailPhone);
        card.add(detailBookings);
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EVENT HANDLERS
    // ═══════════════════════════════════════════════════════════════════════

    private void onMemberSelected() {
        int row = memberTable.getSelectedRow();
        if (row < 0) { selectedMember = null; clearDetail(); return; }

        String id = (String) memberModel.getValueAt(row, 0);
        selectedMember = store.findMemberById(id);
        if (selectedMember == null) return;

        int bookingCount = store.findBookingsByMember(selectedMember).size();
        detailName.setText(selectedMember.getName());
        detailId.setText("ID: " + selectedMember.getMemberId());
        detailPhone.setText("Phone: " + selectedMember.getPhone());
        detailBookings.setText("Bookings: " + bookingCount);

        editNameField.setText(selectedMember.getName());
        editPhoneField.setText(selectedMember.getPhone());
        saveBtn.setEnabled(true);
        statusLabel.setText(" ");
    }

    private void onSave() {
        if (selectedMember == null) return;
        String newName  = editNameField.getText().trim();
        String newPhone = editPhoneField.getText().trim();
        try {
            memberController.updateName(selectedMember, newName);
            memberController.updatePhone(selectedMember, newPhone);
            setStatus("✓ Member updated", Theme.TEXT_SUCCESS);
            refreshTable();
            // Re-select updated member
            detailName.setText(selectedMember.getName());
            detailPhone.setText("Phone: " + selectedMember.getPhone());
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    private void showAddMemberDialog() {
        JTextField nameF  = buildDialogField();
        JTextField phoneF = buildDialogField();

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(
                Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM, Theme.SPACE_SM));
        form.add(new JLabel("Name:"));
        form.add(Box.createVerticalStrut(Theme.SPACE_XS));
        form.add(nameF);
        form.add(Box.createVerticalStrut(Theme.SPACE_MD));
        form.add(new JLabel("Phone:"));
        form.add(Box.createVerticalStrut(Theme.SPACE_XS));
        form.add(phoneF);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Add New Member", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Member m = memberController.addMember(nameF.getText(), phoneF.getText());
            setStatus("✓ Added " + m.getName(), Theme.TEXT_SUCCESS);
            refreshTable();
        } catch (Exception ex) {
            setStatus("✗ " + ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TABLE HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private void refreshTable() {
        memberModel.setRowCount(0);
        for (Member m : store.getMembers()) {
            int count = store.findBookingsByMember(m).size();
            memberModel.addRow(new Object[]{m.getMemberId(), m.getName(), m.getPhone(), count});
        }
    }

    private void filterTable(String query) {
        memberModel.setRowCount(0);
        for (Member m : store.getMembers()) {
            if (query.isEmpty() || m.getName().toLowerCase().contains(query.toLowerCase())) {
                int count = store.findBookingsByMember(m).size();
                memberModel.addRow(new Object[]{m.getMemberId(), m.getName(), m.getPhone(), count});
            }
        }
    }

    private void clearDetail() {
        detailName.setText("Member Name");
        detailId.setText("ID: M000");
        detailPhone.setText("Phone: 00000000000");
        detailBookings.setText("Bookings: 00");
        editNameField.setText("");
        editPhoneField.setText("");
        saveBtn.setEnabled(false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STYLING HELPERS
    // ═══════════════════════════════════════════════════════════════════════

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
        int[] widths = {70, 160, 130, 80};
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private JLabel detailLine(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_MID);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField buildTextField() {
        JTextField f = new JTextField();
        f.setFont(Theme.FONT_INPUT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JTextField buildDialogField() {
        JTextField f = new JTextField(20);
        f.setFont(Theme.FONT_INPUT);
        return f;
    }

    private JButton buildPrimaryBtn(String label, Color bg, Color hover) {
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
        btn.setPreferredSize(new Dimension(140, 36));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
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