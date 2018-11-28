package es.imim.ibi.comorbidity4j.server;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import es.imim.ibi.comorbidity4j.server.reservlet.ComputeComorbidityServlet;
import es.imim.ibi.comorbidity4j.server.reservlet.ResultLoaderServlet;

@SpringBootApplication
// @Configuration
// @EnableAutoConfiguration
@EnableScheduling
public class StartComorbidity4j extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(StartComorbidity4j.class, args);
	}


	@Bean
	ServletRegistrationBean<ComputeComorbidityServlet> computeComorbidityServletRegistration1() {
		ServletRegistrationBean<ComputeComorbidityServlet> srb = new ServletRegistrationBean<ComputeComorbidityServlet>();
		srb.setServlet(new ComputeComorbidityServlet());
		srb.setUrlMappings(Arrays.asList("/compute"));
		return srb;
	}

	@Bean
	ServletRegistrationBean<ResultLoaderServlet> resultLoaderServletRegistration2() {
		ServletRegistrationBean<ResultLoaderServlet> srb = new ServletRegistrationBean<ResultLoaderServlet>();
		srb.setServlet(new ResultLoaderServlet());
		srb.setUrlMappings(Arrays.asList("/results"));
		return srb;
	}
	
	
}
