package com.flc.util;

import com.flc.config.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Modern table factory.
 *
 * Every renderer paints itself entirely — no DefaultTableCellRenderer background bleed, no clipping issues. Each cell
 * owns its background.
 *
 * Renderers: default — padded text, hover, alternating rows bold — bold font, dark text pill — rounded badge with
 * colour map (status, availability) dot — coloured circle prefix + bold text (exercise, day) capacity — "N / 4" with
 * mini fill bar + colour price — green monospaced right-aligned stars — gold star rating week — "Week N" with subtle
 * accent chip
 */
public final class ModernTable {

    // ── Shared exercise colour palette ─────────────────────────────────────────
    public static final Map<String, Color> EXERCISE_COLOURS = new HashMap<>();
    static {
        EXERCISE_COLOURS.put("Yoga", new Color(0x9B59B6));
        EXERCISE_COLOURS.put("Zumba", new Color(0xE74C3C));
        EXERCISE_COLOURS.put("Aquacise", new Color(0x2980B9));
        EXERCISE_COLOURS.put("Box Fit", new Color(0xE67E22));
        EXERCISE_COLOURS.put("Body Blitz", new Color(0x27AE60));
        EXERCISE_COLOURS.put("Pilates", new Color(0xD35400));
        EXERCISE_COLOURS.put("Spin", new Color(0x1ABC9C));
        EXERCISE_COLOURS.put("Circuits", new Color(0xC0392B));
    }

    // ── Day colour palette ─────────────────────────────────────────────────────
    public static final Map<String, Color> DAY_COLOURS = new HashMap<>();
    static {
        DAY_COLOURS.put("Saturday", new Color(0x2D6A4F));
        DAY_COLOURS.put("Sunday", new Color(0x6C3483));
    }

    // ── Time colour palette ─────────────────────────────────────────────────────
    public static final Map<String, Color> TIME_COLOURS = new HashMap<>();
    static {
        TIME_COLOURS.put("Morning", new Color(0xF4A261)); // soft sunrise orange
        TIME_COLOURS.put("Afternoon", new Color(0x2A9D8F)); // bright daylight teal
        TIME_COLOURS.put("Evening", new Color(0xE76F51)); // sunset orange/red
        TIME_COLOURS.put("Night", new Color(0x264653)); // deep night blue
    }

    // ── Uniform visual constants — same across every renderer ─────────────────
    private static final int PILL_H = 22; // pill badge height
    private static final int PILL_PAD = 12; // pill horizontal padding
    private static final int DOT_D = 9; // coloured dot diameter
    private static final int CHIP_H = 22; // week chip height
    private static final int CHIP_PAD = 10; // chip horizontal padding

    private ModernTable() {
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FACTORY
    // ═══════════════════════════════════════════════════════════════════════

    public static JTable create(DefaultTableModel model) {
        JTable table = new JTable(model) {
            int hoveredRow = -1;
            {
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int r = rowAtPoint(e.getPoint());
                        if (r != hoveredRow) {
                            hoveredRow = r;
                            repaint();
                        }
                    }
                });
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hoveredRow != -1) {
                            hoveredRow = -1;
                            repaint();
                        }
                    }
                });
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                // Only apply row background to standard JLabel-based renderers
                // Custom panel renderers manage their own background
                if (c instanceof JLabel) {
                    if (isRowSelected(row))
                        c.setBackground(Theme.TABLE_ROW_SELECTED);
                    else if (row == hoveredRow)
                        c.setBackground(Theme.TABLE_ROW_HOVER);
                    else
                        c.setBackground(row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN);
                    c.setForeground(Theme.TEXT_DARK);
                }
                return c;
            }
        };
        applyCore(table);
        return table;
    }

    private static void applyCore(JTable t) {
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(Theme.TABLE_ROW_HEIGHT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(Theme.TABLE_GRID);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(Theme.TABLE_ROW_SELECTED);
        t.setSelectionForeground(Theme.TEXT_DARK);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setBackground(Theme.BG_CARD);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Default renderer for all columns
        DefaultRenderer def = new DefaultRenderer();
        for (int i = 0; i < t.getColumnCount(); i++)
            t.getColumnModel().getColumn(i).setCellRenderer(def);

        applyHeader(t.getTableHeader());
    }

    private static void applyHeader(JTableHeader h) {
        h.setFont(Theme.FONT_TABLE_HEADER);
        h.setBackground(Theme.TABLE_HEADER_BG);
        h.setForeground(Theme.TEXT_MID);
        h.setPreferredSize(new Dimension(0, Theme.TABLE_HEADER_HEIGHT));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.ACCENT_LIGHT));
        h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int row, int col) {
                JLabel l = new JLabel(v == null ? "" : v.toString());
                l.setFont(Theme.FONT_TABLE_HEADER);
                l.setForeground(Theme.TEXT_MID);
                l.setBackground(Theme.TABLE_HEADER_BG);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, Theme.TABLE_CELL_PAD_H, 0, Theme.TABLE_CELL_PAD_H));
                return l;
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // COLUMN SETUP
    // ═══════════════════════════════════════════════════════════════════════

    public static void setColumnWidths(JTable t, int... widths) {
        for (int i = 0; i < widths.length && i < t.getColumnCount(); i++) {
            if (widths[i] == 0) {
                hideColumn(t, i);
                continue;
            }
            TableColumn col = t.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            col.setMinWidth(widths[i]); // never truncate below this
            col.setMaxWidth(Integer.MAX_VALUE); // grow freely to fill space
        }
    }

    public static void hideColumn(JTable t, int col) {
        TableColumn c = t.getColumnModel().getColumn(col);
        c.setMinWidth(0);
        c.setMaxWidth(0);
        c.setWidth(0);
    }

    public static void setBoldColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new BoldRenderer());
    }

    public static void setRightAligned(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultRenderer(SwingConstants.RIGHT));
    }

    public static void setCentreAligned(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DefaultRenderer(SwingConstants.CENTER));
    }

    public static void setPriceColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new PriceRenderer());
    }

    public static void setStarColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new StarRenderer());
    }

    public static void setCapacityColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new CapacityRenderer());
    }

    public static void setWeekColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new WeekRenderer());
    }

    public static void setExerciseColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DotRenderer(EXERCISE_COLOURS));
    }

    public static void setDayColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DotRenderer(DAY_COLOURS));
    }

    public static void setTimeColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new TimeChipRenderer(TIME_COLOURS));
    }

    public static void setDotColumn(JTable t, int col, Map<String, Color> map) {
        t.getColumnModel().getColumn(col).setCellRenderer(new DotRenderer(map));
    }

    public static void setCodeColumn(JTable t, int col) {
        t.getColumnModel().getColumn(col).setCellRenderer(new CodeTagRenderer());
    }

    public static void setPillColumn(JTable t, int col, Map<String, Color> bg, Map<String, Color> fg) {
        t.getColumnModel().getColumn(col).setCellRenderer(new PillRenderer(bg, fg));
    }

    public static JScrollPane wrap(JTable t) {
        // Use a viewport-aware scroll pane so the table always fills available width.
        // Horizontal scrollbar only appears when the panel is narrower than sum of min widths.
        JScrollPane s = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, super.getPreferredSize().height);
            }
        };
        s.setBorder(BorderFactory.createLineBorder(Theme.BORDER_LIGHT));
        s.getViewport().setBackground(Theme.BG_CARD);
        s.getVerticalScrollBar().setUnitIncrement(16);
        s.getHorizontalScrollBar().setUnitIncrement(16);

        // When scroll pane resizes, redistribute column widths proportionally
        s.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                redistributeColumns(t, s.getViewport().getWidth());
            }
        });
        return s;
    }

    /**
     * Redistributes column widths proportionally to fill the viewport. If viewport is smaller than sum of min widths —
     * keeps min widths and lets the horizontal scrollbar handle it.
     */
    public static void redistributeColumns(JTable t, int viewportWidth) {
        if (viewportWidth <= 0 || t.getColumnCount() == 0)
            return;

        int colCount = t.getColumnCount();
        int totalMin = 0;
        for (int i = 0; i < colCount; i++)
            totalMin += t.getColumnModel().getColumn(i).getMinWidth();

        if (totalMin <= 0)
            return;

        if (viewportWidth <= totalMin) {
            // Not enough space — each column gets its min width, scrollbar appears
            for (int i = 0; i < colCount; i++) {
                TableColumn col = t.getColumnModel().getColumn(i);
                col.setPreferredWidth(col.getMinWidth());
            }
        } else {
            // Distribute extra space proportionally based on min width ratios
            int extra = viewportWidth - totalMin;
            int assigned = 0;
            for (int i = 0; i < colCount; i++) {
                TableColumn col = t.getColumnModel().getColumn(i);
                int min = col.getMinWidth();
                int share = (int) ((double) min / totalMin * extra);
                if (i == colCount - 1)
                    share = viewportWidth - assigned; // last col absorbs rounding
                col.setPreferredWidth(min + share);
                assigned += min + share;
            }
            t.revalidate();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPER — row background colour
    // ═══════════════════════════════════════════════════════════════════════

    static Color rowBg(JTable t, int row) {
        if (t.isRowSelected(row))
            return Theme.TABLE_ROW_SELECTED;
        return row % 2 == 0 ? Theme.TABLE_ROW_ODD : Theme.TABLE_ROW_EVEN;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RENDERERS
    // ═══════════════════════════════════════════════════════════════════════

    // ── Default ────────────────────────────────────────────────────────────
    public static class DefaultRenderer extends DefaultTableCellRenderer {
        public DefaultRenderer() {
            this(SwingConstants.LEFT);
        }

        public DefaultRenderer(int align) {
            setHorizontalAlignment(align);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setFont(Theme.FONT_BODY);
            setForeground(Theme.TEXT_DARK);
            setBackground(rowBg(t, row));
            setBorder(BorderFactory.createEmptyBorder(Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H,
                    Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H));
            return this;
        }
    }

    // ── Bold ───────────────────────────────────────────────────────────────
    public static class BoldRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setFont(Theme.FONT_BODY_BOLD);
            setForeground(Theme.TEXT_DARK);
            setBackground(rowBg(t, row));
            setBorder(BorderFactory.createEmptyBorder(Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H,
                    Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H));
            return this;
        }
    }

    // ── Time chip ───────────────────────────────────────────────────────────
    public static class TimeChipRenderer implements TableCellRenderer {

        private final Map<String, Color> colourMap;

        public TimeChipRenderer(Map<String, Color> colourMap) {
            this.colourMap = colourMap;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {

            final String text = v == null ? "" : v.toString();
            final Color chipColor = colourMap.getOrDefault(text, Theme.ACCENT_MID);
            final Color bg = rowBg(t, row);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    if (text.isEmpty())
                        return;

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setFont(Theme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2.getFontMetrics();

                    int padX = Theme.TABLE_CELL_PAD_H;
                    int textW = fm.stringWidth(text);

                    int chipH = 20;
                    int chipW = textW + 18;

                    int chipX = padX;
                    int chipY = (getHeight() - chipH) / 2;

                    // shadow
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(chipX + 1, chipY + 2, chipW, chipH, chipH, chipH);

                    // chip background
                    g2.setColor(chipColor);
                    g2.fillRoundRect(chipX, chipY, chipW, chipH, chipH, chipH);

                    // text
                    g2.setColor(Color.WHITE);
                    int textY = chipY + (chipH + fm.getAscent() - fm.getDescent()) / 2 - 1;
                    g2.drawString(text, chipX + (chipW - textW) / 2, textY);

                    g2.dispose();
                }
            };
        }
    }

    // ── Price ──────────────────────────────────────────────────────────────
    public static class PriceRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {

            final String text = v == null ? "" : v.toString();
            final Color bg = rowBg(t, row);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setFont(Theme.FONT_MONO);
                    g2.setColor(Theme.ACCENT);

                    FontMetrics fm = g2.getFontMetrics();

                    int padX = Theme.TABLE_CELL_PAD_H;
                    int textWidth = fm.stringWidth(text);
                    int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                    // Ensure column never becomes smaller than text
                    TableColumn column = t.getColumnModel().getColumn(col);
                    int neededWidth = textWidth + (padX * 2) + 8;
                    if (column.getMinWidth() < neededWidth) {
                        column.setMinWidth(neededWidth);
                        column.setPreferredWidth(neededWidth);
                    }

                    g2.drawString(text, padX, textY);
                    g2.dispose();
                }
            };
        }
    }

    // ── Stars ──────────────────────────────────────────────────────────────
    public static class StarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            if (v instanceof Integer r) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 5; i++)
                    sb.append(i < r ? "★" : "☆");
                setText(sb.toString());
                setForeground(Theme.STAR_FILLED);
                setFont(new Font("SansSerif", Font.PLAIN, 15));
            }
            setBackground(rowBg(t, row));
            setBorder(BorderFactory.createEmptyBorder(Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H,
                    Theme.TABLE_CELL_PAD_V, Theme.TABLE_CELL_PAD_H));
            return this;
        }
    }

    // ── Week chip ──────────────────────────────────────────────────────────
    // "Week 3" → small grey chip, number slightly larger
    public static class WeekRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            final String text = v == null ? "" : v.toString();
            final Color bg = rowBg(t, row);
            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setFont(Theme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(text);
                    int cw = tw + CHIP_PAD * 2;
                    int cx = Theme.TABLE_CELL_PAD_H;
                    int cy = (getHeight() - CHIP_H) / 2;

                    g2.setColor(Theme.BG_ALT);
                    g2.fillRoundRect(cx, cy, cw, CHIP_H, CHIP_H, CHIP_H);

                    g2.setColor(Theme.TEXT_MID);
                    g2.drawString(text, cx + CHIP_PAD, cy + (CHIP_H + fm.getAscent() - fm.getDescent()) / 2 - 1);
                    g2.dispose();
                }
            };
        }
    }

    // ── Pill badge ─────────────────────────────────────────────────────────
    public static class PillRenderer implements TableCellRenderer {
        private final Map<String, Color> bgMap;
        private final Map<String, Color> fgMap;

        public PillRenderer(Map<String, Color> bgMap, Map<String, Color> fgMap) {
            this.bgMap = bgMap;
            this.fgMap = fgMap;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            final String text = v == null ? "" : v.toString();
            final Color pillBg = bgMap.getOrDefault(text, Theme.STATUS_BG_GREEN);
            final Color pillFg = fgMap.getOrDefault(text, Theme.STATUS_AVAILABLE);
            final Color bg = rowBg(t, row);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (text.isEmpty())
                        return;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setFont(Theme.FONT_SMALL_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(text);
                    int pw = tw + PILL_PAD * 2;
                    int px = (getWidth() - pw) / 2;
                    int py = (getHeight() - PILL_H) / 2;

                    // Shadow
                    g2.setColor(new Color(0, 0, 0, 12));
                    g2.fillRoundRect(px + 1, py + 2, pw, PILL_H, PILL_H, PILL_H);

                    // Pill
                    g2.setColor(pillBg);
                    g2.fillRoundRect(px, py, pw, PILL_H, PILL_H, PILL_H);

                    // Text
                    g2.setColor(pillFg);
                    g2.drawString(text, px + (pw - tw) / 2, py + (PILL_H + fm.getAscent() - fm.getDescent()) / 2 - 1);
                    g2.dispose();
                }
            };
        }
    }

    // ── Code tag renderer (for IDs like R001, M002) ─────────────────────────
    public static class CodeTagRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {

            final String text = v == null ? "" : v.toString();
            final Color bg = rowBg(t, row);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    if (text.isEmpty())
                        return;

                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setFont(Theme.FONT_MONO); // monospaced looks better for codes
                    FontMetrics fm = g2.getFontMetrics();

                    int padX = Theme.TABLE_CELL_PAD_H;
                    int textW = fm.stringWidth(text);

                    int chipH = 20;
                    int chipW = textW + 16;

                    int chipX = padX;
                    int chipY = (getHeight() - chipH) / 2;

                    // subtle background chip
                    g2.setColor(Theme.BG_ALT);
                    g2.fillRoundRect(chipX, chipY, chipW, chipH, 8, 8);

                    // border
                    g2.setColor(Theme.BORDER_LIGHT);
                    g2.drawRoundRect(chipX, chipY, chipW, chipH, 8, 8);

                    // text
                    g2.setColor(Theme.TEXT_MID);
                    int textY = chipY + (chipH + fm.getAscent() - fm.getDescent()) / 2 - 1;
                    g2.drawString(text, chipX + (chipW - textW) / 2, textY);

                    g2.dispose();
                }
            };
        }
    }

    // ── Coloured dot + bold text ───────────────────────────────────────────
    public static class DotRenderer implements TableCellRenderer {
        private final Map<String, Color> colourMap;

        public DotRenderer(Map<String, Color> colourMap) {
            this.colourMap = colourMap;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            final String text = v == null ? "" : v.toString();
            final Color dot = colourMap.getOrDefault(text, Theme.ACCENT_MID);
            final Color bg = rowBg(t, row);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    int padX = Theme.TABLE_CELL_PAD_H;
                    int dotY = (getHeight() - DOT_D) / 2;

                    // Glow ring
                    g2.setColor(new Color(dot.getRed(), dot.getGreen(), dot.getBlue(), 35));
                    g2.fillOval(padX - 2, dotY - 2, DOT_D + 4, DOT_D + 4);

                    // Dot
                    g2.setColor(dot);
                    g2.fillOval(padX, dotY, DOT_D, DOT_D);

                    // Text
                    g2.setFont(Theme.FONT_BODY_BOLD);
                    g2.setColor(Theme.TEXT_DARK);
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, padX + DOT_D + 8, textY);
                    g2.dispose();
                }
            };
        }
    }

    // ── Capacity bar ───────────────────────────────────────────────────────
    // expects "N / M" string
    public static class CapacityRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
            final String text = v == null ? "" : v.toString();
            final Color bg = rowBg(t, row);

            int en = 0, tot = 4;
            try {
                String[] p = text.replace(" ", "").split("/");
                en = Integer.parseInt(p[0]);
                tot = Integer.parseInt(p[1]);
            } catch (Exception ignored) {
            }
            final int enrolled = en, total = tot;

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(bg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    int pad = Theme.TABLE_CELL_PAD_H;
                    int barH = 5;
                    int barW = getWidth() - pad * 2;
                    int barX = pad;
                    int midY = getHeight() / 2;
                    int barY = midY + 4;

                    double ratio = total > 0 ? (double) enrolled / total : 0;
                    Color fillCol = ratio >= 1.0 ? Theme.STATUS_FULL
                            : ratio >= 0.75 ? Theme.STATUS_ALMOST_FULL : Theme.STATUS_AVAILABLE;

                    // Label
                    g2.setFont(Theme.FONT_SMALL_BOLD);
                    g2.setColor(fillCol);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(text, barX, barY - 2);

                    // Track
                    g2.setColor(Theme.BORDER_LIGHT);
                    g2.fillRoundRect(barX, barY + fm.getHeight() - 4, barW, barH, barH, barH);

                    // Fill
                    int fillW = (int) (barW * ratio);
                    if (fillW > 0) {
                        g2.setColor(fillCol);
                        g2.fillRoundRect(barX, barY + fm.getHeight() - 4, fillW, barH, barH, barH);
                    }

                    g2.dispose();
                }
            };
        }
    }
}