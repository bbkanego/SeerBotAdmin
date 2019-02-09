package com.seerlogics.chatbot.config;

import com.lingoace.spring.filter.CORSFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bkane on 5/7/18.
 */
public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        servletContext.addFilter("corsFilter", corsFilter())
                .addMappingForUrlPatterns(null, false, "/*");

        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringConfig.class);
        ctx.setServletContext(servletContext);
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        dynamic.addMapping("/api/*");
        dynamic.setLoadOnStartup(1);
    }

    public CORSFilter corsFilter() {
        CORSFilter corsFilter = new CORSFilter();
        List<String> allowedOrigins = Arrays.asList("http://localhost:4200",
                "file://", "http://localhost:8000", "http://localhost:8080",
                "http://eventgenie.lingoace.com.s3-website.us-east-2.amazonaws.com", "http://eventgenie.lingoace.com");
        corsFilter.setAllowedOrigins(allowedOrigins);
        return corsFilter;
    }
}
