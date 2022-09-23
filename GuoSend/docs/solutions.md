###### Mysql

1、MySQL 连接出现 Authentication plugin 'caching_sha2_password' cannot be loaded

原因：mysql8 之前的版本中加密规则是mysql_native_password,而在mysql8之后,加密规则是caching_sha2_password

解决方法：https://www.cnblogs.com/zhurong/p/9898675.html



###### 后端

1、自定义静态资源映射出错

原因：项目中放到了classpath即resources下的backend和front文件夹中，SpringBoot默认映射是static和public，因此需要手动改变映射

解决方法：config包需要与主启动类在一层级（因为默认扫描的就是主类的层级），然后写自定义配置类并继承重写方法，放行自定义的静态资源

```java
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("自定义静态资源映射成功");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");//放行的静态资源
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
```

2、@AutoWired和@Resource的区别及使用

原因：标准@AutoWired时，IDEA会警告不推荐使用

解决方法：https://blog.csdn.net/youanyyou/article/details/126970723
