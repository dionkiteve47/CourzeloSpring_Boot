package tn.esprit.user.services.Implementations;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.user.gpt.ChatGPTRequest;
import tn.esprit.user.gpt.ChatGptResponse;


@Service
public class ChatGPTService {

    public  RestTemplate template;

    public String chat(String prompt) {
        try {
            ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo", prompt); // Assuming model is defined elsewhere
            ChatGptResponse chatGptResponse = template.postForObject("https://api.openai.com/v1/chat/completions", request, ChatGptResponse.class);
            return chatGptResponse.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {

            return null;
        }
    }
}