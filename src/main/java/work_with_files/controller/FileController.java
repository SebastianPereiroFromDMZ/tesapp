package work_with_files.controller;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work_with_files.model.FileInfo;
import work_with_files.service.interfaces.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    /**
     * 30 — Принимаем файл в виде MultipartFile. Можно принимать и в виде массива байтов, но этот вариант мне нравится больше,
     * так как мы с MultipartFile можем вытягивать различные свойства переданного файла.
     * 31 - 35 — оборачиваем наши действия в try catch, чтобы если на более низком уровне возникнет исключение,
     * мы его пробросили выше и отправили 400 ошибку ответом.
     */
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileInfo> upload(@RequestParam MultipartFile attachment) {
        try {
            return new ResponseEntity<>(fileService.upload(attachment), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 45 — вытягиваем из БД прилежащую сущность FileInfo.
     * 46 — по ключу из сущности скачиваем файл
     * 47-49 — отправляем назад файл, при этом добавив в хедер имя файла (опять же, полученное из сущности с информацией о файле)
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        try {
            FileInfo foundFile = fileService.findById(id);
            Resource resource = fileService.download(foundFile.getKey());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + foundFile.getName())
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            fileService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}