package uk.broadoakdata.pdi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PdiApplication {

	private static final Logger log = LoggerFactory.getLogger(PdiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PdiApplication.class, args);
	}


	@Value("${pdi.plugins.folder}")
	private String pluginFolder;

	@Value("${pdi.reports.folder}")
	private String reportsFolder;

	@Bean
	public CommandLineRunner example(){
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				log.info("PDI plugins folder:" + pluginFolder);
			}
		};
	}

	@Configuration
	@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
	public class StaticResourceConfiguration implements WebMvcConfigurer {
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			log.info("PDI report folder:" + reportsFolder);
			registry.addResourceHandler("/report/**").addResourceLocations("file:" + reportsFolder);
		}
	}
}
