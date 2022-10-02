package com.tv.yuvipepmediaserver.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "amazons3.aws-config")
public class AmazonConfig {
	private String accessKey;
	private String secretKey;
	private String region;

	public String getAccessKey() {
			return accessKey;
	}

	public String getSecretKey() {
			return secretKey;
	}

	public String getRegion() {
			return region;
	}

	public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
	}

	public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
	}

	public void setRegion(String region) {
			this.region = region;
	}

	@Bean
	public AmazonS3 s3() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSocketTimeout(120 * 1000);
		clientConfiguration.setClientExecutionTimeout(120 * 1000);
		clientConfiguration.setCacheResponseMetadata(true);
		clientConfiguration.setUseThrottleRetries(true);

		return AmazonS3ClientBuilder
				.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withClientConfiguration(clientConfiguration)
				.build();

	}
}
