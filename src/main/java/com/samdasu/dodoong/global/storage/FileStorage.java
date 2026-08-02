package com.samdasu.dodoong.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    // 서버를 경유해 파일 업로드 후 공개 URL 반환
    String upload(MultipartFile file, String directory);

    // 업로드용 presigned PUT url 발급
    PresignedUpload createUploadUrl(String directory, String contentType);

    // 비공개 객체 조회용 presigned GET url 발급
    String createDownloadUrl(String key);

    // 공개 객체 url 생성
    String toPublicUrl(String key);

    // 객체 삭제
    void delete(String key);
}
