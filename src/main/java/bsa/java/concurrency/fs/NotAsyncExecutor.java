package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.DHasher;
import bsa.java.concurrency.image.Image;
import bsa.java.concurrency.image.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class NotAsyncExecutor {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    DHasher hasher;

    public void saveImages(MultipartFile[] files) { // imitation of multithreading saving
        try {
            for (MultipartFile file : files) { // this is an imitation of general threads

                fileWriter(file); // first thread
                Long resutlHash = hasher.calculateHash(file.getBytes());
                        //DHasher.calculateDHash(ImageIO.read(new ByteArrayInputStream(file.getBytes()))); // second thread (if i correct get tt)

                imageRepository.save( // next part of working of general thread
                        new Image(UUID.randomUUID(), FileSystemService.getPath() + file.getOriginalFilename(), resutlHash)
                );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void fileWriter(MultipartFile file) throws IOException {
        OutputStream out = new FileOutputStream(FileSystemService.getPath() + file.getOriginalFilename());
        out.write(file.getBytes());
        out.flush();
        out.close();
    }

//    public List<SearchResultDTO> searchOfCoincidence(MultipartFile file, double threshold) {
//
//        long inputFile;
//        var resultResponse = new LinkedList<SearchResultDTO>();
//
//        try {
//            inputFile = DHasher.calculateDHash(ImageIO.read(new ByteArrayInputStream(file.getBytes())));
//
//            var allImagesFromDb = imageRepository.findAll();
//
//            var temp = 0d;
//            for(Image image : allImagesFromDb) {
//
//                temp = DHasher.matchPercent(image.getHash(), inputFile);
//
//                if(temp >= threshold) { // general thread
//                    resultResponse.add(SearchResultDtoImpl.builder().imageId(image.getId()).imageUrl(image.getUrl()).matchPercent(temp).build());
//                }
//            }
//
//            if(resultResponse.size() == 0) {
//                fileWriter(file);
//                imageRepository.save(new Image(UUID.randomUUID(), FileSystemService.getPath() + file.getOriginalFilename(), inputFile));
//            }
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return resultResponse; // general thread
//    }

    public void deleteFromFileSystem(UUID id) {
        var image = imageRepository.findOneById(id);
        var file = new File(image.getUrl());
        if(!file.delete()) {
            System.out.println("I cant delete this file: " + file.getAbsolutePath() + ", image url: " + image.getUrl());
        }
    }

    public void deleteAllFiles() {
        File directory = new File(FileSystemService.getPath());
        File[] files = directory.listFiles();

        for(File file : files) {
            if(!file.delete()) {
                System.out.println("I cant delete this file: <<" + file.getAbsolutePath() + ">>");
            }
        }
    }

    public byte[] getBytes(MultipartFile file) {
        byte[] result = null;
        try {
            result = file.getBytes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
