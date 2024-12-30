package com.dailycodebuffer.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ICSController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ICSController(ChatClient.Builder builder, VectorStore vectorStore) {
        /*this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(
                        vectorStore,
                        SearchRequest.defaults()
                ))
                .build();*/
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    private String prompt = """
            Your task is to answer the questions about Indian Constitution. Use the information from the DOCUMENTS
            section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
                        
            QUESTION:
            {input}
                        
            DOCUMENTS:
            {documents}
                        
            """;

    @GetMapping("/ics")
    public String icsQuestion(@RequestParam String q) {
        return chatClient
                .prompt()
                .user(q)
                .call()
                .content();
    }


    @GetMapping("/ic")
    public String simplifyIC(@RequestParam String q) {

        PromptTemplate template
                = new PromptTemplate(prompt);

        Map<String, Object> promptParams
                = new HashMap<>();

        promptParams.put("input", q);
        promptParams.put("documents", findSimilarData(q));

        return chatClient
                .prompt(template.create(promptParams))
                .call()
                .content();
    }

    private String findSimilarData(String q) {

        List<Document> documents =
                vectorStore.similaritySearch(SearchRequest.query(q).withTopK(5));

        return documents
                .stream()
                .map(document -> document.getContent().toString())
                .collect(Collectors.joining());
    }
}
