##### 功能点

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