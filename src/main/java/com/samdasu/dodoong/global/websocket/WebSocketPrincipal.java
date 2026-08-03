package com.samdasu.dodoong.global.websocket;

import java.security.Principal;

public record WebSocketPrincipal(
        Long memberId
) implements Principal {
    @Override
    public String getName() {
        return String.valueOf(memberId);
    }
}
