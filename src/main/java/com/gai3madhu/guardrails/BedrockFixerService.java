package com.gai3madhu.guardrails;

import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import software.amazon.awssdk.regions.Region;

public class BedrockFixerService {

    private ChatLanguageModel claude;

    public BedrockFixerService() {
        // Uses your AWS credentials from ~/.aws/credentials
        this.claude = BedrockAnthropicMessageChatModel.builder()
                .model("anthropic.claude-3-haiku-20240307-v1:0") // cheapest, fast
                .region(Region.of("us-east-1"))
                .maxTokens(1000)
                .temperature(0.0)
                .build();
    }

    public String fixCode(FlawChecker flaw, String badCode) {
        String prompt = """
            You are a IBMi to Java modernization expert.
            FLAW: %s
            WHY IT HAPPENS: %s
            FIX RULE: %s
            
            BAD CODE FROM AI:
            %s
            
            Return ONLY fixed Java code. No explanation.
            """.formatted(flaw.getFlawName(), flaw.getWhyAIDoesIt(), flaw.fixPrompt(), badCode);

        return claude.generate(prompt);
    }
}