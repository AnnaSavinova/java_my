package ru.fizteh.fivt.students.annasavinova.filemap.start;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("ru.fizteh.fivt.students.annasavinova.filemap")
@PropertySource("classpath:start.properties")

public class AppConfig {
    @Bean
    public PropertySourcesPlaceholderConfigurer placeholder() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
