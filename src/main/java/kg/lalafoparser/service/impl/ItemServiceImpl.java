package kg.lalafoparser.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.lalafoparser.dto.ItemDto;
import kg.lalafoparser.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ObjectMapper objectMapper;

    @Value("${lalafo.url}")
    private String baseUrl;

    @Override
    public List<ItemDto> getItems(Integer limit) {
        List<ItemDto> its = new ArrayList<>();
        int page = 1;

        while (its.size() < limit) {
            try {
                String url = baseUrl + page;

                log.info("API request: {}", url);

                Connection.Response response = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36")
                        .header("Accept", "application/json, text/plain, */*")
                        .header("device", "pc")
                        .header("country-id", "12")
                        .header("language", "ru_RU")
                        .header("user-hash", "6f6f9b09-96cf-488e-a2c5-5e4eac055154")
                        .header("Referer", "https://lalafo.kg/kyrgyzstan/zapchasti-i-aksessuary/")
                        .header("Origin", "https://lalafo.kg")
                        .method(Connection.Method.GET)
                        .timeout(10000)
                        .execute();

                if (response.statusCode() != 200) {
                    log.error("API error! Code: {}, Body: {}", response.statusCode(), response.body());
                    break;
                }

                JsonNode root = objectMapper.readTree(response.body());
                JsonNode items = root.path("items");

                if (items.isMissingNode() || items.isEmpty()) {
                    log.info("API returned empty list {}", page);
                    break;
                }

                for (JsonNode node : items) {
                    if (its.size() >= limit) break;

                    if (!node.has("id")) continue;

                    try {
                        its.add(mapToItemDto(node));
                    } catch (Exception e) {
                        log.error("Mapping error: {}", e.getMessage());
                    }
                }
                page++;
                Thread.sleep(500);

            } catch (Exception e) {
                log.error("Critical API error: {}", e.getMessage());
                break;
            }
        }
        return its;
    }

    private ItemDto mapToItemDto(JsonNode node) {
        String name = node.path("title").asText("Без названия");
        String priceVal = node.path("price").asText("0");
        String currency = node.path("currency").asText("KGS");
        String price = priceVal.equals("0") ? "Договорная" : priceVal + " " + currency;

        String city = node.path("city").asText("Кыргызстан");
        String createdAt = node.path("created_time").asText("Недавно");

        String photoUrl = "https://placehold.co/400x300?text=No+Image";
        JsonNode images = node.path("images");

        if (images.isArray() && !images.isEmpty()) {
            String urlVal = images.get(0).path("original_url").asText();
            if (urlVal.startsWith("http")) {
                photoUrl = urlVal;
            } else if (!urlVal.isEmpty()) {
                photoUrl = "https://img.lalafo.com/i/" + urlVal;
            }
        }

        return ItemDto.builder()
                .name(name)
                .price(price)
                .city(city)
                .photoUrl(photoUrl)
                .createdAt(createdAt)
                .build();
    }
}
