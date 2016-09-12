package com.akulogics.gallery.service;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class WatchDirService {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final BiConsumer<Path, WatchEvent<Path>> callBackFn;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public WatchDirService(Path dir, BiConsumer<Path, WatchEvent<Path>> callBackFn) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.callBackFn = callBackFn;

        registerAll(dir);
    }

    public void processEvents() {

        Thread watchDirThread = new Thread(() -> {
            for (;;) {

                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!");
                    continue;
                }

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    if (kind == OVERFLOW) {
                        System.out.println("Unable to process file changes due to event overflow!");
                        continue;
                    }

                    WatchEvent<Path> ev = cast(event);
                    callBackFn.accept(dir, ev);

                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (kind == ENTRY_DELETE && name.equals(child)) {
                        keys.remove(key);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        });

        watchDirThread.setDaemon(true);
        watchDirThread.start();
    }
}