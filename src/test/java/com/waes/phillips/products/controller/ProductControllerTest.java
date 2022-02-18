package com.waes.phillips.products.controller;

import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.model.ProductsDTO;
import com.waes.phillips.products.services.ProductService;
import com.waes.phillips.products.services.ProductServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Before
    public void setup() {

        productService = new ProductServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void success_get_products_empty() {

        List<ProductDTO> products = new ArrayList<>();
        ProductsDTO productsDTO = ProductsDTO.builder().bundle(products).build();
        Mockito.when(productService.getProducts(Boolean.FALSE)).thenReturn(productsDTO);
        ResponseEntity responseEntity = productController.getProducts(Boolean.FALSE);

        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.OK));
        Assert.assertTrue(((ProductsDTO)responseEntity.getBody()).getBundle().isEmpty());
    }

    @Test
    public void success_get_products() {
        List<ProductDTO> products = Arrays.asList(ProductDTO.builder().id("123")
                .name("Product").build());
        ProductsDTO productsDTO = ProductsDTO.builder().bundle(products).build();
        Mockito.when(productService.getProducts(Boolean.FALSE)).thenReturn(productsDTO);
        ResponseEntity responseEntity = productController.getProducts(Boolean.FALSE);

        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.OK));

        ProductsDTO productsResult = (ProductsDTO) responseEntity.getBody();
        Assert.assertTrue(!productsResult.getBundle().isEmpty());
        Assert.assertEquals(((ProductDTO)productsResult.getBundle().get(0)).getId(), "123");
    }

    @Test
    public void success_get_product() {

        ProductDTO productDTO = ProductDTO.builder().id("123")
                .name("Product").build();
        Mockito.when(productService.getProduct(ArgumentMatchers.any()))
                .thenReturn(Optional.of(productDTO));
        ResponseEntity responseEntity = productController.getProduct("123");

        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.OK));
        Assert.assertTrue(responseEntity.getBody() instanceof Optional);
        Optional body = (Optional) responseEntity.getBody();
        Assert.assertEquals(((ProductDTO) body.get()).getId(), "123");
    }

    @Test
    public void success_save_product() {

        ProductDTO productDTO = ProductDTO.builder().id("123")
                .name("Product").build();
        Mockito.when(productService.saveProduct(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(productDTO);
        ResponseEntity responseEntity = productController.saveProduct(productDTO, Boolean.FALSE);

        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.CREATED));
        Assert.assertTrue(responseEntity.getBody() instanceof ProductDTO);
        Assert.assertEquals(((ProductDTO) responseEntity.getBody()).getId(), "123");
    }

    @Test
    public void success_delete_product() {

        Mockito.doNothing().when(productService).deleteProduct("123", Boolean.FALSE);
        ResponseEntity responseEntity = productController.deleteProduct("123", Boolean.FALSE);
        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT));
    }
}
