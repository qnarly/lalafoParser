package kg.lalafoparser.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kg.lalafoparser.dto.ItemDto;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public ItemDto mapToItemDto(JsonNode node) {
        String name = node.path("title").asText("Без названия");
        String city = node.path("city").asText("Кыргызстан");
        String createdAt = node.path("created_time").asText("Недавно");

        String priceVal = node.path("price").asText("0");
        String currency = node.path("currency").asText("KGS");
        String price = priceVal.equals("0") ? "Договорная" : priceVal + " " + currency;

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
