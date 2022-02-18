package com.waes.phillips.products.services;

import com.waes.phillips.products.exception.ProductException;
import com.waes.phillips.products.integration.SupplyChainIntegration;
import com.waes.phillips.products.model.ProductsDTO;
import com.waes.phillips.products.utils.ProductUtils;
import com.waes.phillips.products.data.Product;
import com.waes.phillips.products.data.repository.ProductRepository;
import com.waes.phillips.products.model.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplyChainIntegration supplyChainIntegration;

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO, Boolean downstream) {

        try {
            if (downstream) {
                return supplyChainIntegration.saveProduct(productDTO);
            }
            Product product = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name(productDTO.getName())
                    .quantity(productDTO.getQuantity())
                    .price(productDTO.getPrice())
                    .build();

            Product newProduct = productRepository.save(product);
            log.info(String.format("product %s was saved successfully", product.getName()));

            return ProductUtils.parseProductEntityToDTO(newProduct);
        } catch (Exception e) {
            log.error(String.format("Error saving product %s", productDTO.getName()));
            throw new ProductException(String.format("Error saving product %s", productDTO.getName()));
        }
    }

    @Override
    public void deleteProduct(String id, Boolean downstream) {
        try {
            if (downstream) {
                supplyChainIntegration.deleteProduct(id);
            } else {
                Optional<Product> product = productRepository.findById(id);

                product.ifPresent(pr -> productRepository.delete(product.get()));
            }
        } catch (Exception e) {
            log.error(String.format("Error deleting productId %s", id));
            throw new ProductException(String.format("Error deleting productId %s", id));
        }
    }

    @Override
    public Optional<ProductDTO> getProduct(String id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            return product.isPresent() ? Optional.of(ProductUtils.parseProductEntityToDTO(product.get()))
                    : Optional.empty();
        } catch (Exception e) {
            log.error(String.format("Error getting productId %s", id));
            throw new ProductException(String.format("Error getting productId %s", id));
        }
    }

    @Override
    public ProductsDTO getProducts(Boolean downstream) {
        try {
            if (downstream) {
                return supplyChainIntegration.getProducts();
            }

            Iterable<Product> all = productRepository.findAll();

            return Optional.ofNullable(all)
                    .map(pr -> ProductsDTO.builder().bundle(Arrays.asList()).build())
                    .orElse(buildProductsDTO((List<Product>) all));

        } catch (Exception e) {
            log.error("Error getting all products");
            throw new ProductException("Error getting products");
        }

    }

    private ProductsDTO buildProductsDTO(List<Product> products) {
        return ProductsDTO.builder().bundle(products.stream()
                .map(product -> ProductUtils.parseProductEntityToDTO(product))
                .collect(Collectors.toList())).build();
    }

    @Override
    public Optional<ProductDTO> updateProduct(ProductDTO productDTO, String id, Boolean downstream) {

        if (downstream) {
            return supplyChainIntegration.updateProduct(productDTO, id);
        }
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            Product productToSave = Product.builder()
                    .id(id)
                    .name(productDTO.getName())
                    .quantity(productDTO.getQuantity())
                    .price(productDTO.getPrice())
                    .build();
            Product productSaved = productRepository.save(productToSave);

            return Optional.of(ProductUtils.parseProductEntityToDTO(productSaved));
        } else {
            String msj = String.format("Product with id %s wasn't found", id);
            log.info(msj);
            throw new ProductException(msj);
        }
    }
}
