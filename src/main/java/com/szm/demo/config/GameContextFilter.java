package com.szm.demo.config;

import com.szm.demo.context.GameContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class GameContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            Long userId = Long.parseLong(request.getHeader("X-User-Id"));
            Long playerId = Long.parseLong(request.getHeader("X-Player-Id"));
            Long saveId = Long.parseLong(request.getHeader("X-Save-Id"));
            Long battleId = Long.parseLong(request.getHeader("X-Battle-Id"));
            if (userId != null) {
                GameContext.init(userId, playerId, saveId,battleId);
            }
            chain.doFilter(request, servletResponse);
        } finally {
            GameContext.clear();
        }
    }
}
