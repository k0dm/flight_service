package com.spring.flight_service.config


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.RedisStandaloneConfiguration

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
class RedisConfig (val env: Environment){

    companion object {
        val logger = LoggerFactory.getLogger(RedisConfig::class.java)!!
    }

    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 0

    @Bean
    fun jedisConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.hostName = host
        config.port = port
        val lettuceConnectionFactory = LettuceConnectionFactory(config)
        lettuceConnectionFactory.validateConnection = true
        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Object> {
        logger.info("Create redisTemplate")
        var template = RedisTemplate<String, Object>()
        template.connectionFactory = jedisConnectionFactory()
        return template
    }
}


