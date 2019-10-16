package com.fp.rct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.annotations.Api;
@SpringBootApplication
@Api(value = "SSH调用工具",description="SSH调用工具")
public class RctStart {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(RctStart.class, args);
	}


}
