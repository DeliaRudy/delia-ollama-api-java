package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static String generatePrompt(String revenueTarget, String analyticsData) {
        return "Given the following user data from Google Analytics, and the revenue target of '" + revenueTarget + "', generate 3 customer personas. For each persona, provide a mini description, a fictional story about their possible pain points, and a strategy to reach this persona based on the available data.\n\n" + analyticsData;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java -jar <jar-file-name>.jar \"<revenue_target_or_growth_strategy>\"");
            System.exit(1);
        }
        String revenueTarget = args[0];

        // You would have to  change this to variables
        String modelName = "gemma:2b";

        // Fetch Google Analytics data
        String analyticsData = GoogleAnalyticsDataFetcher.fetchData();

        // Construct the final prompt with the fetched data
        String promptText = generatePrompt(revenueTarget, analyticsData);

        HttpURLConnection conn = null;

        try {
            // Set up the URL and connection
            URL url = new URL("http://localhost:11434/api/generate");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Construct JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", modelName);
            requestBody.put("prompt", promptText);
            requestBody.put("stream", false);

            // Write JSON input to the output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get and print the response code
            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);

            // Read the response body
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            System.out.println("Response Body: " + response.toString());

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            String responseText = jsonResponse.getString("response");

            // Print the formatted response
            System.out.println("--- Generated Customer Personas ---");
            System.out.println(responseText);
            System.out.println("------------------------------------");

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure the connection is disconnected
            if (conn != null) {
                conn.disconnect();
                System.out.println("Connection disconnected.");
            }
        }
    }
}
