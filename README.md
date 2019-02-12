## GreenBeanPods
[ ![Download](https://api.bintray.com/packages/jackyjacky/maven/greenbean-pods-plugin/images/download.svg?version=0.0.1) ](https://bintray.com/jackyjacky/maven/greenbean-pods-plugin/0.0.1/link)

[中文文档](https://github.com/JackyAndroid/GreenBeanPods/blob/master/README-CN.md)

#### What is GreenBeanPods?

The origin of greenbeanpods is that the main project needs to rely on a large number of internal maven repositories, usually there will be dozens. At this time, there is a problem. If the maven repository in the main project needs to be upgraded or fixed, what should I do?

Generally, it is first modified in the git repo of the maven repository, then released to the maven center of the intranet, and then upgrade the maven repository version number in the main project, and then verify whether the problem is fixed and whether the function meets the requirements.

Greenbeanpods can switch online maven repo to local dependencies with one click, which is convenient for maven repo new requirements development and problem repair. Even after we can implement the iOS cocoapods dependency manager function, the one-click switch source code is implemented for the maven repository of the whole network.

#### Usage

Add in gradle.properties: `greenbean_pods_version=0.0.1`

Add in setting.gradle:

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "tech.jackywang:greenbean-pods-plugin:${greenbean_pods_version}"
    }
}

apply plugin: 'greenbean-pods-plugin'
```

Add in build.gradle:

```groovy
buildscript {
    // ...
    dependencies {
        classpath "tech.jackywang:greenbean-pods-plugin:${greenbean_pods_version}"
    }
}

allprojects {
    apply plugin: 'greenbean-pods-plugin'
    // ...
}
```

Create a new folder：greenbean.pods，Then create a new configuration file：PodsSpec.groovy，Content is as follows：

```groovy
import greenbean.pods.GreenBean

@groovy.transform.BaseScript GreenBean pods

/**
 * Dependency description file
 * <pre>
 * {@code
 * pod{
 * on_off //Module switch
 * name   //Module name
 * group  //Module group
 * path   //Module local path, only supports the project parent directory to expand downward
 * branch //Module repository branch, switch branches according to path
 * cmd //Command hook, executed before include
 * excludes //Module's online dependency name
 * buildTypes //Module build type
 * seeds //Submodules that the module depends on must match the name field of the submodule
 *}*}
 * </pre>
 */

// The following is an example. If you want to dynamically debug the online module named ‘greenbean-pods-plugin-test-module’ in the app module, first configure the following.
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

When the configuration is completed, sync the entire project, the online module will be included in the project as a source code, and the ability to implement the source code debugging module.

