package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {
        // Variables for the model and prompt
        String modelName = "gemma:2b";
        String promptText = "What is cheese made of?";

        // Declare connection outside the try block to ensure we can disconnect it later
        HttpURLConnection conn = null;

        try {
            // Set up the URL and connection
            URL url = new URL("http://localhost:11434/api/generate");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // JSON body using variables
            String jsonInputString = String.format(
                    "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                    modelName,
                    promptText
            );

            // Write the JSON input to the output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
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

            // Print the response
            System.out.println("Response Body: " + response.toString());

            // Parse the JSON response and print the "response" field
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