package com.samdasu.dodoong.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    @Override
    public String uploadProfileImage(MultipartFile file) {
        // TODO: S3 연결 후 이미지 업로드 구현
        throw new UnsupportedOperationException(
                "S3 이미지 업로드 기능이 아직 구현되지 않았습니다."
        );
    }

    @Override
    public void delete(String fileUrl) {
        // TODO: S3 연결 후 이미지 삭제 기능 구현
        throw new UnsupportedOperationException(
                "S3 이미지 삭제 기능이 아직 구현되지 않았습니다."
        );
    }
}