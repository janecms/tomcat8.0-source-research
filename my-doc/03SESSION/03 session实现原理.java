1. 默认用StandardManager
把session数据保存到work\Catalina\localhost\examples\SESSIONS.ser
SESSIONS.ser中存放了多个session的数据。

启动Tomcat时，调用org.apache.catalina.session.StandardManager.load()，
把SESSIONS.ser中的数据反序列化回来，生成多个StandardSession实例。
停止Tomcat时，调用org.apache.catalina.session.StandardManager.unload()把所有session序列化保存到SESSIONS.ser。
注意在eclipse中按红色方块的Terminate按钮是无法触发session的保存的，必需运行tomcat\bin\shutdown.bat才可以。

2. PersistentManager
可以把session数据保存到文件中(也就是FileStore的方式)，一个session对应一个文件，文件名是sessionId + ".session"，
也可以把session数据保存到数据库中(也就是JDBCStore的方式)。

StandardManager与PersistentManager的差别是:
session的反序列化并不是在启动Tomcat时做的，
也就是说org.apache.catalina.session.PersistentManagerBase.load()这个方法其实是没有用的，
".session"文件的读取会延迟到真正访问这个session时才去读，也就是调用request.getSession方法时，
如果上一次建立了一个socket，那么tomcat会通过cookie的方式把sessionId发给浏览器，
然后浏览器下一次请求时同样通过cookie把sessionId带回来，如:
Cookie: JSESSIONID=3EE8725ABF1B7960363C28BEBBA86D63
最后根据这个JSESSIONID就可以查看相关的".session"文件了。

StandardManager与PersistentManager都是在tomcat关闭时把session保存起来。

-  Manager：Tomcat对于Session管理器定义的接口规范，
-  ManagerBase：封装了Manager接口通用实现的抽象类，未提供对load()/unload()等方法的实现，需要具体子类去实现。所有的Session管理器都继承自ManagerBase。

- ClusterManager：在Manager接口的基础上增加了集群部署下的一些接口，所有实现集群下Session管理的管理器都需要实现此接口。

- PersistentManagerBase：提供了对于Session持久化的基本实现。

-  PersistentManager：继承自PersistentManagerBase，可以在Server.xml的元素下通过配置元素来使用。PersistentManager可以将内存中的Session信息备份到文件或数据库中。当备份一个Session对象时，该Session对象会被复制到存储器（文件或者数据库）中，而原对象仍然留在内存中。因此即便服务器宕机，仍然可以从存储器中获取活动的Session对象。如果活动的Session对象超过了上限值或者Session对象闲置了的时间过长，那么Session会被换出到存储器中以节省内存空间。

-  StandardManager：不用配置元素，当Tomcat正常关闭，重启或Web应用重新加载时，它会将内存中的Session序列化到Tomcat目录下的/work/Catalina/host_name/webapp_name/SESSIONS.ser文件中。当Tomcat重启或应用加载完成后，Tomcat会将文件中的Session重新还原到内存中。如果突然终止该服务器，则所有Session都将丢失，因为StandardManager没有机会实现存盘处理。

-  ClusterManagerBase：提供了对于Session的集群管理实现。

-  DeltaManager：继承自ClusterManagerBase。此Session管理器是Tomcat在集群部署下的默认管理器，当集群中的某一节点生成或修改Session后，DeltaManager将会把这些修改增量复制到其他节点。

-  BackupManager：没有继承ClusterManagerBase，而是直接实现了ClusterManager接口。是Tomcat在集群部署下的可选的Session管理器，集群中的所有Session都被全量复制到一个备份节点。集群中的所有节点都可以访问此备份节点，达到Session在集群下的备份效果。

StandardManager是StandardContext的子组件，用来管理当前Context的所有Session的创建和维护。