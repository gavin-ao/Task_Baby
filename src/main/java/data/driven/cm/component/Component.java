package data.driven.cm.component;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.DispatcherType;

/**
 * @author 何晋凯
 * @date 2018/06/04
 */
@org.springframework.stereotype.Component
public class Component {

    @Bean
    public FilterRegistrationBean sitemeshFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new com.opensymphony.sitemesh.webapp.SiteMeshFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean sessionFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SessionFilter());
        registration.setDispatcherTypes(DispatcherType.FORWARD,DispatcherType.INCLUDE,DispatcherType.REQUEST);
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * 微信请求过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean wechatApiFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WechatApiFilter());
        registration.setDispatcherTypes(DispatcherType.FORWARD,DispatcherType.INCLUDE,DispatcherType.REQUEST);
        registration.addUrlPatterns("/wechatapi/*");
        return registration;
    }

    @Bean
    public ServletRegistrationBean getVelocityServlet(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new data.driven.cm.component.MyVelocityDecoratorServlet());
        registrationBean.addUrlMappings("*.vm");
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }
}
