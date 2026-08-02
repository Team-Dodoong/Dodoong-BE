package com.samdasu.dodoong.domain.member.dto.response;

import com.samdasu.dodoong.global.storage.PresignedUpload;

public record ProfileImageUploadResponse(String uploadUrl, String profileImageKey) {

    public static ProfileImageUploadResponse from(PresignedUpload presignedUpload) {
        return new ProfileImageUploadResponse(presignedUpload.uploadUrl(), presignedUpload.key());
    }
}