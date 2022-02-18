package com.waes.phillips.products.data.repository;

import com.waes.phillips.products.data.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String>  {

}
