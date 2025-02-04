/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.autoconfigure.vectorstore.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties.CONFIG_PREFIX;

/**
 * @author Julien Ruaux
 */
@ConfigurationProperties(CONFIG_PREFIX)
public class RedisVectorStoreProperties {

	public static final String CONFIG_PREFIX = "spring.ai.vectorstore.redis";

	private String uri = "redis://localhost:6379";

	private String index;

	private String prefix;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String name) {
		this.index = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
