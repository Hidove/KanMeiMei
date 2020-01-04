package cn.hidove.meimei.configurer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer {
    @Value("${meimei.image.savePath}")
    private String imageSavePath;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                if (!imageSavePath.endsWith("/")) imageSavePath += "/";
                registry.addResourceHandler("/" + imageSavePath + "/**").addResourceLocations("file:" + System.getProperty("user.dir") + "/" + imageSavePath);
            }
        };
    }
}