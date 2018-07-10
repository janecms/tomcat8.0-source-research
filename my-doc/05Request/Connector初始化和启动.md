## Connector
### Connector的实现分为以下几种
- Http Connector：基于HTTP协议，负责建立HTTP连接。它又分为BIO Http Connector（是Tomcat的默认Connector）与NIO Http Connector两种，后者提供对非阻塞IO与长连接Comet的支持。
- AJP Connector：基于AJP协议，AJP是专门设计用于Tomcat与HTTP服务器通信定制的协议，能提供较高的通信速度和效率。如与Apache服务器集成时，采用这个协议。
- APR HTTP Connector：用C实现，通过JNI调用的。主要提升对静态资源（如HTML、图片、CSS、JS等）的访问性能。现在这个库已独立出来可用在任何项目中。APR性能较前两类有很大提升。

主要解决的问题就是Socket的接收，为了能够很好的处理各种协议和并发异步接收，Connector加入了两个组件 ProtocolHandler和EndPoint。
org.apache.catalina.connector.Connector.startInternal
   -> org.apache.coyote.http11.Http11NioProtocol.start
    ->  org.apache.tomcat.util.net.AbstractEndpoint.start
    -> org.apache.tomcat.util.net.NioEndpoint.startInternal
    -> org.apache.tomcat.util.net.AbstractEndpoint.startAcceptorThreads
        ->org.apache.tomcat.util.net.NioEndpoint.Acceptor

###  ProtocolHandler
每一种协议都有各自具体定义，和具体的协议头的格式，那么我么在接收到客户请求之后，就应该根据协议的类型采用相应的解析方法。
ProtocolHandler的具体作用：
- 定义具体处理Socket的AbstractEndPoint;
- 提供解析请求的AbstractConnectionHandler来获取具体的协议头；
- 相关的init、start、stop方法
### AbstractEndPoint 
   致力于高并发的解决socket的接收和处理;
   EndPoint 中两个协同合作的Runnable:
   - (1) Accepter负责用ServerSocket.accept()来接收客户请求，并且把建立连接之后的Socket交由Poller处理；
   - (2)  Poller负责接收请求，并处理；

## Connector 解析
```markdown

public Connector(String protocol) {
        setProtocol(protocol);
        // Instantiate protocol handler
        ProtocolHandler p = null;
        try {
            Class<?> clazz = Class.forName(protocolHandlerClassName);
            p = (ProtocolHandler) clazz.newInstance();
        } catch (Exception e) {
            log.error(sm.getString(
                    "coyoteConnector.protocolHandlerInstantiationFailed"), e);
        } finally {
            this.protocolHandler = p;
        }

        if (!Globals.STRICT_SERVLET_COMPLIANCE) {
            URIEncoding = "UTF-8";
            URIEncodingLower = URIEncoding.toLowerCase(Locale.ENGLISH);
        }
    }
```

StandardService.initInternal > Connector.init

- Connector初始化 
org.apache.catalina.connector.Connector.initInternal
```markdown
@Override
    protected void initInternal() throws LifecycleException {

        super.initInternal();

        // Initialize adapter
        //请求处理器的实现，该处理器将处理委托给Coyote处理器
        adapter = new CoyoteAdapter(this);
        protocolHandler.setAdapter(adapter);

        // Make sure parseBodyMethodsSet has a default
        if( null == parseBodyMethodsSet ) {
            setParseBodyMethods(getParseBodyMethods());
        }

        if (protocolHandler.isAprRequired() &&
                !AprLifecycleListener.isAprAvailable()) {
        }

        try {
            protocolHandler.init();
        } catch (Exception e) {
        }
    }
```
-  Connector 启动
org.apache.catalina.connector.Connector.startInternal
 ```markdown

@Override
    protected void startInternal() throws LifecycleException {

        // Validate settings before starting
        if (getPort() < 0) {
            throw new LifecycleException(sm.getString(
                    "coyoteConnector.invalidPort", Integer.valueOf(getPort())));
        }

        setState(LifecycleState.STARTING);

        try {
            protocolHandler.start();
        } catch (Exception e) {

        }
    }
```
### ProtocolHandler 启动
org.apache.coyote.http11.Http11NioProtocol.start
```markdown
    public void start() throws Exception {
        super.start();
        if (npnHandler != null) {
            npnHandler.init(getEndpoint(), 0, getAdapter());
        }
    }
```

```markdown
    public void start() throws Exception {
        if (getLog().isInfoEnabled())
            getLog().info(sm.getString("abstractProtocolHandler.start",
                    getName()));
        try {
            endpoint.start();
        } catch (Exception ex) {
        }
    }
```

### NioEndpoint 启动

org.apache.tomcat.util.net.NioEndpoint.startInternal
```markdown

@Override
    public void startInternal() throws Exception {

        if (!running) {
            running = true;
            paused = false;

            processorCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                    socketProperties.getProcessorCache());
            eventCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                            socketProperties.getEventCache());
            nioChannels = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                    socketProperties.getBufferPool());

            // Create worker collection
            if ( getExecutor() == null ) {
                createExecutor();
            }

            initializeConnectionLatch();

            // Start poller threads
            pollers = new Poller[getPollerThreadCount()];
            for (int i=0; i<pollers.length; i++) {
                pollers[i] = new Poller();
                Thread pollerThread = new Thread(pollers[i], getName() + "-ClientPoller-"+i);
                pollerThread.setPriority(threadPriority);
                pollerThread.setDaemon(true);
                pollerThread.start();
            }

            startAcceptorThreads();
        }
    }
```

### 启动线程
-  org.apache.tomcat.util.net.NioEndpoint.Acceptor
-  org.apache.tomcat.util.net.NioEndpoint.Poller