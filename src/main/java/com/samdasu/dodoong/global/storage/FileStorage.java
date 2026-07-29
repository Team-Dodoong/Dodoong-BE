package com.samdasu.dodoong.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    String uploadProfileImage(MultipartFile file);

    void delete(String fileUrl);
}
