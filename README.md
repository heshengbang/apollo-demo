# apollo-demo
Apollo部署添加多环境demo

本项目核心作用是实现多环境配置，因而项目本身并不重要，只是一个restful接口，用来验证环境切换以后配置也随之发生了变动

---

## 部署步骤
- 基本要求：部署支持开发dev和测试fat的两套配置
- 在[Apollo项目的release页面下载](https://github.com/ctripcorp/apollo/releases)三个包，分别是Apollo-portal、Apollo-configService、Apollo-adminService
    - 解压三个包
    - 将Apollo-configService、Apollo-adminService各复制一份，并分别冠以-dev以及-fat的后缀
    - 修改后的目录结构应当如下：
    ```
        - apollo-portal
        | - dev
            | - Apollo-configService-dev
            | - Apollo-configService-dev
        | - fat
            |- Apollo-configService-fat
            |- Apollo-configService-fat
    ```

- 从[另一个仓库](https://github.com/nobodyiam/apollo-build-scripts/tree/master/sql)中获取快捷部署的sql文件，分别是apolloconfigdb.sql、apolloportaldb.sql
    - 在mysql的可视化工具上，例如: Navicat for mysql 工具上执行apolloportaldb.sql
    - 将apolloconfigdb.sql中的database名改为ApolloConfigDB_dev和ApolloConfigDB_fat分别执行一次，为dev和fat分别创建配置地址

- dev部署
    - 进入Apollo-configService-dev中：
        - startup.sh中修改端口为8082，避免端口冲突
        - 修改数据库连接信息，将用户名和密码，以及url均改为本地的数据库对应信息，此外url中的database改为ApolloConfigDB_dev
    - 进入Apollo-adminService-dev中：
        - startup.sh中修改端口为8092，避免端口冲突
        - 修改数据库连接信息，将用户名和密码，以及url均改为本地的数据库对应信息，此外url中的database改为ApolloConfigDB_dev
    - 进入数据库ApolloConfigDB_dev中，找到表serverconfig，修改其中的eureka.service.url，将Eureka端口改为8082，对应前面Apollo-configService-dev中的端口
        - 之所以Apollo-configService-dev的端口和Eureka对应，是因为在Apollo的架构中，Apollo-configService即是metaServer也是Eureka、也是configService，该模块同时具备三个逻辑角色于一体
    - 通过Apollo-configService-dev目录下的scripts中的startup.sh启动Apollo-configService-dev，成功后访问localhost:8082，此时应当可以看到服务本身也注册成功了
    - 通过Apollo-adminService-dev目录下的scripts中的startup.sh启动Apollo-adminService-dev，启动成功后，访问localhost:8082中应当有该服务的注册信息，此外访问localhost:8092应当可以访问成功
    
- 至此，dev环境已部署成功，并且可以向其他应用提供配置服务
- 修改数据库ApolloPortalDB中serverconfig表中的apollo.portal.envs，将`dev`改为`dev,fat`，表示新增fat环境
- 修改apollo-portal中的信息：
    - 将config目录下的application-github.properties中的数据库信息改为本地对应的数据库信息
    - 将config目录下的apollo-env.properties中的dev.meta改为localhost:8082
    - 将config目录下的apollo-env.properties中的fat.meta改为localhost:8084
- 启动Apollo-portal，访问localhost:8070，用户名和密码分别是apollo和admin
    - 进入系统能看到当前拥有的环境是dev
    
- 按照dev的部署模式，将Apollo-configService-fat、Apollo-adminService-fat中的信息分别修改并启动，就能在apollo-portal中看到对应的fat信息
- [在spring-boot项目中集成apollo详见](https://github.com/ctripcorp/apollo/wiki/Java%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97#3213-spring-boot%E9%9B%86%E6%88%90%E6%96%B9%E5%BC%8F%E6%8E%A8%E8%8D%90)，本项目同样采用了该方式进行集成
    - 添加依赖
    - 增加配置文件
        - 核心是以下四条信息
        ```properties
        apollo.meta=http://localhost:8084
        apollo.bootstrap.enabled = true
        app.id=SampleApp
        apollo.autoUpdateInjectedSpringProperties=false
        ```
    - 在项目中使用对应配置

- 启动本项目，通过restful接口访问，不断修改apollo-portal页面上的对应apollo.meta的值，从页面上值的变化，可以看出，配置中心搭建成功
- 本项目只是一个简单的demo，用于快速上手apollo同时可以帮助理解它的设计和架构，生产上落地apollo应当要整合自己的权限系统以及Eureka

## 相关连接
- [apollo wiki](https://github.com/ctripcorp/apollo/wiki)
- [携程Apollo配置中心架构剖析](https://mp.weixin.qq.com/s/-hUaQPzfsl9Lm3IqQW3VDQ)
