package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.DHasher;
import bsa.java.concurrency.image.Image;
import bsa.java.concurrency.image.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class NotAsyncExecutor {

    @Autowired
    ImageRepository imageRepository;

    public void saveImages(MultipartFile[] files) { // imitation of multithreading saving
        try {
            for (MultipartFile file : files) { // this is an imitation of general threads

                fileWriter(file); // first thread
                Long resutlHash = DHasher.calculateDHash(ImageIO.read(new ByteArrayInputStream(file.getBytes()))); // second thread (if i correct get tt)


                imageRepository.save( // next part of working of general thread
                        new Image(UUID.randomUUID(), FileSystemService.getPath() + file.getOriginalFilename(), resutlHash)
                );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void fileWriter(MultipartFile file) throws IOException {
        OutputStream out = new FileOutputStream(FileSystemService.getPath() + file.getOriginalFilename());
        out.write(file.getBytes());
        out.flush();
        out.close();
    }
}
