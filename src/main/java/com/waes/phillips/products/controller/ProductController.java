package com.waes.phillips.products.controller;

import com.waes.phillips.products.model.ProductDTO;
import com.waes.phillips.products.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/supply-chain", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    @Autowired
    private ProductService productService;

    @PutMapping(value = "/product",
            produces = { "application/json" },
            consumes = { "application/json" })
    public ResponseEntity saveProduct(@RequestBody ProductDTO body, @RequestParam(value = "downstream",
            required = false, defaultValue = "false") Boolean downstream) {
        return ResponseEntity.created(null).body(productService.saveProduct(body, downstream));
    }

    /**
     * Deletes a {@link ProductDTO}.
     *
     * @param id product id to be deleted
     * @param downstream indicates if the query should be made on an downstream service side
     * @return
     */
    @DeleteMapping({"/product/{id}"})
    public ResponseEntity<Void> deleteProduct(@PathVariable(value = "id") String id, @RequestParam(value = "downstream",
            required = false, defaultValue = "false") Boolean downstream) {
        productService.deleteProduct(id, downstream);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/product/{id}")
    public ResponseEntity getProduct(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity getProducts(@RequestParam(value = "downstream", required = false, defaultValue = "false") Boolean downstream) {
        return ResponseEntity.ok(productService.getProducts(downstream));
    }

    @PostMapping(value = "/product/{id}",
            produces = { "application/json" },
            consumes = { "application/json" })
    public ResponseEntity updateProduct(@PathVariable(value = "id") String id, @RequestParam(value = "downstream",
            required = false, defaultValue = "false") Boolean downstream, @RequestBody ProductDTO body) {
        return ResponseEntity.created(null).body(productService.updateProduct(body, id, downstream));
    }

}
