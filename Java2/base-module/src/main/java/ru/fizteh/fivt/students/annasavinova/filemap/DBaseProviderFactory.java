package ru.fizteh.fivt.students.annasavinova.filemap;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class DBaseProviderFactory implements TableProviderFactory, AutoCloseable {
    private HashSet<DataBaseProvider> providers = new HashSet<>();
    private volatile boolean isClosed = false;
    
    @Override
    public TableProvider create(String dir) throws IOException {
        if (isClosed) {
            throw new IllegalStateException("TableProviderFactory is closed");
        }
        
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("dir not selected");
        }

        File root = new File(dir);
        if (!root.exists()) {
            if (!root.mkdirs()) {
                throw new IOException("Directory cannot be created");
            }
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
        DataBaseProvider dataBase;
        dataBase = new DataBaseProvider(dir);
        providers.add(dataBase);
        return dataBase;
    }

    @Override
    public void close() throws Exception {
        for (DataBaseProvider prov : providers) {
            prov.close();
        }
        isClosed = true;
    }
}
