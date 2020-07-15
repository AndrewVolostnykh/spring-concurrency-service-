package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
import bsa.java.concurrency.fs.NotAsyncExecutor;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    NotAsyncExecutor fileService;
    @Autowired
    DHasher hasher;
    @Autowired
    ImageService imageService;

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public CompletableFuture<Void> batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        return imageService.saveImages(files);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        //fileService.searchOfCoincidence(file, threshold);
        return imageRepository.getSearchResult(hasher.calculateHash(fileService.getBytes(file)), threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        fileService.deleteFromFileSystem(imageId); // some of shit code
        imageRepository.deleteById(imageId);
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
        imageService.purgeFiles();
    }
}
