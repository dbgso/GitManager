package com.github.dbgso.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ConfigurationProperties(prefix="src-manage-tool")
public class Configuration {

	public String repositoryUrl;
}
