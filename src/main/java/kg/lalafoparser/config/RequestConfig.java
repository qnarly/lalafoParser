package kg.lalafoparser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RequestConfig {
    @Value("${lalafo.url}")
    private String baseUrl;

    @Bean
    public RestClient lalafoRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*")
                .defaultHeader("device", "pc")
                .defaultHeader("country-id", "12")
                .defaultHeader("language", "ru_RU")
                .defaultHeader("user-hash", "6f6f9b09-96cf-488e-a2c5-5e4eac055154")
                .defaultHeader(HttpHeaders.REFERER, "https://lalafo.kg/")
                .defaultHeader(HttpHeaders.ORIGIN, "https://lalafo.kg")
                .build();
    }
}
