package az.beenaport.paymentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TokenProvider {

    public String getToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        return attributes.getRequest().getHeader("Authorization");
    }

    public boolean hasToken() {
        String token = getToken();
        return token != null && token.startsWith("Bearer ");
    }
}