package com.flc.config;

import java.awt.*;

/**
 * Central theme configuration for Furzefield Leisure Centre. Every colour, font, size, spacing, shadow, and border
 * lives here. Never hardcode a visual value anywhere else — always reference this class.
 */
public final class Theme {

    private Theme() {
    } // utility class — no instances

    // ═══════════════════════════════════════════════════════════════════════
    // COLOURS
    // ═══════════════════════════════════════════════════════════════════════

    // ── Backgrounds ──────────────────────────────────────────────────────────
    public static final Color BG = new Color(0xF5F5F3); // main window bg
    public static final Color BG_ALT = new Color(0xF0F0EC); // status bar, alternating rows
    public static final Color BG_CARD = new Color(0xFFFFFF); // card / panel bg
    public static final Color BG_INPUT = new Color(0xFFFFFF); // text field bg
    public static final Color BG_INPUT_FOCUS = new Color(0xF5FBF7); // text field focused bg
    public static final Color BG_HOVER = new Color(0xF0F7F3); // list row hover
    public static final Color BG_SELECTED = new Color(0xDCEFE6); // list row selected
    public static final Color BG_DISABLED = new Color(0xF4F4F2); // disabled field bg
    public static final Color BG_OVERLAY = new Color(0, 0, 0, 90); // modal overlay
    public static final Color BG_TOOLTIP = new Color(0x1A1A18); // tooltip background

    // ── Brand / Accent ────────────────────────────────────────────────────────
    public static final Color ACCENT = new Color(0x2D6A4F); // primary green
    public static final Color ACCENT_MID = new Color(0x52B788); // mid green
    public static final Color ACCENT_LIGHT = new Color(0xE8F5EE); // pale green (pill bg)
    public static final Color ACCENT_DARK = new Color(0x1B4332); // darkest green
    public static final Color ACCENT_XLIGHT = new Color(0xF0FAF4); // very pale tint

    // ── Text ──────────────────────────────────────────────────────────────────
    public static final Color TEXT_DARK = new Color(0x1A1A18); // headings
    public static final Color TEXT_MID = new Color(0x6B6B65); // secondary text
    public static final Color TEXT_LIGHT = new Color(0xB0B0A8); // hints, footer
    public static final Color TEXT_DISABLED = new Color(0xC8C8C0); // disabled labels
    public static final Color TEXT_ON_ACCENT = Color.WHITE; // text on green
    public static final Color TEXT_LINK = new Color(0x2D6A4F); // clickable links
    public static final Color TEXT_TOOLTIP = new Color(0xF0F0EC); // tooltip text
    public static final Color TEXT_ERROR = new Color(0xC0392B); // validation errors
    public static final Color TEXT_SUCCESS = new Color(0x27AE60); // success messages
    public static final Color TEXT_WARNING = new Color(0xE67E22); // warnings

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static final Color BTN_PRIMARY = new Color(0x2D6A4F);
    public static final Color BTN_HOVER = new Color(0x1B4332);
    public static final Color BTN_PRESSED = new Color(0x142E24);
    public static final Color BTN_DISABLED = new Color(0xB0C4BA);
    public static final Color BTN_TEXT = Color.WHITE;
    public static final Color BTN_TEXT_DISABLED = new Color(0xE8EEE9);

    public static final Color BTN_CHANGE = new Color(0x6C63FF);
    public static final Color BTN_CHANGE_HOVER = new Color(0x5A52D5);

    public static final Color BTN_SECONDARY = new Color(0xFFFFFF); // outlined bg
    public static final Color BTN_SEC_BORDER = new Color(0x2D6A4F); // outlined border
    public static final Color BTN_SEC_TEXT = new Color(0x2D6A4F); // outlined text
    public static final Color BTN_SEC_HOVER = new Color(0xE8F5EE); // outlined hover

    public static final Color BTN_DANGER = new Color(0xC0392B); // delete button
    public static final Color BTN_DANGER_HOVER = new Color(0x922B21);
    public static final Color BTN_DANGER_TEXT = Color.WHITE;

    // ── Status / Availability ─────────────────────────────────────────────────
    public static final Color STATUS_AVAILABLE = new Color(0x27AE60); // spaces free
    public static final Color STATUS_FULL = new Color(0xC0392B); // lesson full
    public static final Color STATUS_ALMOST_FULL = new Color(0xE67E22); // 1 space left
    public static final Color STATUS_BG_GREEN = new Color(0xE8F5EE);
    public static final Color STATUS_BG_RED = new Color(0xFDEDEC);
    public static final Color STATUS_BG_ORANGE = new Color(0xFEF5E7);

    // ── Star ratings ──────────────────────────────────────────────────────────
    public static final Color STAR_FILLED = new Color(0xF4C430); // gold
    public static final Color STAR_EMPTY = new Color(0xDDDDD5); // empty

    // ── Borders & Dividers ────────────────────────────────────────────────────
    public static final Color BORDER = new Color(0xDDDDD5);
    public static final Color BORDER_FOCUS = new Color(0x52B788);
    public static final Color BORDER_ERROR = new Color(0xC0392B);
    public static final Color BORDER_LIGHT = new Color(0xEEEEE8);
    public static final Color DIVIDER = new Color(0xE8E8E2);

    // ── Decorative ────────────────────────────────────────────────────────────
    public static final Color DOT_GRID = new Color(0xCCCCC4);
    public static final Color SHADOW = new Color(0, 0, 0, 18);

    // ── Table ─────────────────────────────────────────────────────────────────
    public static final Color TABLE_HEADER_BG = new Color(0xF0F7F3);
    public static final Color TABLE_ROW_ODD = new Color(0xFFFFFF);
    public static final Color TABLE_ROW_EVEN = new Color(0xF9FAF9);
    public static final Color TABLE_ROW_HOVER = new Color(0xEBF5EF);
    public static final Color TABLE_ROW_SELECTED = new Color(0xDCEFE6);
    public static final Color TABLE_GRID = new Color(0xEEEEE8);

    // ═══════════════════════════════════════════════════════════════════════
    // FONTS
    // ═══════════════════════════════════════════════════════════════════════

    // ── Display / Hero ────────────────────────────────────────────────────────
    public static final Font FONT_HEADLINE = new Font("SansSerif", Font.BOLD, 52);
    public static final Font FONT_HERO_SUB = new Font("Georgia", Font.ITALIC, 20);

    // ── Page titles ───────────────────────────────────────────────────────────
    public static final Font FONT_TITLE_LG = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_TITLE_MD = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_TITLE_SM = new Font("SansSerif", Font.BOLD, 18);

    // ── Body ──────────────────────────────────────────────────────────────────
    public static final Font FONT_SUBTITLE = new Font("Georgia", Font.ITALIC, 16);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY_ITALIC = new Font("SansSerif", Font.ITALIC, 14);

    // ── UI elements ───────────────────────────────────────────────────────────
    public static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BTN_SM = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_PLACEHOLDER = new Font("SansSerif", Font.ITALIC, 14);
    public static final Font FONT_TABLE_HEADER = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_TABLE_CELL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_TAB = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_MENU = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_MENU_BOLD = new Font("SansSerif", Font.BOLD, 14);

    // ── Small / Utility ───────────────────────────────────────────────────────
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_TINY = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_TINY_BOLD = new Font("SansSerif", Font.BOLD, 11);
    public static final Font FONT_PILL = new Font("SansSerif", Font.BOLD, 10);
    public static final Font FONT_BADGE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_TOOLTIP = new Font("SansSerif", Font.PLAIN, 12);

    // ── Numbers / Data ────────────────────────────────────────────────────────
    public static final Font FONT_STAT_LG = new Font("SansSerif", Font.BOLD, 36);
    public static final Font FONT_STAT_MD = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 13);

    // ═══════════════════════════════════════════════════════════════════════
    // SPACING (multiples of 4 — keeps everything on a grid)
    // ═══════════════════════════════════════════════════════════════════════

    public static final int SPACE_XXS = 4;
    public static final int SPACE_XS = 8;
    public static final int SPACE_SM = 16; // was 12
    public static final int SPACE_MD = 24; // was 16
    public static final int SPACE_LG = 36; // was 24
    public static final int SPACE_XL = 48; // was 32
    public static final int SPACE_XXL = 64; // was 48
    public static final int SPACE_XXXL = 88; // was 64

    // Legacy aliases
    public static final int PADDING_XS = SPACE_XS;
    public static final int PADDING_SM = SPACE_MD;
    public static final int PADDING_MD = SPACE_XL;
    public static final int PADDING_LG = SPACE_XXXL;

    // ── Insets ────────────────────────────────────────────────────────────────
    public static final Insets INSETS_CARD = new Insets(20, 24, 20, 24);
    public static final Insets INSETS_SECTION = new Insets(24, 32, 24, 32);
    public static final Insets INSETS_BTN_LG = new Insets(14, 28, 14, 28);
    public static final Insets INSETS_BTN_MD = new Insets(10, 20, 10, 20);
    public static final Insets INSETS_BTN_SM = new Insets(6, 14, 6, 14);
    public static final Insets INSETS_INPUT = new Insets(10, 14, 10, 14);
    public static final Insets INSETS_TABLE = new Insets(10, 16, 10, 16);

    // ═══════════════════════════════════════════════════════════════════════
    // BORDER RADII
    // ═══════════════════════════════════════════════════════════════════════

    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 10;
    public static final int RADIUS_BTN = 14;
    public static final int RADIUS_CARD = 16;
    public static final int RADIUS_LG = 20;
    public static final int RADIUS_PILL = 999;

    // ═══════════════════════════════════════════════════════════════════════
    // STROKES
    // ═══════════════════════════════════════════════════════════════════════

    public static final BasicStroke STROKE_THIN = new BasicStroke(1f);
    public static final BasicStroke STROKE_MED = new BasicStroke(1.5f);
    public static final BasicStroke STROKE_THICK = new BasicStroke(2f);
    public static final BasicStroke STROKE_BOLD = new BasicStroke(3f);

    // ═══════════════════════════════════════════════════════════════════════
    // DIMENSIONS
    // ═══════════════════════════════════════════════════════════════════════

    // ── Window ────────────────────────────────────────────────────────────────
    public static final Dimension WINDOW_SIZE = new Dimension(1280, 800);
    public static final Dimension WINDOW_MIN = new Dimension(1024, 680);

    // ── Layout ────────────────────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH = 240;
    public static final int STRIP_WIDTH = 8;
    public static final int TOPBAR_HEIGHT = 68;
    public static final int STATUSBAR_HEIGHT = 36;

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static final Dimension BTN_PRIMARY_SIZE = new Dimension(220, 52);
    public static final Dimension BTN_MD_SIZE = new Dimension(160, 42);
    public static final Dimension BTN_SM_SIZE = new Dimension(110, 34);
    public static final Dimension BTN_ICON_SIZE = new Dimension(36, 36);

    // ── Form ──────────────────────────────────────────────────────────────────
    public static final int INPUT_H = 46;
    public static final int LABEL_WIDTH = 140;

    // ── Cards ─────────────────────────────────────────────────────────────────
    public static final int CARD_MIN_WIDTH = 240;
    public static final int CARD_MIN_HEIGHT = 120;

    // ── Table ─────────────────────────────────────────────────────────────────
    public static final int TABLE_ROW_HEIGHT = 52;
    public static final int TABLE_HEADER_HEIGHT = 52;
    public static final int TABLE_CELL_PAD_H = 20; // horizontal cell padding
    public static final int TABLE_CELL_PAD_V = 12; // vertical cell padding

    // ── Logo / Avatar ─────────────────────────────────────────────────────────
    public static final int LOGO_SIZE = 64;
    public static final int AVATAR_LG = 48;
    public static final int AVATAR_SM = 32;

    // ── Icons ─────────────────────────────────────────────────────────────────
    public static final int ICON_LG = 24;
    public static final int ICON_MD = 18;
    public static final int ICON_SM = 14;

    // ── Stars ─────────────────────────────────────────────────────────────────
    public static final int STAR_SIZE_LG = 22;
    public static final int STAR_SIZE_SM = 14;

    // ═══════════════════════════════════════════════════════════════════════
    // ANIMATION (milliseconds)
    // ═══════════════════════════════════════════════════════════════════════

    public static final int ANIM_FAST = 120;
    public static final int ANIM_NORMAL = 220;
    public static final int ANIM_SLOW = 380;

    // ═══════════════════════════════════════════════════════════════════════
    // OPACITY (alpha 0–255)
    // ═══════════════════════════════════════════════════════════════════════

    public static final int OPACITY_OVERLAY = 90;
    public static final int OPACITY_SHADOW = 18;
    public static final int OPACITY_DECO_CIRCLE = 28;
    public static final int OPACITY_STRIP_TEXT = 60;

    // ═══════════════════════════════════════════════════════════════════════
    // Dashboad Icon
    // ═══════════════════════════════════════════════════════════════════════

    public static final Color BOOKING_ICON_COLOR = new Color(0x6C63FF);
    public static final Color REVIEWS_ICON_COLOR = new Color(0xE67E22);
    public static final Color REPORTS_ICON_COLOR = new Color(0x27AE60);
}