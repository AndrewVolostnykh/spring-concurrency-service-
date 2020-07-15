package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.fs.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private ImageRepository imageRepository;
    private DHasher hasher;
    private FileSystem fileSystem;

    @Autowired
    public ImageService(ImageRepository imageRepository, DHasher hasher, FileSystem fileSystem) {
        this.imageRepository = imageRepository;
        this.hasher = hasher;
        this.fileSystem = fileSystem;
    }

    public CompletableFuture<Void> saveImages(MultipartFile[] files) {
        var promises = Arrays.stream(files)
                .parallel()
                .map(file -> {
                    var future = fileSystem.saveImage(file);
                    var hash = hasher.calculateHash(FileSystemService.getBytes(file));
                    future.thenAccept(result -> imageRepository.save(new Image(UUID.randomUUID(), result, hash)));
                    return future;
                }).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(promises);
    }

    public void searchFile(MultipartFile file) {

    }

    public void purgeFiles() {
        fileSystem.deleteAllFiles();
        imageRepository.deleteAll();
    }


}
