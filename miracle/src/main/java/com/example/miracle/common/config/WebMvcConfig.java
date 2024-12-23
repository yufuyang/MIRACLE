package com.example.miracle.common.config;

import com.example.miracle.common.interceptor.PlatformAuthInterceptor;
import com.example.miracle.common.interceptor.CompanyAuthInterceptor;
import com.example.miracle.common.interceptor.MerchantAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PlatformAuthInterceptor platformAuthInterceptor;

    private final CompanyAuthInterceptor companyAuthInterceptor;

    private final MerchantAuthInterceptor merchantAuthInterceptor;

    public WebMvcConfig(PlatformAuthInterceptor platformAuthInterceptor, CompanyAuthInterceptor companyAuthInterceptor, MerchantAuthInterceptor merchantAuthInterceptor) {
        this.platformAuthInterceptor = platformAuthInterceptor;
        this.companyAuthInterceptor = companyAuthInterceptor;
        this.merchantAuthInterceptor = merchantAuthInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(platformAuthInterceptor)
                .addPathPatterns("/platform/**")
                .excludePathPatterns("/platform/user/login");


        // 公司端拦截器
        registry.addInterceptor(companyAuthInterceptor)
                .addPathPatterns("/company/**")
                .excludePathPatterns("/company/user/login");

        // 商户端拦截器
        registry.addInterceptor(merchantAuthInterceptor)
                .addPathPatterns("/merchant/**")
                .excludePathPatterns("/merchant/user/login");
    }
}