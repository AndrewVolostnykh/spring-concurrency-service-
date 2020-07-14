package bsa.java.concurrency.fs;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class FileSystemService implements FileSystem {

    private static final String path = "D:\\Developing\\git_reposes\\bsa-java-concurrency-template\\src\\main\\java\\bsa\\istore\\";

    @Override
    public CompletableFuture<String> saveFile(String path, byte[] file) {
        return null;
    }

    public static String getPath() {
        return path;
    }
}
