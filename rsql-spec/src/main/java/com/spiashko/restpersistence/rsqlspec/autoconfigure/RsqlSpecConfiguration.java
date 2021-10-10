package com.spiashko.restpersistence.rsqlspec.autoconfigure;


import com.spiashko.restpersistence.rsqlspec.RsqlSpecArgumentResolver;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class RsqlSpecConfiguration implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new RsqlSpecArgumentResolver());
	}
}
