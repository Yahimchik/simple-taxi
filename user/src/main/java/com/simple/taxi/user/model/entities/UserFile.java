package com.simple.taxi.user.model.entities;

import com.simple.taxi.user.model.enums.FileType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("user_files")
public class UserFile {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("original_file_name")
    private String originalFileName;

    @Column("content_type")
    private String contentType; // image/png, application/pdf и т.д.

    @Column("file_type")
    private FileType fileType; // например: "AVATAR", "DOCUMENT", "PHOTO"

    @Column("size")
    private Long size; // в байтах

    @Column("file_name")
    private String fileName;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("deleted_at")
    private Instant deletedAt;
}