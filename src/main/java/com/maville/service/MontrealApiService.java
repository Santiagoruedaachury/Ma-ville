package com.maville.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MontrealApiService {

    private static final String BASE_URL = "https://donnees.montreal.ca/api/3/action/datastore_search";

    @Value("${montreal.api.travaux-resource-id:cc41b532-f12d-40fb-9f55-eb58c9a2b12b}")
    private String travauxResourceId;

    @Value("${montreal.api.entraves-resource-id:a2bc8014-488c-495d-941b-e7ae1999d1bd}")
    private String entravesResourceId;

    private final RestTemplate restTemplate;

    public MontrealApiService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> fetchTravaux(String quartier, int limit) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("resource_id", travauxResourceId)
                .queryParam("limit", limit);

        if (quartier != null && !quartier.isBlank()) {
            builder.queryParam("q", quartier);
        }

        JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);
        return parseResponse(response);
    }

    public Map<String, Object> fetchEntraves(String rue, int limit) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("resource_id", entravesResourceId)
                .queryParam("limit", limit);

        if (rue != null && !rue.isBlank()) {
            builder.queryParam("q", rue);
        }

        JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);
        return parseResponse(response);
    }

    private Map<String, Object> parseResponse(JsonNode response) {
        Map<String, Object> result = new HashMap<>();
        if (response != null && response.has("result")) {
            JsonNode resultNode = response.get("result");
            result.put("total", resultNode.has("total") ? resultNode.get("total").asInt() : 0);

            List<Map<String, Object>> records = new ArrayList<>();
            if (resultNode.has("records")) {
                for (JsonNode record : resultNode.get("records")) {
                    Map<String, Object> map = new HashMap<>();
                    record.fields().forEachRemaining(entry -> map.put(entry.getKey(), entry.getValue().asText()));
                    records.add(map);
                }
            }
            result.put("records", records);
        }
        return result;
    }
}
