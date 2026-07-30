package com.samdasu.dodoong;

import com.samdasu.dodoong.global.config.CorsProperties;
import com.samdasu.dodoong.global.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, CorsProperties.class})
public class DodoongApplication {

	public static void main(String[] args) {
		SpringApplication.run(DodoongApplication.class, args);
	}

}
