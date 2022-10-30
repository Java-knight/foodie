package com.han.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 配置Swagger2
 *
 * http://localhost:8088/swagger-ui.html     原路径（官方）
 * http://localhost:8088/doc.html           修改后的
 * @Author dell
 * @Date 2021/4/29 12:22
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    //配置swagger2核心配置 docket
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)      //指定api类型为swagger2
                   .apiInfo(apiInfo())                      //用于api文档汇总信息
                   .select().apis(RequestHandlerSelectors
                        .basePackage("com.han.controller")) //指定controller包
                   .paths(PathSelectors.any())              //所有controller下的类
                   .build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口api")                //文档标题
                .contact(new Contact("knight",
                        "https://www.imooc.com",
                        "2290707867@qq.com"))              //开发人员的姓名, 博客/网站地址, 邮箱
                .description("专为天天吃货提供的api文档")         //详细信息
                .version("1.0.1")                                //文档版本号
                .termsOfServiceUrl("https://www.imooc.com")      //网站地址
                .build();
    }

}
