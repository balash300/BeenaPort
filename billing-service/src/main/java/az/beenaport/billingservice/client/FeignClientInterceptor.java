package az.beenaport.billingservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {

    private final TokenProvider tokenProvider;
    private final SystemTokenProvider systemTokenProvider;

    @Override
    public void apply(RequestTemplate template) {
        String token = tokenProvider.getToken();

        if (token != null && token.startsWith("Bearer ")) {
            // HTTP request context-dən — user token
            template.header("Authorization", token);
        } else {
            // Scheduler / async context — system token
            log.debug("Using system token for Feign call to: {}", template.url());
            template.header("Authorization",
                    "Bearer " + systemTokenProvider.generateSystemToken());
        }
    }
}