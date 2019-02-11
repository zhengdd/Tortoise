#Tortoise
###项目描述
在一些项目开发中，对于数据的本地加密安全存储有了明确的需求。而Android的SharePreference是我们经常会用到的一种key-value存储方式。但SharePreference的存储形式是以明文的方式进行存储，有一定的数据被破解获取的风险，于是就设计了Tortoise这套框架进行加密存储来减少数据暴露的风险。

###导入

```
compile 'com.dongdong.animal:Toroise:0.0.1'
```

###使用



本项目分为两个部分，第一部分为密钥的生成与存储模块，主要通过AndroidKeyStore实现。第二部分为key-value数据加解密存储的封装实现。两部分可以单独使用。

#####KeyStore部分
在Application中进行初始化
```
KeyStoreManager.turnInit(this);
```
在使用时通过别名获取对称密钥SecretKey

```
KeyStoreManager.getSecretKey(“alias”);
```
#####加密存储部分
对SafeSpManager进行初始化操作
name：SharedPreferences文件名称，可为null，当为null时默认PackageName
```
SafeSpManager.turnInit(context, SecretKey);
SafeSpManager.turnInit(context, name, SecretKey);
```
使用时通过name获取对象然后通过put/get进行操作

```
SafeSpManager.getInstance(name).putString(key, value);
SafeSpManager.getInstance(name).getString(key)
```




