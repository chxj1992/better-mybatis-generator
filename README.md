Mybatis Generator Plugin (IDEA Plugin)
====

本插件基于<a href="https://github.com/kmaster/better-mybatis-generator"> better-mybatis-generator </a>二次开发而成. 
在原有功能的基础上增加了对批量插入, JavaDoc注释, @Mapper注解, 方法命名 等做了改造, 使之符合团队的规范要求.

改造点:
------
1. 增加类注释, @author(作者信息), @since(生成时间)支持
2. 增加 @Mapper, @Getter, @Setter, @ToString 注解支持
3. 增加对 batchInsert 方法的支持
4. 支持将方法命名风格从 xxxByPrimaryKey 改为 xxxById
5. 重构代码, 使之对二次开发更友好


插件安装：
-------
- 手动zip安装:
  - 下载 [最新发布版本](https://github.com/chxj1992/mybatis-generator-plugin/raw/master/mybatis-generator-plugin.zip)        
  - 手动安装: <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

截图：
-------
#1. 设置默认配置 <br>
![image](image/1.png)<br>

#2. 配置数据库 <br>
![image](image/2.png)<br>
![image](image/3.png)<br>
![image](image/4.png)<br>

#3. 在需要生成代码的表上右键, 选择 mybatis generator，打开预览界面 <br>
![image](image/5.png)<br>

#4. 设置确认完成后, 点击 `GENERATE!` 开始生成代码<br>
![image](image/6.png)<br>

#5. 首次使用此插件, 需要为插件提供数据库账号密码 <br>
![image](image/7.png)<br>

#6、检查生成的代码文件<br>
![image](image/8.png)<br>


如何在本地运行/调试此插件 | How to run/debug plugin ：
-------
#1、创建工程  File -> New -> Project from Version Control -> Git  填写github地址：https://github.com/kmaster/better-mybatis-generator.git<br>
![image](https://github.com/kmaster/better-mybatis-generator/blob/master/image/从github创建工程.png)<br>
#2、修改sdk
![image](https://github.com/kmaster/better-mybatis-generator/blob/master/image/修改插件sdk.png)<br>
#3、运行配置无误则直接run/debug。若因idea版本导致提供的配置不对，请考虑本地创建空的插件工程然后参考其配置。<br>
![image](https://github.com/kmaster/better-mybatis-generator/blob/master/image/run.png)<br>
此时打上断点就可以一步步调试，修改代码后点击 Build->prepare plugin modle "xxx" For Deployment生成插件安装包再本地安装使用，
如果想优化此插件代码，比如其他数据库或运行环境，可以修改后在github上发起pull request。能点个star就更好了，哈哈。

注意事项 | notice：
-------
#1、当数据库用mysql8，在URL上定义时区，推荐使用'?serverTimezone=GMT'，配置中勾选上mysql8选项。 | If your database is mysql8，please add  '?serverTimezone=GMT' and select mysql8 option<br>
![image](https://github.com/kmaster/better-mybatis-generator/blob/master/image/mysql8-config.png)<br>
![image](https://github.com/kmaster/better-mybatis-generator/blob/master/image/mysql选项.png)<br>


