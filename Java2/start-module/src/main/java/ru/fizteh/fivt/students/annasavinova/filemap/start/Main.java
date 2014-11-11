package ru.fizteh.fivt.students.annasavinova.filemap.start;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.fizteh.fivt.students.annasavinova.filemap.FileMap;

/**
 * Created by anny on 20.10.14.
 */
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();
        try {
            FileMap data = context.getBean(FileMap.class);
            data.exec(args);
            data.doExit();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
