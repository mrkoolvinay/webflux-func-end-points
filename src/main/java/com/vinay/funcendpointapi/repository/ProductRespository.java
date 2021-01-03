package com.vinay.funcendpointapi.repository;

import com.vinay.funcendpointapi.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRespository extends ReactiveMongoRepository<Product, String> {
}
