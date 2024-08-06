package journeybuddy.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:config.properties")
public class AppConfig {
    @Autowired
    private Environment env;

    public String getKakaoClientId() {
        return env.getProperty("kakao.client.id");
    }
}
