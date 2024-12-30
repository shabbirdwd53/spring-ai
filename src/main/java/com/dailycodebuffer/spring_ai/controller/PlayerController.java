package com.dailycodebuffer.spring_ai.controller;

import com.dailycodebuffer.spring_ai.model.Achievement;
import com.dailycodebuffer.spring_ai.model.Player;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {

    private final ChatClient chatClient;

    public PlayerController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/player")
    public List<Player> getPlayerAchievement(@RequestParam String name) {

        BeanOutputConverter<List<Player>> converter
                = new BeanOutputConverter<>(new ParameterizedTypeReference<List<Player>>() {
        });

        String message = """
                Generate a list of Career achievements for the sportsperson {sports}.
                Include the Player as the key and achievements as the value for it
                {format}
                """;

        PromptTemplate template
                = new PromptTemplate(message);

        Prompt prompt
                = template.create(Map.of("sports",name,
                "format",
                converter.getFormat()));

       /* ChatResponse response = chatClient
                .prompt(prompt)
                .call()
                .chatResponse();

        return response.getResult().getOutput().getContent();*/

        Generation result = chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult();

        return converter.convert(result.getOutput().getContent());
    }


    @GetMapping("/achievement/player")
    public List<Achievement> getAchievement(@RequestParam String name) {

        String message = """
                Provide a List of Achievements for {player}
                """;

        PromptTemplate template
                = new PromptTemplate(message);

        Prompt prompt
                = template.create(Map.of("player",name));

        return chatClient.prompt(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<Achievement>>() {
                });
    }
}
