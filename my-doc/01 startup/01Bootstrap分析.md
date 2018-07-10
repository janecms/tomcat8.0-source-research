## org.apache.catalina.startup.Bootstrap
```
main(String[])
	=> init()
		=> initClassLoaders()
			=> createClassLoader(String, ClassLoader)

	=> setAwait(boolean)
		=> 反射调用 org.apache.catalina.startup.Catalina.setAwait(boolean)

	=> load(String[])
		=> 反射调用 org.apache.catalina.startup.Catalina.load(String[])
			=> arguments(String[])
			=> load()
				=> initDirs()
				=> initNaming()
				=> createStartDigester()
				=> org.apache.tomcat.util.digester.Digester.parse(InputSource)
				=> initStreams()
				=> StandardServer.init()

	=> start()
		=> 反射调用 org.apache.catalina.startup.Catalina.start()
		=> StandardServer.start()
```

## Tomcat容器状态
这些状态都定义在枚举类LifecycleState中。
目前，Tomcat的容器具有以下状态：
- NEW：容器刚刚创建时，即在LifecycleBase实例构造完成时的状态。
- INITIALIZED：容器初始化完成时的状态。
- STARTING_PREP：容器启动前的状态。
- STARTING：容器启动过程中的状态。
- STARTED：容器启动完成的状态。
- STOPPING_PREP：容器停止前的状态。
- STOPPING：容器停止过程中的状态。
- STOPPED：容器停止完成的状态。
- DESTROYED：容器销毁后的状态。
- FAILED：容器启动、停止过程中出现异常的状态。
- MUST_STOP：此状态未使用。
- MUST_DESTROY：此状态未使用。