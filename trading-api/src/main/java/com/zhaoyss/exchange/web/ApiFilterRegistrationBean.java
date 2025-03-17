package com.zhaoyss.exchange.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhaoyss.exchange.ApiError;
import com.zhaoyss.exchange.ApiException;
import com.zhaoyss.exchange.bean.AuthToken;
import com.zhaoyss.exchange.ctx.UserContext;
import com.zhaoyss.exchange.model.ui.UserProfileEntity;
import com.zhaoyss.exchange.support.AbstractFilter;
import com.zhaoyss.exchange.user.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.startup.UserConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zhaoyss
 * @date 12/3/2025 上午 11:12
 * @description:
 */
@Component
public class ApiFilterRegistrationBean extends FilterRegistrationBean<Filter> {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Value("#{exchangeConfiguration.hmackey}")
    String hmacKey;

    @PostConstruct
    public void init() {
        ApiFilter filter = new ApiFilter();
        setFilter(filter);
        addUrlPatterns("/api/*");
        setName(filter.getClass().getSimpleName());
        setOrder(100);
    }


    class ApiFilter extends AbstractFilter {

        @Override
        public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;

            String path = request.getRequestURI();
            logger.info("process api {} {}...", request.getMethod(), path);
            // 设置默认编码
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            // 尝试解析用户
            Long userId = null;
            try {
                userId = parseUser(request);
            } catch (ApiException e) {
                sendErrorResponse(response, e);
                return;
            }
            if (userId == null) {
                chain.doFilter(request, response);
            } else {
                try (UserContext ctx = new UserContext(userId)) {
                    chain.doFilter(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        void sendErrorResponse(HttpServletResponse response, ApiException e) throws IOException {
            response.sendError(400);
            response.setContentType("application/json");
            PrintWriter pw = response.getWriter();
            pw.write(objectMapper.writeValueAsString(e.error));
            pw.flush();
        }

        private Long parseUser(HttpServletRequest request) {
            // 尝试通过Authorization Header认证用户：
            String auth = request.getHeader("Authorization");
            if (auth != null) {
                return parseUserFromAuthorization(auth);
            }
            // 尝试通过API Key 认证用户:
            String apiKey = request.getHeader("API-Key");
            String apiSignature = request.getHeader("API-Signature");
            if (apiKey != null && apiSignature != null) {
                return parseUserFromApiKey(apiKey, apiSignature, request);
            }
            return null;
        }

        private Long parseUserFromApiKey(String apiKey, String apiSignature, HttpServletRequest request) {
            // TODO: 验证API-Key，API-Secret 并返回 userId
            return null;
        }

        private Long parseUserFromAuthorization(String auth) {
            if (auth.startsWith("Basic ")) {
                String eap = new String(Base64.getDecoder().decode(auth.substring(6)), StandardCharsets.UTF_8);
                int pos = eap.indexOf(':');
                if (pos < 1) {
                    throw new ApiException(ApiError.AUTH_SIGIN_FAILED, "Invalid email or password.");
                }
                String email = eap.substring(0, pos);
                String password = eap.substring(pos + 1);
                UserProfileEntity p = userService.signup(email, password);
                Long userId = p.getUserId();
                if (logger.isDebugEnabled()) {
                    logger.debug("parse from basic authorization: {}", userId);
                }
                return userId;
            }
            if (auth.startsWith("Bearer ")) {
                AuthToken token = AuthToken.fromSecureString(auth.substring(7), hmacKey);
                if (token.isExpired()) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("parse from bearer authorization: {}", token.userId());
                }
                return token.userId();
            }
            throw new ApiException(ApiError.AUTH_SIGIN_FAILED, "Invalid Authorization header.");
        }
    }
}
