package kg.lalafoparser.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.lalafoparser.dto.ItemDto;
import kg.lalafoparser.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
        Integer page = 1;

        while (its.size() < limit) {
            try {
                String url = baseUrl + "?page=" + page;
                Document doc = Jsoup.connect(url).get();

                Element script = doc.getElementById("__NEXT_DATA__");
                JsonNode root = objectMapper.readTree(script.html());
                JsonNode items = root.get("props").get("pageProps").get("initialState").get("feed").get("items");

                for (JsonNode item : items) {
                    if (its.size() >= limit) break;

                    its.add(mapToItemDto(item));
                }
                page++;

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return its;
    }

    private ItemDto mapToItemDto(JsonNode node) {
        String photo = node.get("images").get(0).get("original_url").asText();

        String city = node.get("city").asText();

        return ItemDto.builder()
                .name(node.get("title").asText())
                .price(node.get("price").asText())
                .city(city)
                .photoUrl(photo)
                .createdAt(node.get("created_time").asText())
                .build();
    }
}
