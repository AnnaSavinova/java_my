package ru.fizteh.fivt.students.annasavinova.filemap.shell;

import java.util.Scanner;

public abstract class UserShell {
    public boolean isPacket = false;

    public void printError(String errStr) {
        if (isPacket) {
            System.err.println(errStr);
            System.exit(1);
        } else {
            System.out.println(errStr);
        }
    }

    public boolean checkArgs(int num, String[] args) {
        if (args.length != num) {
            printError("Incorrect number of args");
            return false;
        }
        return true;
    }

    public String[] getArgsFromString(String str) {
        str = str.trim();
        str = str.replaceAll("[ ]+", " ");
        int countArgs = 1;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ' ') {
                ++countArgs;
            }
        }
        if (!str.isEmpty()) {
            Scanner stringScanner;
            stringScanner = new Scanner(str);
            stringScanner.useDelimiter(" ");
            String[] cmdArgs = new String[countArgs];
            for (int i = 0; stringScanner.hasNext(); ++i) {
                cmdArgs[i] = stringScanner.next();
            }
            stringScanner.close();
            return cmdArgs;
        }
        return null;
    }

    protected abstract void execProc(String[] args);

    private void execString(String str) {
        Scanner scanner;
        scanner = new Scanner(str);
        scanner.useDelimiter("[ ]*;[ ]*");
        while (scanner.hasNext()) {
            execProc(getArgsFromString(scanner.next()));
        }
        scanner.close();
    }

    private void doPacket(String[] args) {
        isPacket = true;
        StringBuffer argStr;
        argStr = new StringBuffer(args[0]);
        for (int i = 1; i < args.length; ++i) {
            argStr.append(" ");
            argStr.append(args[i]);
        }
        execString(argStr.toString());
    }

    private void doInteractive() {
        isPacket = false;
        System.out.print("$ ");
        Scanner mainScanner;
        mainScanner = new Scanner(System.in);
        while (mainScanner.hasNextLine()) {
            String str;
            str = mainScanner.nextLine().trim();
            if (!str.isEmpty()) {
                execString(str);
            }
            System.out.print("$ ");
        }
        mainScanner.close();
    }

    public void exec(String[] args) {
        if (args.length != 0) {
            doPacket(args);
        } else {
            doInteractive();
        }
    }

}
