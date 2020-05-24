package cn.yuchumian.relimiter.controller;

import cn.yuchumian.relimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yuchumian
 * @since 2020/5/23 19:50
 */
@RequestMapping(value = "/")
@RestController
@Slf4j
public class HelloController {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
    @RateLimiter(expireTime = 10,count = 1)
    @GetMapping(value = "hello")
    public String hello() {
        log.info("yuchumian------>>>>>>>>>>>{}",ATOMIC_INTEGER.incrementAndGet());
        return "hello";
    }
}
