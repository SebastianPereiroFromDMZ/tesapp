package work_with_files.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@ToString
public class FileInfo {

    private Long id;

    private String name;

    private Long size;

    private String key;

    private LocalDate uploadDate;
}