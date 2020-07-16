package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.fs.FileSystemService;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    private ImageRepository imageRepository;
    private HorizontalDHash hasher;
    private FileSystem fileSystem;

    @Autowired
    public ImageService(ImageRepository imageRepository, HorizontalDHash hashCalculator, FileSystem fileSystem) {
        this.imageRepository = imageRepository;
        this.hasher = hashCalculator;
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

    public List<SearchResultDTO> searchFile(MultipartFile file, double threshold) {

        var hash = hasher.calculateHash(FileSystemService.getBytes(file));
        var responseFromDb = imageRepository.getSearchResult(hash, threshold);

        if(responseFromDb.size() == 0) {
            CompletableFuture.supplyAsync(() -> {
                var future = fileSystem.saveImage(file);
                future.thenAccept(result -> imageRepository.save(new Image(UUID.randomUUID(), result, hash)));
                return future;
            });
        }
        return responseFromDb;
    }

    public void purgeFiles() {
        fileSystem.deleteAllFiles();
        imageRepository.deleteAll();
    }

    public void deleteById(UUID imageId) {
        fileSystem.deleteFileById(imageId);
        imageRepository.deleteById(imageId);
    }

}
