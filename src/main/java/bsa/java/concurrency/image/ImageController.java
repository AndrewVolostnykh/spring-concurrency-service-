package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystemService;
import bsa.java.concurrency.fs.NotAsyncExecutor;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    NotAsyncExecutor fileService;

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public void batchUploadImages(@RequestParam("images") MultipartFile[] files) {
        fileService.saveImages(files); // this will call an imitation of concurrent work
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SearchResultDTO> searchMatches(@RequestParam("image") MultipartFile file, @RequestParam(value = "threshold", defaultValue = "0.9") double threshold) {
        return fileService.searchOfCoincidence(file, threshold);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("id") UUID imageId) {
        fileService.deleteFromFileSystem(imageId); // re-write using concurrency
        imageRepository.deleteById(imageId); // this works !
    }

    @DeleteMapping("/purge")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void purgeImages(){
        fileService.deleteAllFiles(); // re-write using concurrency
        imageRepository.deleteAll();
    }
}
