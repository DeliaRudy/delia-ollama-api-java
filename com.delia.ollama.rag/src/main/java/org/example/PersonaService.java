package org.example;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class PersonaService {

    @Value("${vertex.ai.project.id}")
    private String projectId;

    @Value("${vertex.ai.location}")
    private String location;

    @Value("${vertex.ai.model.id}")
    private String modelId;

    @Autowired
    private GoogleAnalyticsDataFetcher googleAnalyticsDataFetcher;

    @Autowired
    private VertexAiClient vertexAiClient;

    public String generatePrompt(String revenueTarget, String analyticsData) {
        return "Given the following user data from Google Analytics, and the revenue target of '" + revenueTarget + "', generate 3 customer personas. For each persona, provide a mini description, a fictional story about their possible pain points, and a strategy to reach this persona based on the available data.\n\n" + analyticsData;
    }

    public String getPersonas(String revenueTarget, String analyticsData) throws IOException {
        String promptText = generatePrompt(revenueTarget, analyticsData);
        return vertexAiClient.generate(projectId, location, modelId, promptText);
    }
}
