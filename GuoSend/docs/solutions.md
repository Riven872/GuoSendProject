###### Mysql

1、MySQL 连接出现 Authentication plugin 'caching_sha2_password' cannot be loaded

原因：mysql8 之前的版本中加密规则是mysql_native_password,而在mysql8之后,加密规则是caching_sha2_password

解决方法：https://www.cnblogs.com/zhurong/p/9898675.html