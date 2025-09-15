package org.example;

import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VertexAiClient {

    public String generate(String projectId, String location, String modelId, String prompt) throws IOException {
        String apiEndpoint = String.format("%s-aiplatform.googleapis.com:443", location);
        PredictionServiceSettings predictionServiceSettings =
                PredictionServiceSettings.newBuilder().setEndpoint(apiEndpoint).build();

        try (PredictionServiceClient predictionServiceClient =
                     PredictionServiceClient.create(predictionServiceSettings)) {
            final EndpointName endpointName =
                    EndpointName.ofProjectLocationPublisherModelName(projectId, location, "google", modelId);

            JSONObject instanceJson = new JSONObject();
            instanceJson.put("prompt", prompt);

            Value.Builder instanceBuilder = Value.newBuilder();
            JsonFormat.parser().merge(instanceJson.toString(), instanceBuilder);
            List<Value> instances = new ArrayList<>();
            instances.add(instanceBuilder.build());

            PredictResponse predictResponse = predictionServiceClient.predict(endpointName, instances, Value.newBuilder().build());
            return predictResponse.getPredictions(0).getStringValue();
        }
    }
}
