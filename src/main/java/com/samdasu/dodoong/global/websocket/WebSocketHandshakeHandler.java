package com.samdasu.dodoong.global.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        Long memberId = (Long) attributes.get(StompHandshakeInterceptor.MEMBER_ID_ATTRIBUTE);
        return new WebSocketPrincipal(memberId);
    }
}
