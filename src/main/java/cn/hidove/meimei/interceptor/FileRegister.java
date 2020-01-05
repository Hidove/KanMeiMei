package cn.hidove.meimei.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileRegister implements WebMvcConfigurer {

    @Value("${meimei.image.savePath}")
    private String imageSavePath;

    @Override
    /*
     * 注册静态文件的自定义映射路径
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!imageSavePath.endsWith("/")) imageSavePath += "/";
        registry.addResourceHandler("/" + imageSavePath + "**").addResourceLocations("file:" + System.getProperty("user.dir") + "/" + imageSavePath);
    }
}
