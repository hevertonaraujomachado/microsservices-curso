package io.github.cursodsousa.msgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
@EnableDiscoveryClient
public class MsgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsgatewayApplication.class, args);
	}

	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("customer_service", r -> r.path("/client/**")
						.uri("lb://customer-service"))
				.build();
	}

}
