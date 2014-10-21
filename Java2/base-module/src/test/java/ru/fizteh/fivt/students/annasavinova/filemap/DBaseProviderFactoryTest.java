package ru.fizteh.fivt.students.annasavinova.filemap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class DBaseProviderFactoryTest {
    DBaseProviderFactory test;

    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        System.setProperty("fizteh.db.dir", root.newFolder().getAbsolutePath());
        test = new DBaseProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() throws IOException {
        test.create(null);
    }

    @Test
    public void testCreateNotExisting() throws IOException {
        test.create(root.newFolder().getAbsolutePath() + "not_existing_name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFile() throws IOException {
        test.create(root.newFile().getAbsolutePath());
    }

    @Test
    public void testCreate() throws IOException {
        assertNotNull(test.create(root.newFolder().getAbsolutePath()));

    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose1() throws Exception {
        DBaseProviderFactory tmp = new DBaseProviderFactory();
        tmp.close();
        tmp.create(root.newFolder().toString());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testClose2() throws Exception {
        DBaseProviderFactory tmp = new DBaseProviderFactory();
        DataBaseProvider prov = (DataBaseProvider) tmp.create(root.newFolder().toString());
        tmp.close();
        prov.createTable("aa", null);
    }
}
