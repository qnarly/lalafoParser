package kg.lalafoparser.service;

import kg.lalafoparser.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Integer limit);
}
