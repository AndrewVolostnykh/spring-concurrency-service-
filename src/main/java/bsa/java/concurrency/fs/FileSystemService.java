package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileSystemService implements FileSystem {

    private ImageRepository imageRepository;

    @Autowired
    public FileSystemService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    private static final String path = "D:\\Developing\\git_reposes\\bsa-java-concurrency-template\\src\\images\\"; //vulnerability here, this is a path in windows
    private static final Path savePath = Paths.get(path);

    @Override
    public CompletableFuture<String> saveImage(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> saveFile(file), threadPool);
    }

    private String saveFile(MultipartFile file) {

        String filePath = null;

        try (var out = new BufferedOutputStream(Files.newOutputStream(savePath.resolve(file.getOriginalFilename())))) {

            if(!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }

            out.write(file.getBytes());
            filePath = path + file.getOriginalFilename();
            out.flush();

        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return filePath;
    }

    @Override
    public void deleteAllFiles() {
        File directory = new File(FileSystemService.getPath());
        File[] files = directory.listFiles();

        for(File file : files) {
            if(!file.delete()) {
                System.out.println("I cant delete this file: <<" + file.getAbsolutePath() + ">>");
            }
        }
    }

    @Override
    public void deleteFileById(UUID id) {
        var image = imageRepository.findOneById(id);
        var file = new File(image.getUrl());
        if(!file.delete()) {
            System.out.println("I cant delete this file: " + file.getAbsolutePath() + ", image url: " + image.getUrl());
        }
    }

    public static String getPath() {
        return path;
    }


    public static byte[] getBytes(MultipartFile file) {
        byte[] result = null;
        try {
            result = file.getBytes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

}
