package com.waes.phillips.products.services;

import com.waes.phillips.products.exception.ProductException;
import com.waes.phillips.products.integration.SupplyChainIntegration;
import com.waes.phillips.products.integration.impl.SupplyChainIntegrationImpl;
import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.utils.HttpUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SupplyChainIntegrationImplTest {

    @Mock
    private HttpUtils httpUtils;

    @InjectMocks
    private SupplyChainIntegration supplyChainIntegration;

    private String url = "http://localhost/resource";

    @Before
    public void setUp() {
        supplyChainIntegration = new SupplyChainIntegrationImpl("http://localhost", "/resource", httpUtils);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void success_save_product(){
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto updated")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Mockito.when(httpUtils.executeRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        supplyChainIntegration.saveProduct(productDto);

        Mockito.verify(httpUtils, Mockito.times(1))
                .executePostRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void success_get_products(){

        Mockito.when(httpUtils.executeRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        supplyChainIntegration.getProducts();

        Mockito.verify(httpUtils, Mockito.times(1))
                .executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void success_get_product(){

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto updated")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        Mockito.when(httpUtils.executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(productDto);

        supplyChainIntegration.getProduct("123");

        Mockito.verify(httpUtils, Mockito.times(1))
                .executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void success_delete_product(){

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto to delete")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        Mockito.when(httpUtils.executeRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);
        Mockito.when(httpUtils.executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(productDto);

        supplyChainIntegration.deleteProduct("123");

        Mockito.verify(httpUtils, Mockito.times(1))
                .executeDeleteRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void success_delete_product_no_found(){

        Mockito.when(httpUtils.executeRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        Assertions.assertThrows(ProductException.class, () -> {
            supplyChainIntegration.deleteProduct("123");
        });

        Mockito.verify(httpUtils, Mockito.times(0))
                .executeDeleteRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void success_update_product(){

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto to update")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();
        Mockito.when(httpUtils.executePostRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(productDto);
        Mockito.when(httpUtils.executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(productDto);

        supplyChainIntegration.updateProduct(productDto, "123");

        Mockito.verify(httpUtils, Mockito.times(1))
                .executePostRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void no_found_update_product(){

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto to update")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Mockito.when(httpUtils.executeGetRequest(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(null);

        Assertions.assertThrows(ProductException.class, () -> {
            supplyChainIntegration.updateProduct(productDto, "123");
        });

        Mockito.verify(httpUtils, Mockito.times(0))
                .executePostRequest(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
