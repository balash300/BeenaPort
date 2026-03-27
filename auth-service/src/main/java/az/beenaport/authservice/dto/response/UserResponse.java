package az.beenaport.authservice.dto.response;

import az.beenaport.authservice.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public Roles role;
}
