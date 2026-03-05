package com.flc.view;

import com.flc.config.Theme;
import com.flc.util.ModernTable;
import com.flc.controller.MemberController;
import com.flc.data.DataStore;
import com.flc.data.persistence.JsonStore;
import com.flc.model.Member;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Members screen — view all members, add new ones, edit name/phone.
 * Add member uses an inline panel — no JOptionPane dialogs.
 * Layout: left = member list | right = detail / edit / add panel
 */
public class MemberScreen extends JPanel {

    private final MemberController memberController = new MemberController();
    private final DataStore        store            = DataStore.getInstance();

    // ── Selected state ────────────────────────────────────────────────────────
    private Member selectedMember = null;

    // ── UI refs ───────────────────────────────────────────────────────────────
    private JTable            memberTable;
    private DefaultTableModel memberModel;
    private JLabel            detailName;
    private JLabel            detailId;
    private JLabel            detailPhone;
    private JLabel            detailBookings;
    private JTextField  editNameField;
    private JTextField  editPhoneField;
    private JButton           saveBtn;
    private JLabel            statusLabel;

    // ── Right panel cards (switched via CardLayout) ────────────────────────────
    private JPanel      rightCards;
    private CardLayout  rightCardLayout;

    private static final String CARD_DETAIL = "DETAIL";
    private static final String CARD_ADD    = "ADD";

    // ── Add form fields ───────────────────────────────────────────────────────
    private JTextField addNameField;
    private JTextField addPhoneField;
    private JLabel     addStatusLabel;

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

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("All Members");
        title.setFont(Theme.FONT_TITLE_SM);
        title.setForeground(Theme.TEXT_DARK);
        top.add(title, BorderLayout.WEST);

        JButton addBtn = buildBtn("Add Member", Theme.ACCENT, Theme.ACCENT_DARK);
        addBtn.addActionListener(e -> showAddPanel());
        top.add(addBtn, BorderLayout.EAST);

        // Search bar
        JTextField searchField = new JTextField();
        searchField.setFont(Theme.FONT_INPUT);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterTable(searchField.getText().trim());
            }
        });

        // Table
        String[] cols = {"ID", "Name", "Phone", "Bookings"};
        memberModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        memberTable = ModernTable.create(memberModel);

        ModernTable.setColumnWidths(memberTable, 70, 200, 190, 110);
        ModernTable.setBoldColumn(memberTable,    1);  // Name
        ModernTable.setCodeColumn(memberTable,    0);  // ID chip

        // Bookings count — pill: 0=grey, else green
        java.util.Map<String, Color> bkBg = new java.util.HashMap<>();
        java.util.Map<String, Color> bkFg = new java.util.HashMap<>();
        bkBg.put("0", Theme.BG_ALT);       bkFg.put("0", Theme.TEXT_LIGHT);
        ModernTable.setPillColumn(memberTable, 3, bkBg, bkFg);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onMemberSelected();
        });

        JScrollPane scroll = ModernTable.wrap(memberTable);

        JPanel mid = new JPanel(new BorderLayout(0, Theme.SPACE_SM));
        mid.setOpaque(false);
        mid.add(searchField, BorderLayout.NORTH);
        mid.add(scroll,      BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(mid, BorderLayout.CENTER);

        refreshTable();
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RIGHT — CardLayout: DETAIL card + ADD card
    // ═══════════════════════════════════════════════════════════════════════

    private JPanel buildRightPanel() {
        rightCardLayout = new CardLayout();
        rightCards      = new JPanel(rightCardLayout);
        rightCards.setOpaque(false);
        rightCards.setPreferredSize(new Dimension(280, 0));

        rightCards.add(buildDetailCard(), CARD_DETAIL);
        rightCards.add(buildAddCard(),    CARD_ADD);

        rightCardLayout.show(rightCards, CARD_DETAIL);
        return rightCards;
    }

    // ── DETAIL card ───────────────────────────────────────────────────────────
    private JPanel buildDetailCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, Theme.SPACE_LG, 0, 0)
        ));

        // Member info card
        JPanel infoCard = buildInfoCard();
        panel.add(infoCard);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Edit form title
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

        saveBtn = buildBtn("Save Changes", Theme.ACCENT, Theme.ACCENT_DARK);
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

    private JPanel buildInfoCard() {
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

        detailName     = detailLine("Select a member", Theme.FONT_TITLE_SM, Theme.TEXT_DARK);
        detailId       = detailLine("ID: —",           Theme.FONT_SMALL,    Theme.TEXT_MID);
        detailPhone    = detailLine("Phone: —",        Theme.FONT_SMALL,    Theme.TEXT_MID);
        detailBookings = detailLine("Bookings: —",     Theme.FONT_SMALL,    Theme.ACCENT);

        card.add(detailName);
        card.add(Box.createVerticalStrut(Theme.SPACE_XS));
        card.add(detailId);
        card.add(detailPhone);
        card.add(detailBookings);
        return card;
    }

    // ── ADD card ──────────────────────────────────────────────────────────────
    private JPanel buildAddCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, Theme.SPACE_LG, 0, 0)
        ));

        // Header row: title + cancel button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel addTitle = new JLabel("New Member");
        addTitle.setFont(Theme.FONT_TITLE_SM);
        addTitle.setForeground(Theme.TEXT_DARK);
        header.add(addTitle, BorderLayout.WEST);

        JButton cancelBtn = new JButton("Cancel") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, Theme.RADIUS_BTN, Theme.RADIUS_BTN);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cancelBtn.setFont(Theme.FONT_BTN_SM);
        cancelBtn.setForeground(Theme.TEXT_MID);
        cancelBtn.setPreferredSize(new Dimension(70, 30));
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> showDetailPanel());
        header.add(cancelBtn, BorderLayout.EAST);

        panel.add(header);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER_LIGHT);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Name field
        panel.add(formLabel("Full Name"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        addNameField = buildTextField();
        panel.add(addNameField);
        panel.add(Box.createVerticalStrut(Theme.SPACE_MD));

        // Phone field
        panel.add(formLabel("Phone Number"));
        panel.add(Box.createVerticalStrut(Theme.SPACE_XS));
        addPhoneField = buildTextField();
        panel.add(addPhoneField);
        panel.add(Box.createVerticalStrut(Theme.SPACE_XL));

        // Submit button — full width feel
        JButton confirmBtn = buildBtn("Add Member", Theme.ACCENT, Theme.ACCENT_DARK);
        confirmBtn.addActionListener(e -> onAddMember());
        panel.add(confirmBtn);
        panel.add(Box.createVerticalStrut(Theme.SPACE_SM));

        addStatusLabel = new JLabel(" ");
        addStatusLabel.setFont(Theme.FONT_SMALL);
        addStatusLabel.setForeground(Theme.TEXT_MID);
        addStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(addStatusLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PANEL SWITCHING
    // ═══════════════════════════════════════════════════════════════════════

    private void showAddPanel() {
        addNameField.setText("");
        addPhoneField.setText("");
        addStatusLabel.setText(" ");
        rightCardLayout.show(rightCards, CARD_ADD);
        addNameField.requestFocusInWindow();
    }

    private void showDetailPanel() {
        rightCardLayout.show(rightCards, CARD_DETAIL);
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

        // Switch back to detail card if on add card
        showDetailPanel();

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
        try {
            memberController.updateName(selectedMember,  editNameField.getText().trim());
            memberController.updatePhone(selectedMember, editPhoneField.getText().trim());
            JsonStore.save();
            setStatus("Member updated", Theme.TEXT_SUCCESS);
            refreshTable();
            detailName.setText(selectedMember.getName());
            detailPhone.setText("Phone: " + selectedMember.getPhone());
        } catch (Exception ex) {
            setStatus(ex.getMessage(), Theme.TEXT_ERROR);
        }
    }

    private void onAddMember() {
        try {
            Member m = memberController.addMember(
                    addNameField.getText().trim(),
                    addPhoneField.getText().trim());
            JsonStore.save();
            refreshTable();
            addStatusLabel.setText("Added " + m.getName());
            addStatusLabel.setForeground(Theme.TEXT_SUCCESS);
            addNameField.setText("");
            addPhoneField.setText("");
            // Auto-close after short delay so user sees the confirmation
            Timer t = new Timer(1200, e -> showDetailPanel());
            t.setRepeats(false);
            t.start();
        } catch (Exception ex) {
            addStatusLabel.setText(ex.getMessage());
            addStatusLabel.setForeground(Theme.TEXT_ERROR);
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
        detailName.setText("Select a member");
        detailId.setText("ID: —");
        detailPhone.setText("Phone: —");
        detailBookings.setText("Bookings: —");
        editNameField.setText("");
        editPhoneField.setText("");
        saveBtn.setEnabled(false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STYLING HELPERS
    // ═══════════════════════════════════════════════════════════════════════


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