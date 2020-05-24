package cn.yuchumian.relimiter.aop;

import com.google.common.collect.ImmutableList;
import cn.yuchumian.relimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author yuchumian
 * @since 2020/5/23 19:42
 */
@Aspect
@Configuration
@Slf4j
public class RateLimiterAspect {

    private final RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    public RateLimiterAspect(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("execution(public * *(..)) && @annotation(cn.yuchumian.relimiter.annotation.RateLimiter)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
        int expireTime = rateLimiter.expireTime();
        int limitCount = rateLimiter.count();
        String key = rateLimiter.limitType().getLimitKey(rateLimiter);
        if (StringUtils.isBlank(key)) {
            key = StringUtils.upperCase(method.getName());
        }
        ImmutableList<String> keys = ImmutableList.of(StringUtils.join(rateLimiter.prefix(), key));
        String luaScript = buildLuaScript();
        RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
        try {
            Number count = redisTemplate.execute(redisScript, keys, limitCount, expireTime);
            log.info("访问次数：{}, 访问资源名为: {}, key : {}", count,   pjp.getTarget().getClass().getName() + "-->" + method.getName(), key);
            if (count != null && count.intValue() <= limitCount) {
                return pjp.proceed();
            } else {
                throw new RuntimeException("接口访问过于频繁请稍后在再试！");
            }
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
            throw new RuntimeException("server exception");
        }
    }

    /**
     * 限流 脚本
     * @return lua脚本
     */
    public String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append("local c");
        lua.append("\nc = redis.call('get',KEYS[1])");
        // 调用不超过最大值，则直接返回
        lua.append("\nif c and tonumber(c) > tonumber(ARGV[1]) then");
        lua.append("\nreturn c;");
        lua.append("\nend");
        // 执行计算器自加
        lua.append("\nc = redis.call('incr',KEYS[1])");
        lua.append("\nif tonumber(c) == 1 then");
        // 从第一次调用开始限流，设置对应键值的过期
        lua.append("\nredis.call('expire',KEYS[1],ARGV[2])");
        lua.append("\nend");
        lua.append("\nreturn c;");
        return lua.toString();
    }

}
