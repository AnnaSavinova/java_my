package ru.fizteh.fivt.students.annasavinova.filemap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.annasavinova.filemap.shell.UserShell;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class FileMap extends UserShell {
    @Value("${ru.fizteh.fivt.students.annasavinova.filemap.path:BBBB}")
    private String path;

    DBaseProviderFactory factory;
    private DataBaseProvider prov;
    private DataBase currTable;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FileMap.class);

    public FileMap() {
        String root;
        if (path == null) {
            path = System.getProperty("fizteh.db.dir");
            log.error("root dir not selected");
            //TODO throw new RuntimeException("root dir not selected");
        }
        File r = new File(path);
        if (!r.exists()) {
            if (!r.mkdirs()) {
                log.error("cannot create root dir " + path);
                throw new RuntimeException("cannot create root dir " + path);
            }
            log.info("Root directory created");

        }
        if (path.endsWith(File.separator)) {
            root = path;
        } else {
            root = path + File.separatorChar;
        }
        factory = new DBaseProviderFactory();
        try {
            prov = (DataBaseProvider) factory.create(root);
        } catch (IllegalArgumentException | IOException e) {
            System.err.println(e.getMessage());
            log.error(e.getMessage());
            System.exit(1);
        }
    }

    public String appendArgs(int num, String[] args) {
        StringBuilder str;
        str = new StringBuilder(args[num]);
        for (int i = num + 1; i < args.length; ++i) {
            str.append(" ");
            str.append(args[i]);
        }
        return str.toString();
    }

    @Override
    public void printError(String errStr) {
        if (isPacket) {
            try {
                currTable.unloadData();
            } finally {
                System.err.println(errStr);
                log.info("data unloaded");
                log.error(errStr);
                System.exit(1);
            }
        } else {
            System.out.println(errStr);
            log.error(errStr);
        }
    }

    @Override
    public String[] getArgsFromString(String str) {
        str = str.trim();
        if (str.isEmpty()) {
            return null;
        } else {
            return str.split("[\\s]+", 3);
        }
    }

    @Override
    protected void execProc(String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
            case "create":
                if (args.length > 1) {
                    doCreateTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "drop":
                if (args.length > 1) {
                    doDropTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "use":
                if (args.length > 1) {
                    doUseTable(appendArgs(1, args));
                } else {
                    printError("Incorrect number of args");
                }
                break;
            case "put":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (args.length > 2) {
                        doPut(args[1], appendArgs(2, args));
                    } else {
                        printError("Incorrect number of args");
                    }
                }
                break;
            case "get":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (checkArgs(2, args)) {
                        doGet(args[1]);
                    }
                }
                break;
            case "remove":
                if (currTable == null) {
                    System.out.println("no table");
                } else {
                    if (checkArgs(2, args)) {
                        doRemove(args[1]);
                    }
                }
                break;
            case "size":
                System.out.println(currTable.size());
                break;
            case "commit":
                if (checkArgs(1, args)) {
                    doCommit();
                }
                break;
            case "rollback":
                if (checkArgs(1, args)) {
                    doRollBack();
                }
                break;
            case "exit":
                if (checkArgs(1, args)) {
                    doExit();
                }
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    public void doExit() {
        log.info("Exit");
        System.exit(0);
    }

    private void doCommit() {
        try {
            System.out.println(currTable.commit());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        log.info("Commit");
    }

    private void doRollBack() {
        System.out.println(currTable.rollback());
        log.info("RollBack");
    }

    private void doCreateTable(String args) {
        args = args.trim();
        if (!args.endsWith(")")) {
            printError("Incorrect arguments. Need types");
            return;
        }
        args = args.replace(")", "");
        String[] strArray = args.split("[ ]+[/(]", 2);
        if (strArray.length < 2) {
            printError("Incorrect arguments. Need types");
            return;
        }
        String tableName = strArray[0];
        ArrayList<Class<?>> list = new ArrayList<>();
        Scanner sc = new Scanner(strArray[1]);
        while (sc.hasNext()) {
            try {
                String type = sc.next();
                Class<?> cl = prov.getClassFromString(type);
                list.add(cl);
            } catch (RuntimeException e) {
                printError("wrong type (" + e.getMessage() + ")");
                sc.close();
                return;
            }
        }
        sc.close();
        try {
            if (prov.createTable(tableName, list) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
                log.info("Table " + tableName + " created");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void doUseTable(String tableName) {
        if (currTable != null) {
            int changes = currTable.countChanges();
            if (changes != 0) {
                System.out.println(changes + " unsaved changes");
                return;
            }
        }
        try {
            DataBase tmp = (DataBase) prov.getTable(tableName);
            if (tmp == null) {
                System.out.println(tableName + " not exists");
            } else {
                if (currTable != null) {
                    currTable.unloadData();
                }
                currTable = tmp;
                System.out.println("using " + tableName);
                log.info("Use " + tableName);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void doDropTable(String tableName) {
        if (currTable == prov.getTable(tableName)) {
            currTable = null;
        }
        try {
            prov.removeTable(tableName);
            System.out.println("dropped");
            log.info(tableName + " dropped");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        }
    }

    private void doPut(String key, String value) {
        try {
            Storeable oldValue = currTable.put(key, prov.deserialize(currTable, value));
            if (oldValue == null) {
                System.out.println("new");
                log.info("Put new " + key + " " + value + " into " + currTable.getName());
            } else {
                System.out.println("overwrite");
                System.out.println(prov.serialize(currTable, oldValue));
                log.info("Owerwrite " + key + " " + value + " into " + currTable.getName());
            }
        } catch (ParseException | RuntimeException e) {
            printError("Cannot parse arguments");
        }
    }

    private void doGet(String key) {
        try {
            Storeable value = currTable.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(prov.serialize(currTable, value));
                log.info("Get " + key + " from " + currTable.getName());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void doRemove(String key) {
        try {
            Storeable value = currTable.remove(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
                log.info("Removed " + key + " from " + currTable.getName());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }


}
