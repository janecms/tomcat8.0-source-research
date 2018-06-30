org.apache.catalina.session.ManagerBase.createSession

- 保存调用栈
```
org.apache.catalina.session.PersistentManagerBase.stopInternal(PersistentManagerBase.java:866)
org.apache.catalina.session.PersistentManagerBase.unload(PersistentManagerBase.java:629)
org.apache.catalina.session.PersistentManagerBase.swapOut(PersistentManagerBase.java:787)
org.apache.catalina.session.PersistentManagerBase.writeSession(PersistentManagerBase.java:818)
org.apache.catalina.session.TestPersistentManager$DummyStore.save(TestPersistentManager.java:304)
```
- 查询调用栈
```markdown
org.apache.catalina.session.TestStandardSession$Bug56578Servlet.doGet(TestStandardSession.java:92)
org.apache.catalina.session.StandardSessionFacade.getCreationTime(StandardSessionFacade.java:60)
org.apache.catalina.session.StandardSession.getCreationTime(StandardSession.java:1136)
```
