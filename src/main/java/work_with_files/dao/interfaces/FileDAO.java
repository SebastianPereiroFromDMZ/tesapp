package work_with_files.dao.interfaces;

import work_with_files.model.FileInfo;

public interface FileDAO {

    FileInfo create(FileInfo file);

    FileInfo findById(Long fileId);

    void delete(Long fileId);
}