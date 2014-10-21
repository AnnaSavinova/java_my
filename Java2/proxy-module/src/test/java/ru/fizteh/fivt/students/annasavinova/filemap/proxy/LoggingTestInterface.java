package ru.fizteh.fivt.students.annasavinova.filemap.proxy;

import java.util.ArrayList;

public interface LoggingTestInterface {
    void takeIntReturnVoid(int i);

    int takeStringThrowException(String s) throws Exception;

    void takeNothingReturnVoidThrowException() throws Exception;

    ArrayList<?> takeIterableReturnArray(ArrayList<?> list);
    
    void takeNothingReturnNothing();
    
    int takeNothingReturnInt();

}
