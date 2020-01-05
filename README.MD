## 自动采集妹子图

> 作者：Ivey
>
> 主要是写逻辑，前端直接扒了OneIndex的，能看效果就行了 (*￣︶￣)

## 演示

![](https://pic.abcyun.co/image/5e11a94c0084b)
![](https://pic.abcyun.co/image/5e1099ecf14c4)
![](https://pic.abcyun.co/image/5e109a06c6db7)
![](https://pic.abcyun.co/image/5e109a10880b1)
![](https://pic.abcyun.co/image/5e1099f5ced24)
![](https://pic.abcyun.co/image/5e1099fe4b262)
![](https://pic.abcyun.co/image/5e1099e3c5ee7)

### 快速运行

* 安装必备工具
* JDK 1.8
* MySql 5.6+

### 创建目录

```
mkdir /javaweb/KanMeiMei/ && cd /javaweb/KanMeiMei/
```

### 下载&编译

* 自行编译

```
wget https://github.com/Hidove/KanMeiMei/archive/master.zip
unzip master.zip
mvn package
```

* 使用编译包

<https://github.com/Hidove/KanMeiMei/releases/>

### 导入数据库

`create database KanMeiMei;`
`use KanMeiMei;`
`source KanMeiMei.sql;`

### 运行项目

`java -jar KanMeiMei.jar`

* 后台运行

`nohup java -jar KanMeiMei.jar &` 

* 访问项目

<http://localhost:8080>

>图片资源已做映射

<http://localhost:8080/images/XXX/1.jpg>

* 配置自行搞定

> 需要修改 MySQL 和 服务器端口 等连接信息.
> 可自行在根目录创建`application.properties`文件
* application.properties
```
server.port=8080

#启动时是否获取整站的主题URL，推荐第一次使用运行一下
meimei.getHomePageList.start=true
#获取整站的主题URL时最大线程数
meimei.MaximumThreads=5
#meimei.image.savePath=相对路径
meimei.image.savePath=images/

##startPage和maxPage为0时不限制
#采集列表开始页
mm131.startPage=0
#采集列表最大页
mm131.maxPage=0

#是否以id命名文件夹 true以id命名，false以主题名命名
mm131.namedWithId=false
##定时任务
#定时下载
cron.download=0 0 0 * * ?
#定时获取imageUrl
cron.getImageUrl=0 0 0 * * ?
#定时获取首页更新数据
cron.getHomePageList=0 0 0 * * ?

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/meimei?serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=root

logging.file.max-size=2MB
logging.file.name=logs/meimei.log
logging.level.root=info
logging.file.path=logs
```
