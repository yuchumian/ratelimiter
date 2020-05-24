package cn.yuchumian.relimiter.annotation;

import cn.yuchumian.relimiter.limiter.RateLimitType;

import java.lang.annotation.*;

/**
 * @author yuchumian
 * @since 2020/5/23 19:28
 */
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    /**
     * 资源的key
     *
     * @return String
     */
    String key() default "";

    /**
     * Key的prefix
     *
     * @return String
     */
    String prefix() default "RateLimiter:";

    /**
     * 时间段/单位秒
     * @return int
     */
    int expireTime();

    /**
     * 时间段内访问最多次数
     *
     * @return int
     */
    int count();

    /**
     * 限制类型类型 默认自定义
     *
     * @return RateLimitType
     */
    RateLimitType limitType() default RateLimitType.CUSTOMER;
}
