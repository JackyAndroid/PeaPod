## GreenBeanPods
[ ![Download](https://api.bintray.com/packages/jackyjacky/maven/greenbean-pods-plugin/images/download.svg?version=0.0.1) ](https://bintray.com/jackyjacky/maven/greenbean-pods-plugin/0.0.1/link)

[中文文档](https://github.com/JackyAndroid/GreenBeanPods/blob/master/README-CN.md)

#### What is GreenBeanPods?

Android relies on third-party repositories as binary dependencies. If we want to repair or add features to the repository, we have to publish the repositories, and then update the repositories version number at the business side to verify. This process is sometimes repeated frequently.

It is to solve this problem

#### Usage

Add in setting.gradle:

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

Add in build.gradle:

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

