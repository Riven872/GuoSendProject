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

###### 3、获取菜品分类下拉框的源数据

- 会根据type的不同查出不同的菜品分类List

###### 4、新增菜品

- 会用到自定义model来接收页面传输的参数，术语就叫DTO，放在dto包中
    - DTO，Data Transfer Object，数据传输对象，一般用于展示层与服务层之间的数据传输
- 新增菜品时需要同时新增对应的口味，因此要操作两张表，需要自己写Service
- 同时操作两张表需要开启事务的支持

###### 5、菜品信息分页查询

- 查询的信息只有Dish实体不够用，需要用DTO去返回，因为DishDto继承了Dish且扩展了属性值
- 此处需要用到对象的拷贝功能，将对象的部分属性或全部拷贝到另一个对象上去

###### 6、修改菜品

- 首先要回写信息到DishDto实体中，要查两张表，因此在Service中自定义扩展一个方法
- 不能复用新增的功能，是因为如果原先有三个口味，修改之后变成两个，另一个口味并没有删除
- 因此可以先更新dish基本表，然后清理口味表，最后再添加口味至口味表
    - 注：`updateById(dishDto)`会更新Dish表，因为dishDto是Dish的子类，因此更新的实体是Dish实体，除非有个实体名字叫DishDto
- 多表要添加事务注解，保证数据的一致性

###### 7、批量启售、停售

- 根据传入的id和状态批量修改

###### 8、批量删除

- 无难度。。。



##### 四、套餐管理业务模块

###### 1、新增套餐

- 首先获取套餐分类和菜品分类到下拉框
- 根据菜品分类查询出对应的菜品以供添加
- 图片的上传和回写
- 保存套餐的相关数据（包括套餐信息及其关联的菜品信息，更新两张表需要事务注解）

###### 2、分页查询

- 老样子，需要用dto去返回数据

###### 3、删除套餐

- 删除套餐以及对应的菜品，因此要操作两张表
- 删除前需要查询套餐的状态，停售状态才可以删除

###### 4、修改售卖状态

- 没啥难度

###### 5、修改套餐

- 先回写
    - 查询套餐的基本信息，然后根据套餐查出对应的菜品（类似于主档和子档的关系）
- 保存
    - 先保存套餐的基本信息
    - 删除原套餐中的菜品信息
    - 重新添加菜品信息并将套餐的值赋上

    

##### 五、手机验证码登录

###### 1、短信发送（使用腾讯云SDK及发送短信API）

- 导入Maven坐标
- 调用API
- 获取用户手机号
- 生成验证码
- 调用腾讯云接口，将验证码作为参数传进去
- 将验证码存到session中，登录时进行校验

###### 2、验证码登录

- 点击发送验证码，向服务器发送请求，服务器调用腾讯云短信API返回验证码
- 点击登录，向服务器发送请求，服务器处理登录请求
    - 校验验证码是否登录成功
    - 如果登录成功且该账号不在数据库中，则自动注册，将手机号存到数据库中
    - 成功后需要将用户的id放到session中，保持会话状态
    - 登录失败则提示失败



##### 七、菜品展示、购物车、下单模块（移动端）