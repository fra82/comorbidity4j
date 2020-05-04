package es.imim.ibi.comorbidity4j.server;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import es.imim.ibi.comorbidity4j.server.reservlet.ComputeComorbidityServlet;
import es.imim.ibi.comorbidity4j.server.reservlet.ResultLoaderServlet;

@SpringBootApplication
// @Configuration
// @EnableAutoConfiguration
@EnableScheduling
public class StartComorbidity4j extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		
		final ConfigurableApplicationContext context = SpringApplication.run(StartComorbidity4j.class, args);
		StartComorbidity4j c4j = context.getBean(StartComorbidity4j.class);
		
		System.out.println("-------------------------------------------------");
		System.out.println("\n--- SERVER STARTUP CONFIGURATION GUIDE ---");
		System.out.println("-   To change server port:\n-       when you start Comorbidity4j server, specify the following program parameter: --server.port=8585");
		System.out.println("-   To change temporary files directory path:\n-       when you start Comorbidity4j server, specify the following program parameter: --spring.servlet.multipart.location=/path/to/temporary/files/directory/");
		System.out.println("-\n-   To start the server:");
		System.out.println("-      > Linux users:");
		System.out.println("-        java -cp '/local/path/to/comorbidity4j-LATEST-VERS/comorbidity4j-LATEST-VERS.jar:/local/path/to/comorbidity4j-LATEST-VERS/lib/*' es.imim.ibi.comorbidity4j.server.StartComorbidity4j");
		System.out.println("-      > Windows users:");
		System.out.println("-        java -cp \"c:\\Local\\Path\\To\\comorbidity4j-LATEST-VERS\\comorbidity4j-LATEST-VERS.jar;c:\\Local\\Path\\To\\comorbidity4j-LATEST-VERS\\lib\\*\" es.imim.ibi.comorbidity4j.server.StartComorbidity4j"); 
		System.out.println("-\n-   To specify a custom server port or temporary files directory path, start the server with the following program parameters:");
		System.out.println("-      > Linux users:");
		System.out.println("-        java -cp '/local/path/to/comorbidity4j-LATEST-VERS/comorbidity4j-LATEST-VERS.jar:/local/path/to/comorbidity4j-LATEST-VERS/lib/*' es.imim.ibi.comorbidity4j.server.StartComorbidity4j");
	    System.out.println("-	         --server.port=PORT_NUMBER --spring.servlet.multipart.location=/path/to/temporary/files/directory/");
		System.out.println("-      > Windows users:");
		System.out.println("-        java -cp \"c:\\Local\\Path\\To\\comorbidity4j-LATEST-VERS\\comorbidity4j-LATEST-VERS.jar;c:\\Local\\Path\\To\\comorbidity4j-LATEST-VERS\\lib\\*\" es.imim.ibi.comorbidity4j.server.StartComorbidity4j");
	    System.out.println("-	         --server.port=PORT_NUMBER --spring.servlet.multipart.location=c:\\local\\path\\to\\temporary\\files\\directory\\");
	    System.out.println("-------------------------------------------------"); 
		
		System.out.println("\n------------------- COMORBIDITY4WEB SERVER -------------------");
		// Check if the temp directory exists and is writable
		boolean startupCheckPassed = true;
		try {
			System.out.println(" --> Checking running port number:");
			if(c4j.getEnv() == null || !c4j.getEnv().containsProperty("server.port") || c4j.getEnv().getProperty("server.port") == null) {
				System.out.println(" >>> ATTENTION: wrongly specified server port number.");
				startupCheckPassed = false;
			}
			else {
				System.out.println("   Server port number equal to: " + c4j.getEnv().getProperty("server.port"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(" --> Checking the validity of the temporary files directory path (the local directory where Comorbidity4web writes partial and final resulta of comorbidity analyses):");
			
			if(c4j.getEnv() == null || !c4j.getEnv().containsProperty("spring.servlet.multipart.location") || c4j.getEnv().getProperty("spring.servlet.multipart.location") == null) {
				System.out.println(" >>> ATTENTION: the temporary files to directory path property is not specified.");
				startupCheckPassed = false;
			}
			else {
				File tempDir = new File(c4j.getEnv().getProperty("spring.servlet.multipart.location"));
				if(tempDir == null || !tempDir.exists()) {
					System.out.println(" >>> ATTENTION: the temporary files to directory path points to a directory that does not exist.");
					startupCheckPassed = false;
				} 
				else if(!tempDir.isDirectory()) {
					System.out.println(" >>> ATTENTION: the temporary files directory path points to local file system location that is not a directory.");
					startupCheckPassed = false;
				}
				else if(!tempDir.canWrite()) {
					System.out.println(" >>> ATTENTION: the server is not allowed to write files in the the temporary files directory path.");
					startupCheckPassed = false;
				}
				else {
					System.out.println("   Temporary files directory path correctly specified and equal to: " + c4j.getEnv().getProperty("spring.servlet.multipart.location"));
					ComputeComorbidityServlet.basePathResultStorage = c4j.getEnv().getProperty("spring.servlet.multipart.location");
					ComputeComorbidityServlet.basePathResultStorage = (ComputeComorbidityServlet.basePathResultStorage.endsWith(File.separator)) ? ComputeComorbidityServlet.basePathResultStorage : ComputeComorbidityServlet.basePathResultStorage + File.separator;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(startupCheckPassed) {
			System.out.println("\n--- SERVER CONFIGURATION ---");
			System.out.println("- Server port: " + ((c4j.getEnv() != null && c4j.getEnv().containsProperty("server.port") 
					&& c4j.getEnv().getProperty("server.port") != null) ? c4j.getEnv().getProperty("server.port") : "UNAVAILABLE"));
			System.out.println("- Temporary files directory path (local directory where Comorbidity4web writes partial and final resulta of comorbidity analyses): " + ((c4j.getEnv() != null && c4j.getEnv().containsProperty("spring.servlet.multipart.location") && 
					c4j.getEnv().getProperty("spring.servlet.multipart.location") != null) ? c4j.getEnv().getProperty("spring.servlet.multipart.location") : "UNAVAILABLE"));
			
			System.out.println("\nComorbidity4web server can be accessed at the following URL: http://localhost:" + c4j.getEnv().getProperty("server.port") + "/comorbidity4web/");
		}
		else {
			System.out.println(" >>>            Please, stop the server, solve this issue and restart the server.");
		}

		System.out.println("\n >>>            Documentation: https://comorbidity4j.readthedocs.io/");
		System.out.println("----------------------------------------------------------------");
		
		
		
	}
	
	@Autowired
	private Environment env;
	
	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
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
