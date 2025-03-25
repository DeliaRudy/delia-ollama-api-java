package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {
        // You would have to  change this to variables
        String modelName = "gemma:2b";
        String basePrompt = "Analyze the following energy consumption data and provide insights:\n\n";

        // Create JSON array for "data"
        JSONArray dataArray = new JSONArray();

        // Make this a database query resultset
        dataArray.put(new JSONObject()
                .put("Organization", "ABC")
                .put("Measure", "Generator Electricity")
                .put("Period", "Sep-24")
                .put("Quantity", 13000)
                .put("Unit of Measurement", "Kilowatt Hour"));

        dataArray.put(new JSONObject()
                .put("Organization", "ABC")
                .put("Measure", "Solar Energy")
                .put("Period", "Sep-24")
                .put("Quantity", 2700)
                .put("Unit of Measurement", "Kilowatt Hour"));

        dataArray.put(new JSONObject()
                .put("Organization", "ABC")
                .put("Measure", "Metered Electricity")
                .put("Period", "Sep-24")
                .put("Quantity", 800)
                .put("Unit of Measurement", "Kilowatt Hour"));

        // Convert JSON data to a formatted string
        String jsonDataString = dataArray.toString(2);  // Pretty-print JSON

        // Construct the final prompt with JSON included
        String promptText = basePrompt + jsonDataString;

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
            System.out.println("Response: " + responseText);

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
