package com.flc.util;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.*;

class ModernTableTest {

    @Test
    void testExerciseColoursNotNull() {
        assertNotNull(ModernTable.EXERCISE_COLOURS);
        assertFalse(ModernTable.EXERCISE_COLOURS.isEmpty());
        assertTrue(ModernTable.EXERCISE_COLOURS.containsKey("Yoga"));
    }

    @Test
    void testDayColoursNotNull() {
        assertNotNull(ModernTable.DAY_COLOURS);
        assertFalse(ModernTable.DAY_COLOURS.isEmpty());
        assertTrue(ModernTable.DAY_COLOURS.containsKey("Saturday"));
    }

    @Test
    void testTimeColoursNotNull() {
        assertNotNull(ModernTable.TIME_COLOURS);
        assertFalse(ModernTable.TIME_COLOURS.isEmpty());
        assertTrue(ModernTable.TIME_COLOURS.containsKey("Morning"));
    }

    @Test
    void testCreateTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1", "Col2"}, 0);
        JTable table = ModernTable.create(model);
        assertNotNull(table);
        assertEquals(model, table.getModel());
        assertEquals(2, table.getColumnCount());
    }

    @Test
    void testSetColumnWidths() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1", "Col2"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.setColumnWidths(table, 100, 200);
        assertEquals(100, table.getColumnModel().getColumn(0).getPreferredWidth());
        assertEquals(200, table.getColumnModel().getColumn(1).getPreferredWidth());
    }

    @Test
    void testHideColumn() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1", "Col2"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.hideColumn(table, 1);
        assertEquals(0, table.getColumnModel().getColumn(1).getWidth());
    }

    @Test
    void testSetBoldColumn() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.setBoldColumn(table, 0);
        // Hard to test renderer without GUI, just check no exception
        assertNotNull(table.getColumnModel().getColumn(0).getCellRenderer());
    }

    @Test
    void testSetRightAligned() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.setRightAligned(table, 0);
        assertNotNull(table.getColumnModel().getColumn(0).getCellRenderer());
    }

    @Test
    void testSetCentreAligned() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.setCentreAligned(table, 0);
        assertNotNull(table.getColumnModel().getColumn(0).getCellRenderer());
    }

    @Test
    void testSetPriceColumn() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Col1"}, 0);
        JTable table = ModernTable.create(model);
        ModernTable.setPriceColumn(table, 0);
        assertNotNull(table.getColumnModel().getColumn(0).getCellRenderer());
    }
}