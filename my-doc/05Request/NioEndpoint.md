## nio 定制线程池
- SocketProcessor : Worker thread
- Poller :委派SocketProcessor处理请求
- Acceptor :侦听传入的TCP / IP连接并将其移交给适当的处理器
## 相关内部类
- PollerEvent :轮询事件的可缓存对象
- SocketProcessor : Worker thread
- Poller :委派SocketProcessor处理请求
- Acceptor :侦听传入的TCP / IP连接并将其移交给适当的处理器
- KeyAttachment
- NioBufferHandler
- NioEndpoint.Handler :用于套接字处理

