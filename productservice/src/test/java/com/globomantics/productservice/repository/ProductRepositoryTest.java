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

  @Test
  @DataSet("products.yml")
  void testSave() {
    // Create a new product and save it to the database
    Product product = new Product("Product 3", 10);
    product.setVersion(1);
    Product savedProduct = repository.save(product);

    // Validate the saved product
    assertNotNull(savedProduct, "Saved product should not be null");
    assertEquals("Product 3", savedProduct.getName(), "Saved product name should match");
    assertEquals(10, savedProduct.getQuantity(), "Saved product quantity should match");

    // Validate that we can get it back out of the database
    Optional<Product> foundProduct = repository.findById(savedProduct.getId());
    assertTrue(foundProduct.isPresent(), "Product should exist in the database");
    assertEquals("Product 3", foundProduct.get().getName(), "Product name does not match");
    assertEquals(10, foundProduct.get().getQuantity(), "Product quantity does not match");
    assertEquals(1, foundProduct.get().getVersion(), "Product version is incorrect");
  }

  @Test
  @DataSet("products.yml")
  void testUpdateSuccess() {
    // Update product 200
    Product product = repository.findById(200).get();
    product.setName("Product 2 Updated");
    product.setQuantity(10);
    product.setVersion(5);
    boolean result = repository.update(product);

    // Validate the update operation
    assertTrue(result, "Update operation should return true for successful update");

    // Validate that the product was updated in the database
    Optional<Product> updatedProduct = repository.findById(200);
    assertTrue(updatedProduct.isPresent(), "Updated product should exist in the database");
    assertEquals("Product 2 Updated", updatedProduct.get().getName(), "Product name does not match");
    assertEquals(10, updatedProduct.get().getQuantity(), "Product quantity should now be 10");
    assertEquals(5, updatedProduct.get().getVersion(), "Product version should now be 5");
  }

  @Test
  @DataSet("products.yml")
  void testUpdateFailure() {
    // Update product 300's name, quantity, and version (not in the database)
    Product product = new Product(300, "Product 3 Updated", 10, 10);
    boolean result = repository.update(product);

    // Validate the update operation
    assertFalse(result, "Update operation should return false for failed update");
  }
}