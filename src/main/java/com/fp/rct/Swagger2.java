package com.fp.rct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {
	public static final String SWAGGER_SCAN_BASE_PACKAGE = "com.fp.rct";
	public static final String VERSION = "1.0.0";
	//dd
	@Bean
	public Docket CreateRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE)).paths(PathSelectors.any())
				.build();
	}

	ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Api").description("my api test")
				.termsOfServiceUrl("https://blog.csdn.net/yeyinglingfeng").contact("yeyinglingfeng").version(VERSION)
				.build();
	}
}
