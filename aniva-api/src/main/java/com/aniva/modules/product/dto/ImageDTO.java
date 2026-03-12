package com.aniva.modules.product.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import lombok.Data;

@Getter
@Setter
@Data
public class ImageDTO {

    @NotBlank(message = "Image URL is required")
    @Size(max = 1000, message = "Image URL is too long")
    private String imageUrl;

    private Boolean isPrimary;

    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder;
}
