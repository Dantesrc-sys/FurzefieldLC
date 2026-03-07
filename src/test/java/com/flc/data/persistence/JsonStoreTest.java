package com.flc.data.persistence;

import com.flc.data.DataStore;
import com.flc.data.SampleData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class JsonStoreTest {

    @BeforeEach
    void setUp() {
        // Clear any existing data
        DataStore.getInstance().clearAll();
        // Delete the file if exists
        try {
            Files.deleteIfExists(Paths.get("flc-data.json"));
        } catch (IOException e) {
            // ignore
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up
        try {
            Files.deleteIfExists(Paths.get("flc-data.json"));
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    void testSaveFileExists() {
        assertFalse(JsonStore.saveFileExists());
        // Create empty file
        try {
            Files.createFile(Paths.get("flc-data.json"));
        } catch (IOException e) {
            fail("Could not create test file");
        }
        assertTrue(JsonStore.saveFileExists());
    }

    @Test
    void testLoadWhenNoFile() {
        assertFalse(JsonStore.load());
    }

    @Test
    void testSaveAndLoad() {
        // Load sample data
        SampleData.load();
        DataStore store = DataStore.getInstance();
        int initialMembers = store.getTotalMembers();
        int initialLessons = store.getTotalLessons();

        // Save
        JsonStore.save();
        assertTrue(JsonStore.saveFileExists());

        // Clear
        store.clearAll();
        assertEquals(0, store.getTotalMembers());
        assertEquals(0, store.getTotalLessons());

        // Load
        assertTrue(JsonStore.load());
        assertEquals(initialMembers, store.getTotalMembers());
        assertEquals(initialLessons, store.getTotalLessons());
    }
}