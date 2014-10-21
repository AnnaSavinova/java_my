package ru.fizteh.fivt.students.annasavinova.filemap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DataBaseProviderTest {
    DataBaseProvider test;
    ArrayList<Class<?>> list;
    DBaseProviderFactory fact;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void initialize() throws IOException {
        fact = new DBaseProviderFactory();
        test = (DataBaseProvider) fact.create(root.newFolder().toString());
        list = new ArrayList<>();
        list.add(int.class);
        list.add(String.class);
        test.createTable("testBase1", list);
    }

    @Test
    public void testCheckTableName() {
        assertFalse(test.checkTableName(".qwer"));
        assertFalse(test.checkTableName("..qwer"));
        assertFalse(test.checkTableName("/qwer"));
        assertFalse(test.checkTableName("/"));
        assertFalse(test.checkTableName("\\qw"));
        assertFalse(test.checkTableName(";qwer"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableNull() {
        test.getTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTableEmpty() {
        test.getTable("");
    }

    @Test
    public void testGetTable() {
        assertNull(test.getTable("not_existing_table"));
        assertNotNull(test.getTable("testBase1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableNull() throws IOException {
        test.createTable(null, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableEmpty() throws IOException {
        test.createTable("", list);
    }

    @Test
    public void testCreateTable() throws IOException {
        assertNotNull(test.createTable("tmpTable", list));
        assertNull(test.createTable("tmpTable", list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableNull() throws IOException {
        test.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableEmpty() throws IOException {
        test.removeTable("");
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableNotExists() throws IOException {
        test.removeTable("not_existing_table");
    }

    @Test
    public void testRemoveTable() throws IOException {
        test.createTable("table_for_removing", list);
        test.removeTable("table_for_removing");
        assertNull(test.getTable("table_for_removing"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose1() throws Exception {
        DBaseProviderFactory tmpFactory = new DBaseProviderFactory();
        DataBaseProvider tmp = (DataBaseProvider) tmpFactory.create(root.newFolder().toString());
        tmp.close();
        tmp.createTable("aa", null);
        tmpFactory.close();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose2() throws Exception {
        DBaseProviderFactory tmpFactory = new DBaseProviderFactory();
        DataBaseProvider tmp = (DataBaseProvider) tmpFactory.create(root.newFolder().toString());
        DataBase tmpTable = (DataBase) tmp.createTable("test", list);
        tmp.close();
        tmpTable.size();
        tmpFactory.close();
    }
}
