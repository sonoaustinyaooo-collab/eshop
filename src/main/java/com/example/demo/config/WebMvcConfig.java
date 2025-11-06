package com.example.demo.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.demo") 
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 靜態資源映射：將 /resources/** 的請求映射到 webapp/resources/ 目錄
        // 例如：/resources/images/products/abc.jpg → webapp/resources/images/products/abc.jpg
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
        
        // ⭐ 如果要使用外部固定目錄儲存圖片（例如：D:/uploads/），請取消以下註解：
        // registry.addResourceHandler("/uploads/**")
        //         .addResourceLocations("file:///D:/uploads/")
        //         .setCachePeriod(3600);  // 快取 1 小時
        
        // 注意：目前系統使用 webapp/resources/images/products/ 儲存圖片
        // 如需改用外部目錄，需同時修改：
        // 1. ProductController 中的 uploadPath
        // 2. Product.java 中的 getImageUrl() 方法
        // 3. 取消上方的註解並設定正確路徑
    }
    
    // 新增：檔案上傳設定
    
    /**
     * 設定檔案上傳解析器
     * 
     * 功能說明：
     * 1. 處理 multipart/form-data 表單
     * 2. 設定上傳檔案的大小限制
     * 3. 設定暫存目錄
     * 
     * @return 檔案上傳解析器
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();       
        // 設定上傳檔案的總大小限制（10MB）
        resolver.setMaxUploadSize(10 * 1024 * 1024);     
        // 設定單一檔案大小限制（5MB）
        resolver.setMaxUploadSizePerFile(5 * 1024 * 1024);
        // 設定記憶體緩衝區大小（1MB）
        resolver.setMaxInMemorySize(1 * 1024 * 1024);    
        // 設定預設編碼
        resolver.setDefaultEncoding("UTF-8");      
        return resolver;
    }   
    
    // 國際化 (i18n) 設定
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(4800);
        return resolver;
    }
    
    @Bean 
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}