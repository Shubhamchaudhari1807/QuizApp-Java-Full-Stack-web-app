package com.genie.quiz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.genie.quiz.dto.OptionDto;
import com.genie.quiz.dto.QuestionDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.model.name}")
    private String modelName;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<QuestionDto> generateQuestions(String topic, int count) throws Exception {
        String prompt = "Create " + count + " different multiple choice quiz questions about '" + topic +
                "'. Each question must have exactly 4 options, with one option marked correct. " +
                "Return ONLY a valid JSON array of objects with this exact structure: " +
                "[{\"questionText\": \"Your question?\", \"options\": [{\"optionText\": \"Option A\", \"correct\": false}, ...]}, ...]";

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode contentItem = contents.addObject();
        ArrayNode parts = contentItem.putArray("parts");
        ObjectNode part = parts.addObject();
        part.put("text", prompt);
        String requestBody = objectMapper.writeValueAsString(root);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to call Gemini API: " + response.body());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        String jsonText = rootNode.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();

        if (jsonText == null || jsonText.isBlank()) {
            throw new RuntimeException("Gemini returned empty text.");
        }

        jsonText = jsonText.replace("```json", "").replace("```", "").trim();
        if (!jsonText.startsWith("[")) {
            int start = jsonText.indexOf("[");
            int end = jsonText.lastIndexOf("]") + 1;
            if (start >= 0 && end > start) jsonText = jsonText.substring(start, end);
            else throw new RuntimeException("No valid JSON array in AI response.");
        }

        List<QuestionDto> questions = new ArrayList<>();
        ArrayNode arrayNode = (ArrayNode) objectMapper.readTree(jsonText);
        for (JsonNode qNode : arrayNode) {
            questions.add(parseJsonToQuestionDto(qNode.toString()));
        }
        return questions;
    }

    private QuestionDto parseJsonToQuestionDto(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText(root.get("questionText").asText());
        List<OptionDto> options = new ArrayList<>();
        for (JsonNode optionNode : root.get("options")) {
            OptionDto optionDto = new OptionDto();
            optionDto.setOptionText(optionNode.get("optionText").asText());
            optionDto.setCorrect(optionNode.get("correct").asBoolean());
            options.add(optionDto);
        }
        questionDto.setOptions(options);
        return questionDto;
    }
}