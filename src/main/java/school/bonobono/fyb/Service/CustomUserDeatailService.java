package school.bonobono.fyb.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.bonobono.fyb.Entity.FybUser;
import school.bonobono.fyb.Exception.CustomErrorCode;
import school.bonobono.fyb.Exception.CustomException;
import school.bonobono.fyb.Model.StatusTrue;
import school.bonobono.fyb.Repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDeatailService implements UserDetailsService {
    public static final CustomErrorCode LOGIN_FALSE = CustomErrorCode.LOGIN_FALSE;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        return userRepository.findOneWithAuthoritiesByEmail(email)
                .map(user -> createUser(user))
                .orElseThrow(() -> new CustomException(LOGIN_FALSE));
    }

    private User createUser(FybUser user) {

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new User(user.getEmail(),
                user.getPw(),
                grantedAuthorities
        );
    }
}
