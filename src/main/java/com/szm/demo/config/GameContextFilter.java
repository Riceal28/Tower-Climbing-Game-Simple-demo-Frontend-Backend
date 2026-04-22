package com.szm.demo.config;

import com.szm.demo.context.GameContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameContextFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            Long userId = getHeaderLong(request, "X-User-Id");
            Long playerId = getHeaderLong(request, "X-Player-Id");
            Long saveId = getHeaderLong(request, "X-Save-Id");
            Long battleId = getHeaderLong(request, "X-Battle-Id");
            logger.info("Filter:userId[{}],playerId[{}],saveId[{}],battleId[{}]",userId,playerId,saveId,battleId);
            if (userId != null) {
                GameContext.init(userId, playerId, saveId,battleId);
            }
            chain.doFilter(request, servletResponse);
        } finally {
            GameContext.clear();
        }
    }
    private Long getHeaderLong(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null || !value.trim().matches("-?\\d+")) {
            return null;
        }
        return Long.parseLong(value);
    }
}
