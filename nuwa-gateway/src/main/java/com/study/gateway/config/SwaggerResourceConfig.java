package com.study.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Primary
public class SwaggerResourceConfig implements SwaggerResourcesProvider {

    /**
     * Swagger2默认的url后缀
     */
    public static final String SWAGGER2URL = "v2/api-docs";

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private GatewayProperties gatewayProperties;

    @Value("${spring.application.name}")
    private String self;

    @Autowired
    public SwaggerResourceConfig(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }


    // 请求网关时就会执行此方法
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();
        //获取所有路由的ID并加入到routes里
        //routeLocator.getRoutes().filter(route -> route.getUri().getHost() != null)
        //        .filter(route -> !self.equals(route.getUri().getHost()))
        //        .subscribe(route -> routes.add(route.getId()));
        //// 记录已经添加的server
        //Set<String> dealed = new HashSet<>();
        //routes.forEach(instance -> {
        //    // 拼接url
        //    String url = "/" + instance.toLowerCase() + SWAGGER2URL;
        //    if (!dealed.contains(url)){
        //        dealed.add(url);
        //        SwaggerResource swaggerResource = new SwaggerResource();
        //        swaggerResource.setUrl(url);
        //        swaggerResource.setName(instance);
        //        resources.add(swaggerResource);
        //    }
        //});

        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        // 过滤出配置文件中定义的路由->过滤出Path Route Predicate->根据路径拼接成api-docs路径->生成SwaggerResource
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route -> {
            route.getPredicates().stream()
                    .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                    .forEach(predicateDefinition -> resources.add(swaggerResource(route.getId(),
                            predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                    .replace("**", SWAGGER2URL + "?group=1.X版本"))));
        });
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        log.info("name:{},location:{}", name, location);
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("1.0.0");
        return swaggerResource;
    }

}
