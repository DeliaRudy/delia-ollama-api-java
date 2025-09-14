package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testGeneratePrompt() {
        String revenueTarget = "Increase sales by 10%";
        String analyticsData = "Users: 1000\nCountry: USA";
        String expectedPrompt = "Given the following user data from Google Analytics, and the revenue target of 'Increase sales by 10%', generate 3 customer personas. For each persona, provide a mini description, a fictional story about their possible pain points, and a strategy to reach this persona based on the available data.\n\nUsers: 1000\nCountry: USA";

        String actualPrompt = Main.generatePrompt(revenueTarget, analyticsData);

        assertEquals(expectedPrompt, actualPrompt);
    }
}
