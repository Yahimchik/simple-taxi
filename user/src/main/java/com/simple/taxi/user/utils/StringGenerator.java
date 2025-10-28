package com.simple.taxi.user.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;

import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class StringGenerator {
    public String getUniqueNameOfFile(String originalFileName, UUID uuid) {
        return LocalDate.now() + "/" + uuid + "." + FilenameUtils.getExtension(originalFileName);
    }
}
