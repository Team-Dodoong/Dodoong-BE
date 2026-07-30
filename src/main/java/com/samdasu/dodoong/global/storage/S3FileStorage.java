package com.samdasu.dodoong.global.storage;

import com.samdasu.dodoong.global.config.S3Properties;
import com.samdasu.dodoong.global.exception.CustomException;
import com.samdasu.dodoong.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Properties props;

    private static final Map<String, String> ALLOWED = Map.of(
            "image/jpeg", ".jpg",
            "image/png",  ".png",
            "image/webp", ".webp"
    );

    @Override
    public PresignedUpload createUploadUrl(String directory, String contentType) {
        String extension = resolveExtension(contentType);
        String key = buildKey(directory, extension);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(props.presignExpiration())
                .putObjectRequest(objectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        return new PresignedUpload(uploadUrl, key);
    }

    @Override
    public String createDownloadUrl(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(props.presignExpiration())
                .getObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public String toPublicUrl(String key) {
        return props.baseUrl() + "/" + key;
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build());
    }

    private String resolveExtension(String contentType) {
        String ext = ALLOWED.get(contentType);
        if (ext == null) {
            throw new CustomException(ErrorCode.UNSUPPORTED_IMAGE_TYPE);
        }
        return ext;
    }

    private String buildKey(String directory, String ext) {
        String normalized = directory.endsWith("/")
                ? directory.substring(0, directory.length() - 1)
                : directory;
        return normalized + "/" + UUID.randomUUID() + ext;
    }
}