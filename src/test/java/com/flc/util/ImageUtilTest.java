package com.flc.util;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilTest {

    @Test
    void testLoadExistingAsset() {
        ImageIcon icon = ImageUtil.load("assets/logo.png", 32, 32);
        assertNotNull(icon, "Should load existing asset");
        assertEquals(32, icon.getIconWidth());
        assertEquals(32, icon.getIconHeight());
    }

    @Test
    void testLoadNonExistingAsset() {
        ImageIcon icon = ImageUtil.load("assets/nonexistent.png", 32, 32);
        assertNull(icon, "Should return null for non-existing asset");
    }

    @Test
    void testTint() {
        // Create a dummy icon
        ImageIcon source = new ImageIcon(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB));
        ImageIcon tinted = ImageUtil.tint(source, Color.RED);
        assertNotNull(tinted, "Should return tinted icon");
        assertEquals(10, tinted.getIconWidth());
        assertEquals(10, tinted.getIconHeight());
    }

    @Test
    void testTintNullSource() {
        ImageIcon tinted = ImageUtil.tint(null, Color.RED);
        assertNull(tinted, "Should return null for null source");
    }

    @Test
    void testLoadTinted() {
        ImageIcon icon = ImageUtil.loadTinted("assets/logo.png", 32, 32, Color.BLUE);
        assertNotNull(icon, "Should load and tint existing asset");
    }

    @Test
    void testLoadTintedNonExisting() {
        ImageIcon icon = ImageUtil.loadTinted("assets/nonexistent.png", 32, 32, Color.BLUE);
        assertNull(icon, "Should return null for non-existing asset");
    }

    @Test
    void testIconLabel() {
        JLabel label = ImageUtil.iconLabel("assets/logo.png", 32, 32);
        assertNotNull(label);
        assertNotNull(label.getIcon());
    }

    @Test
    void testTintedLabel() {
        JLabel label = ImageUtil.tintedLabel("assets/logo.png", 32, 32, Color.GREEN);
        assertNotNull(label);
        assertNotNull(label.getIcon());
    }
}