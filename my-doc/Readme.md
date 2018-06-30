Catalina（Servlet容器实现）
-  通过Connector与用户交互，通过Adapter与Coyote交互。
- Coyote（底层协议相关实现，http11,ajp）每个协议都实现ProtocolHandler接口

## 初始化顺序（initInternal）
StandardServer
    ->StandardService
        ->StandardEngine
            ->StandardHost
                ->StandardContext
                    ->StandardWrapper

## 启动顺序
Catalina.start
->StandardServer
    ->StandardService
        ->StandardEngine
            ->StandardHost
                ->StandardContext
                    ->WebappLoader
                        ->StandardWrapper

## 总结

- Server: 是管理tomcat所有组件的容器，包含一个或多个的service；

- Catalina: 
    tomcat的顶级容器，main()方法中就是通过，创建Catalina 对象实例，来启动或者关闭 tomcat；

- Service: Service是包含Connector和Container的集合，Service用适当的Connector接收用户的请求，再发给相应的Container来处理；

- Connector: 主要功能是 ◇socket的接收 ◇根据协议类型处理socket ◇封装相应的request和response，交给Container；

- Container: Engine容器接收来自Connector的请求，并且通过Pipeline依次传递给子容器的Pipeline；
    **Container有多种子类型：Engine、Host、Context和Wrapper，这几种子类型Container依次包含，处理不同粒度的请求。**
另外Container里包含一些基础服务，如Loader、Manager和Realm。
- Engine: 在Engine的Pipeline中的Valve的invoke方法中，根据request.getHost()来定位下一个host；

- Host: 一个Web服务器虚拟机，管理着具体的 web application；

- Context: 就是我们所部属的具体Web应用的上下文，每个请求都是在具体的上下文中处理；

- Wrapper：对应着Web的每一个 Servlet；                        