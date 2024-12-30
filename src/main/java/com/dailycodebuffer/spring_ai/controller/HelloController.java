package com.dailycodebuffer.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class HelloController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/celeb-details.st")
    private Resource celebPrompt;

    public HelloController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String prompt(@RequestParam String message) {
        return chatClient
                .prompt(message)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();
    }


    @GetMapping("/celeb")
    public String getCelebDetails(@RequestParam String name) {

        String message = """
                List the details of the Famous personality {name}
                along with their Carrier achievements.
                Show the details in the readable format
                """;

        PromptTemplate template
                = new PromptTemplate(celebPrompt);

        Prompt prompt = template.create(
                Map.of("name",name)
        );

        return chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();
    }


    @GetMapping("/sports")
    public String getSportsDetail(@RequestParam String name) {

        String message = """
                List the details of the Sport %s 
                along with their Rules and Regulations.
                Show the details in the readable format
                """;

        String systemMessage = """
                You are a smart Virtual Assistant.
                Your task is to give the details about the Sports.
                If someone ask about something else and you do not know the answer,
                Just say that you do not know the answer.
                """;

        UserMessage userMessage
                = new UserMessage(String.format(message,name));

        SystemMessage systemMessage1
                = new SystemMessage(systemMessage);

        Prompt prompt
                = new Prompt(List.of(userMessage,systemMessage1));

        return chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();

    }

}
