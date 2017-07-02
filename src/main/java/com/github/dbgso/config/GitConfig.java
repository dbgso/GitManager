package com.github.dbgso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties
public class GitConfig {

	private String repositoryPath;
}
