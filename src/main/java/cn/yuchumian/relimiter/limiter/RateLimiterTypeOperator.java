package cn.yuchumian.relimiter.limiter;

import cn.yuchumian.relimiter.annotation.RateLimiter;

/**
 * @author yuchumian
 * @since 2020/5/24 13:40
 */
public interface RateLimiterTypeOperator {
    /**
     * 获取key
     * @param rateLimiter 限流注解
     * @return  string
     */
    String getLimitKey(RateLimiter rateLimiter);
}
