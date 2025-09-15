package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class PersonaServiceTest {

    @InjectMocks
    private PersonaService personaService;

    @Mock
    private VertexAiClient vertexAiClient;

    @Mock
    private GoogleAnalyticsDataFetcher googleAnalyticsDataFetcher;

    @Test
    public void testGeneratePrompt() {
        String revenueTarget = "Increase sales by 10%";
        String analyticsData = "Users: 1000\nCountry: USA";
        String expectedPrompt = "Given the following user data from Google Analytics, and the revenue target of 'Increase sales by 10%', generate 3 customer personas. For each persona, provide a mini description, a fictional story about their possible pain points, and a strategy to reach this persona based on the available data.\n\n" + analyticsData;

        String actualPrompt = personaService.generatePrompt(revenueTarget, analyticsData);

        assertEquals(expectedPrompt, actualPrompt);
    }

    @Test
    public void testGetPersonas() throws IOException {
        String revenueTarget = "Increase sales by 10%";
        String analyticsData = "Users: 1000\nCountry: USA";
        String expectedPersonas = "Generated personas";
        String prompt = personaService.generatePrompt(revenueTarget, analyticsData);

        when(vertexAiClient.generate(anyString(), anyString(), anyString(), eq(prompt))).thenReturn(expectedPersonas);

        String actualPersonas = personaService.getPersonas(revenueTarget, analyticsData);

        assertEquals(expectedPersonas, actualPersonas);
    }
}
