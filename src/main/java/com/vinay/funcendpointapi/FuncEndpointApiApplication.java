package com.vinay.funcendpointapi;

import com.vinay.funcendpointapi.handler.ProductHandler;
import com.vinay.funcendpointapi.model.Product;
import com.vinay.funcendpointapi.repository.ProductRespository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class FuncEndpointApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FuncEndpointApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRespository repo) {
		return args -> {
			Flux<Product> initProds = Flux.just(new Product(null, "Capuchino", 2.99),
					new Product(null, "Big Latte", 1.99),
					new Product(null, "Cold Coffee", 3.99))
					.flatMap(repo::save);

			initProds.thenMany(repo.findAll()).subscribe(System.out::println);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
//		return RouterFunctions.route(RequestPredicates.GET("/products")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), productHandler::getAllProducts)
//				.andRoute(RequestPredicates.POST("/products")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), productHandler::saveProduct)
//				.andRoute(RequestPredicates.DELETE("/products/{id}")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), productHandler::deleteProductById)
//				.andRoute(RequestPredicates.GET("/products/{id}")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), productHandler::getProductById)
//				.andRoute(RequestPredicates.PUT("/products/{id}")
//				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), productHandler::updateProduct);

		return RouterFunctions.nest(RequestPredicates.path("/products"),
				RouterFunctions.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON)
						.or(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
						RouterFunctions.route(RequestPredicates.GET("/"), productHandler::getAllProducts)
				.andRoute(RequestPredicates.POST("/"), productHandler::saveProduct))
		.andNest(RequestPredicates.path("/{id}"),
				RouterFunctions.route(RequestPredicates.method(HttpMethod.GET), productHandler::getProductById)
		.andRoute(RequestPredicates.method(HttpMethod.PUT), productHandler::updateProduct)
		.andRoute(RequestPredicates.method(HttpMethod.DELETE), productHandler::deleteProductById)));
	}
}
