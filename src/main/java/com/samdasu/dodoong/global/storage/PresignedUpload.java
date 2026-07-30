package com.samdasu.dodoong.global.storage;

public record PresignedUpload(
        String uploadUrl,
        String key
) {
}
