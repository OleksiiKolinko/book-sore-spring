package mate.academy.config;

import java.util.Map;
import java.util.Set;
import mate.academy.model.Role;
import mate.academy.model.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {
    private static final Long ID_TWO = 2L;
    private static final String USER_EMAIL = "Jon1.doe@example.com";
    private static final String USER_PASSWORD = "12345678";
    private static final String FIRST_NAME = "firstName1";
    private static final String LAST_NAME = "lastName1";
    private static final String SHIPPING_ADDRESS = "ShippingAddress1";

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User user = new User();
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        role.setId(ID_TWO);
        user.setId(ID_TWO);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setShippingAddress(SHIPPING_ADDRESS);
        user.setRoles(Set.of(role));
        Map<String, User> users = Map.of(user.getEmail(), user);
        return users::get;
    }
}
