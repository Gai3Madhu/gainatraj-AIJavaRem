package com.gai3madhu.guardrails;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BedrockFixerService {
    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();

    public BedrockFixerService() {
        String key = System.getenv("GOOGLE_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("GOOGLE_API_KEY environment variable not set");
        }
        apiKey = key.trim();
    }

    public String fixCode(FlawChecker flaw, String badCode) {
        try {
            String prompt = "IBMi expert. Flaw:" + flaw.getFlawName() + " Fix:" + flaw.fixPrompt() + " Code:" + badCode;
            String json = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"","'").replace("\n"," ") + "\"}]}]}";
            /** Why gemini-3.5-flash-lite worked and flash-latest didn't?
               flash-latest points to the heavy 2.5 Pro which is hammered right now - 503. 3.5-flash-lite is the new light model Google made exactly for AQ keys on free tier - less traffic, faster. **/
            HttpRequest req = HttpRequest.newBuilder()
                    //.uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"))
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent"))
                  //  .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"))
                    .header("Content-Type","application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + res.statusCode());
            return res.body();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}