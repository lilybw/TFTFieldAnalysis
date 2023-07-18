package gbw.riot.tftfieldanalysis.configurations;

import gbw.riot.tftfieldanalysis.responseUtil.DetailedResponse;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.DataPointDTO;
import gbw.riot.tftfieldanalysis.responseUtil.dtos.EdgeDTO;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.MapSchema;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Configuration
@OpenAPIDefinition
public class DocsConfiguration {

    @Bean
    public GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("custom")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public Info apiInfo(){
        Contact contact = new Contact()
                .email("gustavbw@gmail.com")
                .name("Gustav B. Wanscher")
                .url("https://http.cat/404");

        License license = new License()
                .name("MIT")
                .url("http://localhost:13498/licence");

        return new Info()
                .contact(contact)
                .title("TFTFA")
                .description("Browsing TFT as was it a graph data model.")
                .version("OPENAPI_3_0")
                .termsOfService("https://http.cat/404")
                .license(license);
    }
}
