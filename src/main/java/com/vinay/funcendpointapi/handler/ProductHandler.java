package com.vinay.funcendpointapi.handler;

import com.vinay.funcendpointapi.model.Product;
import com.vinay.funcendpointapi.repository.ProductRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Component
public class ProductHandler {

    @Autowired
    ProductRespository repo;

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        Flux<Product> products = repo.findAll();

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products, Product.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> product = repo.findById(id);

        return product.flatMap(prod -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(prod))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(product -> ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(repo.save(product), Product.class));
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> updateProdMono = request.bodyToMono(Product.class);
        Mono<Product> existProdMono = repo.findById(id);

        updateProdMono.zipWith(existProdMono, (prod, existProd) -> {
            Product updProd = new Product();
            updProd = prod;
            updProd.setId(id);
            return updProd;
        })
        .flatMap(product -> ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(repo.save(product), Product.class))
        .switchIfEmpty(ServerResponse.notFound().build());

        return updateProdMono.flatMap(product -> {
            product.setId(id);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(repo.save(product), Product.class);
        });
    }

    public Mono<ServerResponse> deleteProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Product> productMono = repo.findById(id);

        return productMono.flatMap(product -> ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(repo.delete(product), Product.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
