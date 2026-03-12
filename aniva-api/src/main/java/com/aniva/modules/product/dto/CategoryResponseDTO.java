package com.aniva.modules.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private Long id;

    private String name;

    private String slug;

    private String description;
}