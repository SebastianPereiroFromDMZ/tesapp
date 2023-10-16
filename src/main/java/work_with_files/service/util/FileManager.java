package work_with_files.service.util;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    /**
     * 20 — принимаем файл в виде массива байтов и отдельно имя, под которым он будет сохранен (наш сгенерированный ключ).
     * 21 - 22 — создаем путь (а в пути прописываем путь плюс наш ключ) и файл по нему.
     * 25 - 26 — создаем поток и пишем туда наши байты (и оборачиваем это все добро в try-finally чтобы быть уверенными, что поток точно закроется).
     */
    public void upload(byte[] resource, String keyName) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, keyName);
        Path file = Files.createFile(path);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file.toString());
            stream.write(resource);
        } finally {
            stream.close();
        }
    }

    public Resource download(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH + key);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException();
        }
    }

    public void delete(String key) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH + key);
        Files.delete(path);
    }
}

