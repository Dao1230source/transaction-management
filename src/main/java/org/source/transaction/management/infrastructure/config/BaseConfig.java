package org.source.transaction.management.infrastructure.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
public class BaseConfig {

    /**
     * <pre>
     * 1、由于时间问题，暂时忽略风险
     * 2、实际分布式系统中应使用 Redisson 的分布式布隆过滤器
     * </pre>
     *
     * @return bloomFilter
     */
    @SuppressWarnings("UnstableApiUsage")
    @Bean
    public BloomFilter<String> bloomFilter() {
        return BloomFilter.create(
                Funnels.stringFunnel(Charset.defaultCharset()),
                1000,
                0.01);
    }
}
