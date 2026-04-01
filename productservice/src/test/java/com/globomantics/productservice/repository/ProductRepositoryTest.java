package com.globomantics.productservice.repository;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.globomantics.productservice.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, DBUnitExtension.class})
@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTest {

  @Autowired private DataSource dataSource;

  @Autowired private ProductRepository repository;

  public ConnectionHolder getConnectionHolder() {
    // Return a function that retrieves a connection from our data source
    return () -> dataSource.getConnection();
  }

  @Test
  @DataSet("products.yml")
  void testFindAll() {
    List<Product> products = repository.findAll();
    assertEquals(2, products.size(), "We should have 2 products in our database");
  }

  @Test
  @DataSet("products.yml")
  void testFindByIdSuccess() {
    // Find the product with ID 200
    Optional<Product> product = repository.findById(200);

    // Validate that we found it
    assertTrue(product.isPresent(), "Product with ID 200 should exist");

    // Validate the product values
    Product foundProduct = product.get();
    assertEquals(200, foundProduct.getId().intValue(), "Product ID should be 200");
    assertEquals("Product 2", foundProduct.getName(), "Product name should be Product 2");
    assertEquals(5, foundProduct.getQuantity().intValue(), "Product quantity should be 5");
    assertEquals(2, foundProduct.getVersion().intValue(), "Product version should be 2");
  }

  @Test
  @DataSet("products.yml")
  void testFindByIdNotFound() {
    // Find the product with ID 999
    Optional<Product> product = repository.findById(999);

    // Validate that we didn't find it
    assertFalse(product.isPresent(), "Product with ID 999 should not exist");
  }
}