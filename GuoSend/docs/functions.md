#### 功能点

##### 一、员工管理模块

###### 1、用户登录

- 校验账号以及MD5加密后的密码
- 登录成功后将用户id存入session中

###### 2、页面校验登录

- 防止用户没有登录，直接拼路由进入页面
- 自定义过滤器`LoginCheckFilter`，进行页面的过滤
- 每次访问资源时，校验session中是否有用户的id，有则放行，没有则转到`login`登录页

###### 3、用户退出

- 清除掉当前员工的session即可

###### 4、新增用户

- 因为在新增时无法设置密码，因此需要在后台初始化一个初始密码MD5加密后存入数据库

###### 5、全局异常处理

- 新增用户时，用户名在数据库中是Unique类型，如果新增时重复输入了一个用户名，在数据库层面会抛出异常至后端
- 使用`@ControllerAdvice(annotations = {RestController.class, Controller.class})`和`@ExceptionHandler(SQLIntegrityConstraintViolationException.class)`注解去扫描标注了`annotations` 中的class，且在该类中抛出异常的类
- 当发生扫描时，进行异常信息的拦截，并通过`ex.getMessage()`进行判别，细化对异常的处理

###### 6、员工信息分页查询

- 使用MP的page分页插件，在MybatisPlusConfig中配置分页插件（即MP的拦截器`mybatisPlusInterceptor`）
- 新增Page对象并添加Wrapper条件即可

###### 7、禁用员工账号（修改员工信息）

- 前端发送请求，将整个Employee对象传到后端
- 后端使用Service对数据库进行UpdateById操作

###### 8、扩展MVC的框架消息转换器

- 使用`JacksonObjectMapper`类
- 并在MVCConfig中扩展消息转换器

###### 9、编辑员工信息

- 先回写要修改员工的信息
- 复用`禁用员工账号`的方法，即修改员工信息的方法提交数据即可



##### 二、分类管理模块

###### 1、公共字段自动填充

- 几乎所有的实体都有创建时间、创建人、更新时间、更新人这四个字段，在每次业务中赋值时会显得很弱智

- MP提供了自动填充功能，新建一个类实现`MetaObjectHandler`接口中并重写`insertFill`和`updateFill`方法，在方法中添加赋值逻辑

    - 在实体字段中，使用注解`@TableField`来标注在什么时候进行填充

        ```java
        @TableField(fill = FieldFill.INSERT)//插入时填充字段
        private LocalDateTime createTime;
        
        @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
        private LocalDateTime updateTime;
        
        @TableField(fill = FieldFill.INSERT)//插入时填充字段
        private Long createUser;
        
        @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
        private Long updateUser;
        ```

    - 其中`insertFill`会在执行插入操作时，进行自动填充，`updateFill`当发生更新操作时，自动填充

        ```java
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("字段名", 需要填充的值);
    }

###### 2、在`MetaObjectHandle`r中获取当前登录人的id

- `MetaObjectHandler`无法获取到`httpservletrequest`，也无法拿到session中的值，因此无法获取到id
- 客户端发送的每次`http`请求，对应的在服务端都会分配一个新的线程来处理，因此可以将id放到`ThreadLocal`中，作为局部变量来使用
- 编写`BaseContext`工具类，持有`ThreadLocal<Long>`类型的成员变量，并基于`ThreadLocal`编写set和get方法

###### 3、新增分类

- 普通的`@RequestBody`传递参数，新增数据

###### 4、分类信息分页查询

- MP的分页插件，老样子

###### 5、删除分类

- 如果分类关联了菜品或套餐，则不允许删除，因此在删除前先进行判断
- 自定义业务异常类
- 在全局异常处理器中捕获自定义的业务异常

###### 6、修改分类

- 不需要回写，因为弹窗中的字段不多，可以通过前端赋值进行回写
- 点击确认时，直接将回写的值更新到数据库即可

##### 三、菜品管理业务模块

###### 1、文件上传

- 上传菜品图片时，通过vue封装的form表单将图片上传至服务端

###### 2、文件下载

- 图片上传完成后，发送请求将图片回传到浏览器并渲染出来