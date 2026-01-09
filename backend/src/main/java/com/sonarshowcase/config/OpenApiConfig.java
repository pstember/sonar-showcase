package com.sonarshowcase.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for automatic API documentation generation.
 * 
 * @author SonarShowcase
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Default constructor for OpenApiConfig.
     */
    public OpenApiConfig() {
    }

    /**
     * Creates the OpenAPI configuration bean
     *
     * @return Configured OpenAPI instance
     */
    @Bean
    public OpenAPI sonarShowcaseOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("SonarShowcase");
        contact.setUrl("https://github.com/sonarcloud-demos/sonar-demo");

        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("SonarShowcase API")
                .version("1.2.0")
                .contact(contact)
                .description("""
                        SonarShowcase REST API Documentation
                        
                        ⚠️ **WARNING:** This application intentionally contains security vulnerabilities, 
                        bugs, and code smells for educational purposes. DO NOT use in production!
                        
                        ## Base URL
                        All endpoints are prefixed with `/api/v1`
                        
                        ## Authentication
                        **No authentication is implemented.** All endpoints are publicly accessible.
                        The `/api/v1/users/login` endpoint exists but is a vulnerable SQL injection 
                        demo, not a real authentication mechanism.
                        
                        ## Vulnerable Endpoints
                        Several endpoints contain intentional security vulnerabilities for SonarCloud 
                        demonstration purposes. These are clearly marked in the endpoint descriptions.
                        """)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}

