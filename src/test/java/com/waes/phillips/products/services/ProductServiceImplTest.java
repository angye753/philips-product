package com.waes.phillips.products.services;

import com.waes.phillips.products.data.Product;
import com.waes.phillips.products.data.repository.ProductRepository;
import com.waes.phillips.products.exception.ProductException;
import com.waes.phillips.products.integration.SupplyChainIntegration;
import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.model.ProductsDTO;
import com.waes.phillips.products.utils.ProductUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplyChainIntegration supplyChainIntegration;

    @InjectMocks
    private ProductServiceImpl productService;

    @Before
    public void setup() {

        productService = new ProductServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void success_save_product() {
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("New productDto")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product product = ProductUtils.parseProductDTOToEntity(productDto);
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        productService.saveProduct(productDto, Boolean.FALSE);

        Mockito.verify(productRepository).save(ArgumentMatchers.any());
    }

    @Test
    public void success_save_product_downstream() {
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("New productDto")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Mockito.when(supplyChainIntegration.saveProduct(ArgumentMatchers.any())).thenReturn(productDto);

        productService.saveProduct(productDto, Boolean.TRUE);

        Mockito.verify(supplyChainIntegration).saveProduct(ArgumentMatchers.any());
        Mockito.verify(productRepository, Mockito.times(0)).save(ArgumentMatchers.any());
    }

    @Test
    public void success_delete_product() {

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("New productDto")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product product = ProductUtils.parseProductDTOToEntity(productDto);
        Mockito.when(productRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(product));

        productService.deleteProduct("123", Boolean.FALSE);

        Mockito.verify(productRepository).delete(ArgumentMatchers.any());
    }

    @Test
    public void success_delete_product_downstream() {

        productService.deleteProduct("123", Boolean.TRUE);

        Mockito.verify(supplyChainIntegration).deleteProduct(ArgumentMatchers.any());
        Mockito.verify(productRepository, Mockito.times(0)).delete(ArgumentMatchers.any());
    }

    @Test
    public void success_get_products() {

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("New productDto")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        ProductDTO productDto2 = ProductDTO.builder()
                .id("456")
                .name("New productDto 2")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product product = ProductUtils.parseProductDTOToEntity(productDto);
        Product product2 = ProductUtils.parseProductDTOToEntity(productDto2);

        Iterable<Product> products = Arrays.asList(product, product2);
        Mockito.when(productRepository.findAll()).thenReturn(products);

        ProductsDTO productsEntity = productService.getProducts(Boolean.FALSE);

        Assert.assertTrue(productsEntity.getBundle().size() == 2);
        Assert.assertTrue(productsEntity.getBundle().get(0).getId() == "123");
        Assert.assertTrue(productsEntity.getBundle().get(1).getId() == "456");
    }

    @Test
    public void success_get_products_downstream() {

        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("New productDto")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        ProductDTO productDto2 = ProductDTO.builder()
                .id("456")
                .name("New productDto 2")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Mockito.when(supplyChainIntegration.getProducts()).thenReturn(ProductsDTO.builder()
                .bundle(Arrays.asList(productDto,productDto2)).build());

        ProductsDTO productsEntity = productService.getProducts(Boolean.TRUE);

        Assert.assertTrue(productsEntity.getBundle().size() == 2);
        Assert.assertTrue(productsEntity.getBundle().get(0).getId() == "123");
        Assert.assertTrue(productsEntity.getBundle().get(1).getId() == "456");
    }

    @Test
    public void success_get_empty_products() {

        Iterable<Product> products = Arrays.asList();
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Assert.assertTrue(productService.getProducts(Boolean.FALSE).getBundle().isEmpty());
    }

    @Test
    public void success_update_product() {
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto updated")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product product = ProductUtils.parseProductDTOToEntity(productDto);
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(productRepository.findById("123")).thenReturn(Optional.of(product));

        Optional<ProductDTO> productDTO = productService.updateProduct(productDto, "123", Boolean.FALSE);

        Mockito.verify(productRepository).save(ArgumentMatchers.any());

        Assert.assertTrue(productDTO.isPresent());
        Assert.assertTrue(productDTO.get().getName().equalsIgnoreCase("ProductDto updated"));
    }

    @Test
    public void success_update_product_downstream() {
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto updated")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Mockito.when(supplyChainIntegration.updateProduct(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Optional.of(productDto));

        Optional<ProductDTO> productDTO = productService.updateProduct(productDto, "123", Boolean.TRUE);

        Mockito.verify(productRepository, Mockito.times(0)).save(ArgumentMatchers.any());
        Mockito.verify(supplyChainIntegration).updateProduct(ArgumentMatchers.any(), ArgumentMatchers.any());

        Assert.assertTrue(productDTO.isPresent());
        Assert.assertTrue(productDTO.get().getName().equalsIgnoreCase("ProductDto updated"));
    }

    @Test
    public void success_no_update_product() {
        ProductDTO productDto = ProductDTO.builder()
                .id("123")
                .name("ProductDto updated")
                .quantity(1)
                .price(BigDecimal.TEN)
                .build();

        Product product = ProductUtils.parseProductDTOToEntity(productDto);
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
        Mockito.when(productRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductException.class, () -> {
            Optional<ProductDTO> productDTO = productService.updateProduct(productDto, "123", Boolean.FALSE);
            Assert.assertTrue(!productDTO.isPresent());
        });
        Mockito.verify(productRepository, Mockito.times(0)).save(ArgumentMatchers.any());
    }

}
