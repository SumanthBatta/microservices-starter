package com.sum.product.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.data.mongodb.uri=mongodb://localhost:27017/test-product-service"
})
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
		// Simple context loading test
	}

}