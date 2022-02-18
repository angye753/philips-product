package com.waes.phillips.products.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class UpdateProductDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
