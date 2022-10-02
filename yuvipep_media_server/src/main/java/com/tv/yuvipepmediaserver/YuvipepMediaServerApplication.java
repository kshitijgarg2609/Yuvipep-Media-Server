package com.tv.yuvipepmediaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAutoConfiguration
@EnableCaching
// @ComponentScan(basePackages = {"com.tv.yuvipepmediaserver.video",
// "com.tv.yuvipepmediaserver.image", "com.tv.yuvipepmediaserver.repository",
// "com.tv.yuvipepmediaserver.model"})
public class YuvipepMediaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YuvipepMediaServerApplication.class, args);
	}

}
