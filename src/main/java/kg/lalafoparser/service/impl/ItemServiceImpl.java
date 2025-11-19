package kg.lalafoparser.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import kg.lalafoparser.dto.ItemDto;
import kg.lalafoparser.mapper.ItemMapper;
import kg.lalafoparser.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final RestClient lalafoRestClient;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItems(Integer limit) {
        List<ItemDto> resultItems = new ArrayList<>();
        int page = 1;

        while (resultItems.size() < limit) {
            try {
                log.info("Page request: {}", page);

                int finalPage = page;
                JsonNode root = lalafoRestClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("expand", "url")
                                .queryParam("per-page", 40)
                                .queryParam("sort_by", "newest")
                                .queryParam("page", finalPage)
                                .build())
                        .retrieve()
                        .body(JsonNode.class);

                if (root == null) break;

                JsonNode itemsNode = root.path("items");

                if (itemsNode.isMissingNode() || itemsNode.isEmpty()) {
                    log.info("Page list is empty {}", page);
                    break;
                }

                for (JsonNode node : itemsNode) {
                    if (resultItems.size() >= limit) break;

                    if (!node.has("id")) continue;

                    resultItems.add(itemMapper.mapToItemDto(node));
                }

                page++;
                Thread.sleep(500);

            } catch (Exception e) {
                log.error("Parsing error {}: {}", page, e.getMessage());
                break;
            }
        }
        return resultItems;
    }
}
