package com.waes.phillips.products.services;

import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.model.ProductsDTO;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface ProductService {

    ProductDTO saveProduct(ProductDTO productDTO, Boolean downstream);
    void deleteProduct(String id, Boolean downstream);
    Optional<ProductDTO> getProduct(String id);
    ProductsDTO getProducts(Boolean downstream);
    Optional<ProductDTO> updateProduct(ProductDTO productDTO, String id, Boolean downstream);
}
