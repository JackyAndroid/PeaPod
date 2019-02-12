## 绿豆荚

[ ![Download](https://api.bintray.com/packages/jackyjacky/maven/greenbean-pods-plugin/images/download.svg?version=0.0.1) ](https://bintray.com/jackyjacky/maven/greenbean-pods-plugin/0.0.1/link)

#### 绿豆荚是什么？

绿豆荚的起源是主工程中需要依赖大量的内部maven仓库，通常会有几十个。这时就有一个问题，如果主工程中maven仓库需要升级或者修复问题，那该怎么操作？一般就是先在maven仓库的git repo中修改，然后发布到内网的maven中心，然后再在主工程中升级maven仓库版本号，再验证问题是否修复，以及功能是否满足需求。

这么长的流程经常出现，那我们能不能直接在主工程中修改maven仓库？修复、发布功能的同时能直接在主工程验证？在[美团外卖架构演进](https://tech.meituan.com/2018/03/16/meituan-food-delivery-android-architecture-evolution.html)文章中有讲到类似的问题，如下：

![image-20190129101936404](https://ws1.sinaimg.cn/large/006tNc79gy1g03h7uja1uj31820u0ape.jpg)

绿豆荚依赖管理器也是实现类似的功能，一键把线上maven仓库切换为本地依赖，方便maven仓库新需求开发、问题修复。甚至之后可以实现类似iOS cocoapods依赖管理器功能，对全网的maven仓库实现一键切换源码。

提问：
1. [实际使用场景是什么？](https://github.com/JackyAndroid/GreenBeanPods/issues/1)
2. [实现原理是什么？](http://www.jackywang.tech/2018/05/03/%E6%BA%90%E7%A0%81%E4%BE%9D%E8%B5%96%E7%AE%A1%E7%90%86%E5%99%A8%EF%BC%88%E4%B8%80%EF%BC%89/)

#### 用法

**配置**

在setting.gradle中添加：

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'tech.jackywang:greenbean-pods-plugin:0.0.1'
    }
}

apply plugin: 'greenbean-pods-plugin'
```

在build.gradle中添加：

```groovy
buildscript {
    // ...
    dependencies {
        classpath 'tech.jackywang:greenbean-pods-plugin:0.0.1'
    }
}

allprojects {
    apply plugin: 'greenbean-pods-plugin'
    // ...
}
```

新建文件夹：greenbean.pods，然后新建配置文件：PodsSpec.groovy，内容如下：

```groovy
import greenbean.pods.GreenBean

@groovy.transform.BaseScript GreenBean pods

/**
 * 依赖描述文件
 * <pre>
 * {@code
 * pod{
 * on_off //模块开关
 * name   //模块名称
 * group  //模块Group
 * path   //模块本地路径，仅支持本工程父目录向下扩展
 * branch //模块仓库分支，根据path切换分支
 * cmd //命令hook，在include之前执行
 * excludes //模块的线上依赖名称
 * buildTypes //模块的构建类型
 * seeds //模块依赖的子模块，必须与子模块的name字段匹配
 *}*}
 * </pre>
 */

// 以下为示例内容，如果想动态调试app模块中的名为‘greenbean-pods-plugin-test-module’的线上模块，则先配置如下。
pod {
    on_off true
    name 'greenbean-pods-plugin-test-module'
    group 'tech.jackywang'
    path 'test-module/library'
}

pod {
    on_off true
    name 'app'
    seeds 'greenbean-pods-plugin-test-module'
}
```

配置完成，sync整个工程，线上模块将会以源码的方式包含进工程，实现源码调试模块的能力。

**配置前后图文对比：**

**配置前（线上依赖）**

配置文件：

![image-20190129105234167](https://ws3.sinaimg.cn/large/006tNc79gy1g03h8n9773j30ca07474s.jpg)

依赖的maven仓库：

![image-20190129105400191](https://ws2.sinaimg.cn/large/006tNc79gy1g03h950910j30an078aao.jpg)

**配置后（本地依赖）**

配置文件：

![image-20190129105604524](https://ws1.sinaimg.cn/large/006tNc79gy1g03h9l1jcqj30cs07bdgb.jpg)

依赖的maven仓库：

![image-20190129105815769](https://ws4.sinaimg.cn/large/006tNc79gy1g03h9tclg9j30b207q3z0.jpg)

通过配置文件的开关即可以一键切换maven仓库为源码依赖，实现源码调试。
