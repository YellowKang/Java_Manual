# 1、什么是Spring Security？

​	Spring Security是一个由Spring开发的一个能够为基于Spring的企业级应用系统提供申明式的安全访问控制解决方案的安全框架，负责应用的安全包括用户认证，用户授权，两个部分，用户认证指要求用户的用户名和密码，系统通过用户名和密码来完成认证过程，也就是说用户能否访问系统，用户认证一般要求用户提供用户名和密码，用户授权指的是验证某个用户是否有权限执行某个操作，在一个系统中，不同用户所具有的的权限是不同的，比如对一个文件来说，有的用户只能读取，有的用户可以进行修改，一般来说，系统会为不同的用户分配不同的角色，而每个角色对应一系列的权限，Spring Security的主要核心功能为认证和授权，所有的架构也是基于这两个核心功能去实现的

# 2、Spring Security核心过滤器

​	Spring Security  众所周知 想要对对Web资源进行保护，最好的办法莫过于Filter，要想对方法调用进行保护，最好的办法莫过于AOP。所以springSecurity在我们进行用户认证以及授予权限的时候，通过各种各样的拦截器来控制权限的访问，从而实现安全。

 	如下为其主要过滤器  

## WebAsyncManagerIntegrationFilter 

​			此过滤器用于集成SecurityContext到Spring异步执行机制中的WebAsyncManager。



​		SecurityContextPersistenceFilter 

​			SecurityContextPersistenceFilter是承接容器的session与spring security的重要filter，主要工作是从session中获取SecurityContext，然后放到上下文中，之后的filter大多依赖这个来获取登录态。其主要是通过HttpSessionSecurityContextRepository来存取的。



​		HeaderWriterFilter 

        CorsFilter 
        LogoutFilter
        RequestCacheAwareFilter
        SecurityContextHolderAwareRequestFilter
        AnonymousAuthenticationFilter
        SessionManagementFilter
        ExceptionTranslationFilter
        FilterSecurityInterceptor
        UsernamePasswordAuthenticationFilter

​	BasicAuthenticationFilter