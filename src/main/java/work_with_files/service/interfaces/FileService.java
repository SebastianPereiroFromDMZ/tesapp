package work_with_files.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import work_with_files.model.FileInfo;

import java.io.IOException;

public interface FileService {

    FileInfo upload(MultipartFile resource) throws IOException;

    Resource download(String key) throws IOException;

    FileInfo findById(Long fileId);

    void delete(Long fileId) throws IOException;
}
