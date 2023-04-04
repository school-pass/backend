package gbsw.plutter.project.PMS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private HandlerInterceptor jwtHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
            .addInterceptor(jwtHandlerInterceptor)
            .addPathPatterns("/api/**");
    }

    @Override
    protected void configure(HttpSecurity sec) throws Exception {
        sec.authorizeRequests().antMatchers("/").permitAll();
        sec.authorizeRequests().antMatchers("/api/").permitAll();
        // 등등

        sec.csrf().disable();

        // https://youtu.be/8qrcM3Pm-Lk?t=845 참고
        sec.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
