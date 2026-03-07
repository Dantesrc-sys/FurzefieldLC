package com.flc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Shared utility for loading and manipulating image assets. All assets live in src/main/resources/assets/
 *
 * Usage: ImageIcon icon = ImageUtil.load("assets/logo.png", 36, 36); ImageIcon tinted = ImageUtil.tint(icon,
 * Theme.ACCENT); ImageIcon both = ImageUtil.loadTinted("assets/bookings.png", 20, 20, Theme.ACCENT);
 */
public final class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    private ImageUtil() {
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOAD
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Loads an image from the classpath and scales it to w x h. Returns null gracefully if the file is not found —
     * callers should handle this.
     */
    public static ImageIcon load(String path, int w, int h) {
        URL url = ImageUtil.class.getClassLoader().getResource(path);
        if (url == null) {
            logger.warn("Image not found: {}", path);
            return null;
        }
        Image scaled = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TINT
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Tints a transparent-background PNG icon to the given colour. Uses SrcAtop compositing — preserves alpha, replaces
     * colour. Works best with white or grey icons on transparent backgrounds.
     */
    public static ImageIcon tint(ImageIcon source, Color colour) {
        if (source == null)
            return null;
        int w = source.getIconWidth();
        int h = source.getIconHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(source.getImage(), 0, 0, null);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.setColor(colour);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return new ImageIcon(result);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOAD + TINT (convenience)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Loads, scales, and tints in one call. Returns null if the file is not found.
     */
    public static ImageIcon loadTinted(String path, int w, int h, Color colour) {
        ImageIcon icon = load(path, w, h);
        if (icon == null)
            return null;
        return tint(icon, colour);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LABEL HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns a JLabel containing the loaded icon, or an empty JLabel if not found.
     */
    public static JLabel iconLabel(String path, int w, int h) {
        ImageIcon icon = load(path, w, h);
        return icon != null ? new JLabel(icon) : new JLabel();
    }

    /**
     * Returns a JLabel containing the tinted icon, or an empty JLabel if not found.
     */
    public static JLabel tintedLabel(String path, int w, int h, Color colour) {
        ImageIcon icon = loadTinted(path, w, h, colour);
        return icon != null ? new JLabel(icon) : new JLabel();
    }
}