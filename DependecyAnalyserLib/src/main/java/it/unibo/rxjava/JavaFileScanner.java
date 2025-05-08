package it.unibo.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import java.io.File;
import java.util.Objects;

public class JavaFileScanner {
    public Observable<File> scan(File dir) {
        return Observable.create(emitter -> {
            scanDirectory(dir, emitter);
            emitter.onComplete();
        });
    }

    private void scanDirectory(File dir, ObservableEmitter<File> emitter) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, emitter);
            } else if (file.getName().endsWith(".java")) {
                emitter.onNext(file);
            }
        }
    }
}
