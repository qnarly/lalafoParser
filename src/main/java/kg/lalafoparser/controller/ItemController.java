package kg.lalafoparser.controller;

import kg.lalafoparser.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/test-result")
    public String getTestResultPage(Model model) {
        model.addAttribute("items", itemService.getItems(100));
        return "test-result";
    }
}
