package ru.fizteh.fivt.students.annasavinova.filemap.proхy;

import java.io.Writer;
import java.lang.reflect.Proxy;

public class MyLoggingProxyFactory implements LoggingProxyFactory {

    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        if (writer == null) {
            throw new IllegalArgumentException("writer is null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("implementation is null");
        }
        if (interfaceClass == null) {
            throw new IllegalArgumentException("interfaceClass is null");
        }
        if (!interfaceClass.isAssignableFrom(implementation.getClass())) {
            throw new IllegalArgumentException(implementation + " doesn't implements " + interfaceClass);
        }
        return Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[] {interfaceClass},
                new MyProxyHandler(writer, implementation));
    }

}
