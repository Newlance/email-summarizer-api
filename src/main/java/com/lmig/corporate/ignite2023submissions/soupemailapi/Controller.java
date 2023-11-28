package com.lmig.corporate.ignite2023submissions.soupemailapi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmig.corporate.ignite2023submissions.entities.SummaryRequest;
import com.lmig.corporate.ignite2023submissions.entities.ChatResponse;
import com.lmig.corporate.ignite2023submissions.entities.AcronymRequest;

@RestController
@CrossOrigin("*")
public class Controller {

  @Value("${app.client.id}")
  private String clientId;

  @Value("${app.client.secret}")
  private String clientSecret;

  @Autowired
  private WebClient tokenClient;
  private WebClient lmGPTClient;

  @GetMapping("/token")
  public String token() {
    return this.getToken();
  }

  private String getToken() {
    return this.tokenClient.post()
        .uri(UriComponentsBuilder
            .fromUriString("https://login.microsoftonline.com/08a83339-90e7-49bf-9075-957ccd561bf1/oauth2/v2.0/token")
            .build().toUri())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(BodyInserters.fromFormData("grant_type", "client_credentials")
            .with("client_id", clientId)
            .with("client_secret", clientSecret)
            .with("scope", "https://cognitiveservices.azure.com/.default"))
        .retrieve()
        .bodyToMono(Token.class)
        .block().getAccess_token();
  }

  @PostMapping("/summarize")
  public ChatResponse summarize(@RequestBody SummaryRequest req) {

    return this.getSummary(req.prompt, req.format, req.conciceness);
  }

  @PostMapping("/acronyms")
  public ChatResponse acronyms(@RequestBody AcronymRequest req) {

    return this.getAcronyms(req.prompt);
  }

  private ChatResponse getSummary(String message, String format, String conciceness) {

    if (format == null) {
      format = "summarize";
    }
    if (conciceness == null) {
      conciceness = "average";
    }

    String prompt = "With " + conciceness + "detail " + format + " the following message(s): " + message;
    return queryGPT(prompt);       

  }

  private ChatResponse getAcronyms(String message) {

    String prompt = "I am about to send you a message. I need you to identify anything that could be an acronym or abbreviation in the message and return them as an alphabetized comma delimited list. It is important that you not include any other text in the response. the only text in your response must be the comma delimited list, as it will be consumed by a service. You may ignore excessively long strings which could be URLs, GUIDs etc. The message is: " + message;
    return queryGPT(prompt);

  }

  private ChatResponse queryGPT(String prompt) {

    String uri = "https://lm-cf-openai-api-tst01.apim.lmig.com/ignitegpt/openai/deployments/gpt-35-turbo/chat/completions?api-version=2023-08-01-preview";

    // create headers
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + getToken());

    // create request body
    String jsonBody = "{" +
        "    \"messages\": [" +
        "        {\"content\": \""+ prompt +"\", \"role\": \"user\"}" +
        "    ]," +
        "    \"temperature\": 1, \"user\": \"" + clientId + "\"" +
        "}";
    HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
    // create RestTemplate instance
    RestTemplate restTemplate = new RestTemplate();
    // send POST request
    ChatResponse result = restTemplate.postForObject(uri, request, ChatResponse.class);    
    return result;

  }
}
