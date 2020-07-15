package bsa.java.concurrency.fs;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FileSystem {
    CompletableFuture<String> saveImage(MultipartFile file);
    void deleteAllFiles();
    void deleteFileById(UUID id);
}
