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

package org.springframework.ai.bedrock.llama2;

import java.util.List;

import reactor.core.publisher.Flux;

import org.springframework.ai.bedrock.MessageToPromptConverter;
import org.springframework.ai.bedrock.llama2.api.Llama2ChatBedrockApi;
import org.springframework.ai.bedrock.llama2.api.Llama2ChatBedrockApi.Llama2ChatRequest;
import org.springframework.ai.bedrock.llama2.api.Llama2ChatBedrockApi.Llama2ChatResponse;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.client.AiStreamClient;
import org.springframework.ai.client.Generation;
import org.springframework.ai.metadata.ChoiceMetadata;
import org.springframework.ai.metadata.Usage;
import org.springframework.ai.prompt.Prompt;

/**
 * Java {@link AiClient} and {@link AiStreamClient} for the Bedrock Llama2 chat model.
 *
 * @author Christian Tzolov
 * @since 0.8.0
 */
public class BedrockLlama2ChatClient implements AiClient, AiStreamClient {

	private final Llama2ChatBedrockApi chatApi;

	private Float temperature;

	private Float topP;

	private Integer maxGenLen;

	public BedrockLlama2ChatClient(Llama2ChatBedrockApi chatApi) {
		this.chatApi = chatApi;
	}

	public BedrockLlama2ChatClient withTemperature(Float temperature) {
		this.temperature = temperature;
		return this;
	}

	public BedrockLlama2ChatClient withTopP(Float topP) {
		this.topP = topP;
		return this;
	}

	public BedrockLlama2ChatClient withMaxGenLen(Integer maxGenLen) {
		this.maxGenLen = maxGenLen;
		return this;
	}

	@Override
	public AiResponse generate(Prompt prompt) {
		final String promptValue = MessageToPromptConverter.create().toPrompt(prompt.getMessages());

		var request = Llama2ChatRequest.builder(promptValue)
			.withTemperature(this.temperature)
			.withTopP(this.topP)
			.withMaxGenLen(this.maxGenLen)
			.build();

		Llama2ChatResponse response = this.chatApi.chatCompletion(request);

		return new AiResponse(List.of(new Generation(response.generation())
			.withChoiceMetadata(ChoiceMetadata.from(response.stopReason().name(), extractUsage(response)))));
	}

	@Override
	public Flux<AiResponse> generateStream(Prompt prompt) {

		final String promptValue = MessageToPromptConverter.create().toPrompt(prompt.getMessages());

		var request = Llama2ChatRequest.builder(promptValue)
			.withTemperature(this.temperature)
			.withTopP(this.topP)
			.withMaxGenLen(this.maxGenLen)
			.build();

		Flux<Llama2ChatResponse> fluxResponse = this.chatApi.chatCompletionStream(request);

		return fluxResponse.map(response -> {
			String stopReason = response.stopReason() != null ? response.stopReason().name() : null;
			return new AiResponse(List.of(new Generation(response.generation())
				.withChoiceMetadata(ChoiceMetadata.from(stopReason, extractUsage(response)))));
		});
	}

	private Usage extractUsage(Llama2ChatResponse response) {
		return new Usage() {

			@Override
			public Long getPromptTokens() {
				return response.promptTokenCount().longValue();
			}

			@Override
			public Long getGenerationTokens() {
				return response.generationTokenCount().longValue();
			}
		};
	}

}
