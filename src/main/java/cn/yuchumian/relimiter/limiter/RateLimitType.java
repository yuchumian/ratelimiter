package cn.yuchumian.relimiter.limiter;

import cn.yuchumian.relimiter.annotation.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yuchumian
 * @since 2020/5/23 19:33
 */
public enum RateLimitType implements RateLimiterTypeOperator {

    /**
     * 自定义key
     */
    CUSTOMER {
        @Override
        public String getLimitKey(RateLimiter rateLimiter) {
            return rateLimiter.key();
        }
    },
    /**
     * 根据请求IP
     */
    IP {
        @Override
        public String getLimitKey(RateLimiter rateLimiter) {
            return getIpAddress();
        }
    };

    private static final String UNKNOWN = "unknown";

    public String getIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
