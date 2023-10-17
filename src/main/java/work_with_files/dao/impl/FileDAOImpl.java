package work_with_files.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import work_with_files.dao.interfaces.FileDAO;
import work_with_files.model.FileInfo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class FileDAOImpl implements FileDAO {

    private static final String CREATE_FILE = "INSERT INTO netology.FILES_INFO (file_name, file_size, file_key, upload_date) VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 32 — создаём дату которую и сохраним.
     * 33 - 42 — сохраняем  сущность, но более сложным путем, с явным созданием объекта PreparedStatement,
     * чтобы можно было вытащить сгенерированный id (он его вытягивает не отдельным запросом, а в виде ответных метаданных).
     * 43 - 47 — достраиваем нашу многострадальную сущность и отдаем наверх (на самом деле он его не достраивает,
     * а создаёт новый объект, заполняя переданные поля и копируя остальные с изначального).
     */
    @Override
    @Transactional
    public FileInfo create(final FileInfo file) {
        LocalDate uploadDate = LocalDate.now();
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        FileInfo newFile = FileInfo.builder().id(1L).name(file.getName()).size(file.getSize()).key(file.getKey()).uploadDate(LocalDate.now()).build();
        entityManager.persist(newFile);
        return newFile;

//        jdbcTemplate.update(x -> {
//            PreparedStatement preparedStatement = x.prepareStatement(CREATE_FILE, Statement.RETURN_GENERATED_KEYS);
//            preparedStatement.setString(1, file.getName());
//            preparedStatement.setLong(2, file.getSize());
//            preparedStatement.setString(3, file.getKey());
//            preparedStatement.setDate(4, Date.valueOf(uploadDate));
//            return preparedStatement;
//        }, keyHolder);

////        return file.toBuilder()
////                .id(keyHolder.getKey().longValue())
////                .uploadDate(uploadDate)
////                .build();
//        return null;
    }

    private static final String FIND_FILE_BY_ID = "SELECT id, file_name, file_size, file_key, upload_date FROM files_info WHERE id = ?";

    @Override
    public FileInfo findById(Long fileId) {
        return jdbcTemplate.queryForObject(FIND_FILE_BY_ID, rowMapper(), fileId);
    }

    private RowMapper<FileInfo> rowMapper() {
        return (rs, rowNum) -> FileInfo.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("file_name"))
                .size(rs.getLong("file_size"))
                .key(rs.getString("file_key"))
                .uploadDate(rs.getObject("upload_date", LocalDate.class))
                .build();
    }

    private static final String DELETE_FILE_BY_ID = "DELETE FROM files_info WHERE id = ?";

    @Override
    public void delete(Long fileId) {
        jdbcTemplate.update(DELETE_FILE_BY_ID, fileId);
    }
}