package com.waes.phillips.products.utils;

import com.waes.phillips.products.data.Product;
import com.waes.phillips.products.model.ProductDTO;

public class ProductUtils {

    public static ProductDTO parseProductEntityToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity()).build();
    }

    public static Product parseProductDTOToEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity()).build();
    }


}
