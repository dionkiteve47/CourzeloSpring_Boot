package tn.esprit.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tn.esprit.user.gpt.ChatGPTRequest;
import tn.esprit.user.gpt.ChatGptResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bott")
public class CustomController {


    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;
    @Autowired
    private RestTemplate template;
    @GetMapping("/chat")

    public ResponseEntity<?> chat(@RequestParam("prompt") String prompt) {
        try {
            ChatGPTRequest request = new ChatGPTRequest(model, prompt);
            ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);

            // Create a JSON object to hold the hint
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("hint", chatGptResponse.getChoices().get(0).getMessage().getContent());

            // Return the JSON object with HTTP status 200 OK
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            // If an error occurs, return an error message with HTTP status 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
        }
    }
}