package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private GoogleAnalyticsDataFetcher googleAnalyticsDataFetcher;

    @GetMapping("/")
    public String showForm() {
        return "form";
    }

    @PostMapping("/generate")
    public String generatePersonas(@RequestParam("revenueTarget") String revenueTarget, Model model) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(90);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String analyticsData = googleAnalyticsDataFetcher.fetchData(startDate.format(formatter), endDate.format(formatter));
            String personas = personaService.getPersonas(revenueTarget, analyticsData);
            model.addAttribute("personas", personas);
            model.addAttribute("analyticsData", analyticsData);
            return "result";
        } catch (IllegalStateException e) {
            e.printStackTrace();
            model.addAttribute("error", "Configuration error: " + e.getMessage());
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while communicating with external services: " + e.getMessage());
            return "error";
        }
    }
}
