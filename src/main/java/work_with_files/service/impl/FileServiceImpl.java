package work_with_files.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import work_with_files.dao.interfaces.FileDAO;
import work_with_files.model.FileInfo;
import work_with_files.service.interfaces.FileService;
import work_with_files.service.util.FileManager;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileDAO fileDAO;
    private final FileManager fileManager;

    /**
     * 31 — в случае падения IOException, все наши сохранения в БД откатятся.
     * 34 — генерируем ключ, который будет уникальным для файла, когда он будет соохранен (даже если будут сейвиться два файла с одинаковыми именами,
     * путаницы не возникнет).
     * 35-39 — строим сущность для сохранения в БД.
     * 40 — загоняем сущность с инфой в БД.
     * 41 — сохраняем файл с за хешированным именем.
     * 43 — возвращаем созданую сущность FileInfo, но со сгенерированным id в БД (об этом речь пойдёт чуть ниже) и датой создания.
     */

    @Transactional(rollbackFor = {IOException.class})
    @Override
    public FileInfo upload(MultipartFile resource) throws IOException {
        String key = generateKey(resource.getName());
        FileInfo createdFile = FileInfo.builder()
                .name(resource.getOriginalFilename())
                .key(key)
                .size(resource.getSize())
                .build();
        createdFile = fileDAO.create(createdFile);
        fileManager.upload(resource.getBytes(), key);

        return createdFile;
    }

    @Override
    public Resource download(String key) throws IOException {
        return fileManager.download(key);
    }

    @Transactional(readOnly = true)
    @Override
    public FileInfo findById(Long fileId) {
        return fileDAO.findById(fileId);
    }

    @Transactional(rollbackFor = {IOException.class})
    @Override
    public void delete(Long fileId) throws IOException {
        FileInfo file = fileDAO.findById(fileId);
        fileDAO.delete(fileId);
        fileManager.delete(file.getKey());
    }

    /**
     * Метод генерации ключа к файлу:
     * Здесь мы хешируем имя + дата создания, что и обеспечит нам уникальность.
     */
    private String generateKey(String name) {
        return DigestUtils.md5Hex(name + LocalDateTime.now().toString());
    }
}