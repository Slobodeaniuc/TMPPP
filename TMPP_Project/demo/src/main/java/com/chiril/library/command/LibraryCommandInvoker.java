package com.chiril.library.command;

import java.util.ArrayList;
import java.util.List;

public final class LibraryCommandInvoker {
    private final List<LibraryCommand<?>> history = new ArrayList<>();

    public <R> R submit(LibraryCommand<R> command) {
        if (command == null) {
            throw new IllegalArgumentException("command null");
        }
        R result = command.execute();
        history.add(command);
        return result;
    }

    public List<LibraryCommand<?>> history() {
        return List.copyOf(history);
    }
}
