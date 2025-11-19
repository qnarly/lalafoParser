package kg.lalafoparser.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String photoUrl;
    private String name;
    private String price;
    private String city;
    private String createdAt;
}
