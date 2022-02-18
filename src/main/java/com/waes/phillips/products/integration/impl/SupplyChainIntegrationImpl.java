package com.waes.phillips.products.integration.impl;

import com.waes.phillips.products.exception.ProductException;
import com.waes.phillips.products.integration.SupplyChainIntegration;
import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.model.ProductsDTO;
import com.waes.phillips.products.utils.HttpUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class SupplyChainIntegrationImpl implements SupplyChainIntegration {

    private final String supplyChainIntegrationBasePath;
    private final String supplyChainIntegrationResourcesPath;
    private final String url;

    private HttpUtils httpUtils;

    public SupplyChainIntegrationImpl(@Value("${supply.chain.url}") String supplyChainPath,
                                      @Value("${supply.chain.resource.path}") String supplyChainResourcePath,
                                      HttpUtils httpUtils) {
        this.supplyChainIntegrationBasePath = supplyChainPath;
        this.supplyChainIntegrationResourcesPath = supplyChainResourcePath;
        this.url = supplyChainPath.concat(supplyChainResourcePath);
        this.httpUtils = httpUtils;
    }

    @Override
    @CircuitBreaker(name = "cd", fallbackMethod = "supplyChainFallback")
    public ProductsDTO getProducts() {
        log.info("Getting All Products from Supply Chain Integration");
        return httpUtils.executeGetRequest(url, ProductsDTO.class);
    }

    @Override
    public Optional<ProductDTO> updateProduct(ProductDTO productDTO, String id) {
        log.info(String.format("Updating Product with data %s and id %s on Supply Chain Integration.", productDTO, id));
        validateProductId(id);
        return Optional.of(httpUtils.executePostRequest(url.concat("/").concat(id), productDTO, ProductDTO.class));
    }

    @Override
    public Optional<ProductDTO> getProduct (String productId) {
        log.info(String.format("Getting Product with id %s from Supply Chain Integration.", productId));
        return Optional.of(httpUtils.executeGetRequest(url.concat("/").concat(productId), ProductDTO.class));
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        log.info("Creating Product {} on Supply Chain Integration.", productDTO);
        return httpUtils.executePostRequest(url, productDTO, ProductDTO.class);
    }

    @Override
    public void deleteProduct(String productId) {
        log.info("Deleting product with id {} from Supply Chain Integration.", productId);
        validateProductId(productId);
        httpUtils.executeDeleteRequest(url.concat("/").concat(productId), ProductDTO.class);
    }

    private void validateProductId(String productId) {
        try {
            getProduct(productId);
        } catch (Exception e) {
            throw new ProductException(String.format("We could not find a Product with  Id %s", productId));
        }
    }

    public ProductsDTO supplyChainFallback(Exception e) {
        return ProductsDTO.builder()
                .bundle(Arrays.asList(ProductDTO.builder().name("angye").build())).build();
    }
}
