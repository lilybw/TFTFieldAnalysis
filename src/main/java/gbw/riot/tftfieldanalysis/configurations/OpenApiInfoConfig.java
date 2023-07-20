package gbw.riot.tftfieldanalysis.configurations;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiInfoConfig {
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
