package com.milczarek.joboffers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class JobController {

    private final ObjectMapper objectMapper;


    public JobController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api")
    public Map<String, Integer> get() throws IOException {
        Map<String, Integer> skillCount = new HashMap<>();
        //String jobOffers = Files.readString(Path.of("src/main/resources/jobs.json"));
        Document document = Jsoup.connect("https://justjoin.it/all-locations/java").get();
        Element first = document.select("script[type=application/json]").first();

        JsonNode jsonParent = objectMapper.readTree(first.data());

        List<JsonNode> jsonNodes = jsonParent.findValues("requiredSkills");

        for (JsonNode jsonNode : jsonNodes) {
            for (JsonNode node : jsonNode) {
                String skillName = node.asText();
                if (skillName.equalsIgnoreCase("Spring") || skillName.equalsIgnoreCase("Spring Boot")) {
                    skillName = "Spring / Spring Boot";
                }
                if (skillName.contains("Java")) {
                    skillName = "Java";
                }
                skillCount.put(skillName, skillCount.getOrDefault(skillName, 0) + 1);
            }
        }
        return skillCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= 10)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
