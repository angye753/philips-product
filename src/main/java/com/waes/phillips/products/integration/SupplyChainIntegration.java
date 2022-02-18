package com.waes.phillips.products.integration;

import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.model.ProductsDTO;
import java.util.Optional;

public interface SupplyChainIntegration {

    ProductDTO saveProduct(ProductDTO productDTO);
    void deleteProduct(String id);
    Optional<ProductDTO> getProduct(String id);
    ProductsDTO getProducts();
    Optional<ProductDTO> updateProduct(ProductDTO productDTO, String id);
}
