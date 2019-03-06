package greenbean.pods

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware

/**
 * Green Bean Pods
 *
 * @since 2018.11.7
 * @author jacky
 */
class GreenBeanPods implements Plugin<PluginAware> {

    private String dslPath = "greenbean.pods/PodsSpec.groovy"

    @Override
    void apply(PluginAware pluginAware) {
        def isInclude = pluginAware instanceof Settings
        def isReplace = pluginAware instanceof Project
        if (isInclude) {
            includePods(pluginAware)
        } else if (isReplace) {
            replacePods(pluginAware)
        }
    }

    /**
     * 加载依赖描述
     */
    private void evaluateDSL(String path) {
        def shell = new GroovyShell(this.getClass().getClassLoader())
        // 加载内部DSL
        // shell.evaluate(SpecInternal.text)
        // 加载外部DSL
        File spec = new File(path)
        if (spec.exists()) {
            shell.evaluate(spec)
        } else {
            System.err.println "PodsSpec file not exists！path:" + path
        }
    }

    /**
     * Include 需要源码编译的模块
     * @param pluginAware
     * @return
     */
    void includePods(PluginAware pluginAware) {
        def settings = (Settings) pluginAware
        def rootDir = settings.rootDir
        evaluateDSL(rootDir.path + File.separator + dslPath)
        GreenBean.pods.each { pod ->
            if (pod.on_off) {
                if (pod.path == null && pod.absPath == null) {
                    return
                }
                def path
                if (pod.path != null) {
                    path = rootDir.getParent() + File.separator + pod.path
                } else {
                    path = pod.absPath
                }
                // cmd hook
                if (pod.cmd != null && !pod.cmd.isEmpty()) {
                    def proc = pod.cmd.execute()
                    def outputStream = new StringBuffer()
                    proc.waitForProcessOutput(outputStream, System.err)
                    println "执行Hook命令:" + pod.cmd + " 结果:" + outputStream.toString()
                }
                // 切换分支
                if (pod.branch != null && !pod.branch.isEmpty()) {
                    String cmd = "git -C " + path + " checkout " + pod.branch
                    def proc = cmd.execute()
                    def outputStream = new StringBuffer()
                    proc.waitForProcessOutput(outputStream, System.err)
                    println "模块:" + pod.name + " 检出分支:" + pod.branch + " 结果:" + outputStream.toString()
                }
                def projectName = ":" + pod.name
                settings.include(projectName)
                settings.project(projectName).projectDir = new File(path)
                println("被包含的模块 名字:" + pod.name + " 路径:" + path)
            }
        }
    }

    /**
     * 动态替换依赖
     * @param pluginAware
     * @return
     */
    void replacePods(PluginAware pluginAware) {
        def project = (Project) pluginAware
        evaluateDSL(project.rootDir.path + File.separator + dslPath)
        project.afterEvaluate {
            //寻找当前节点
            def currentItem = GreenBean.pods.find {
                project.name == it.name
            }
            if (currentItem == null) return
            // 寻找子节点
            List<String> seeds = currentItem.seeds
            if (seeds == null) return
            seeds.each { seedName ->
                // 寻找子节点
                def seed = GreenBean.pods.find {
                    it.name == seedName
                }
                if (seed == null) return
                if (seed.on_off) {
                    // 去除线上依赖
                    def excludeModule = ""
                    def excludeGroup = ""
                    if (seed.group != null) {
                        excludeGroup = seed.group
                    }
                    if (seed.excludes == null || seed.excludes.size() == 0) {
                        Map<String, String> map = new HashMap<>()
                        excludeModule = seed.name
                        map.put("module", excludeModule)
                        map.put("group", excludeGroup)
                        project.configurations.compile.exclude(map)
                    } else {
                        seed.excludes.each {
                            Map<String, String> map = new HashMap<>()
                            excludeModule = it
                            map.put("module", excludeModule)
                            map.put("group", excludeGroup)
                            project.configurations.compile.exclude(map)
                        }
                    }
                    println("Project:" + currentItem.name + " exclude online dependence,group:" + excludeGroup + " module:" + excludeModule)
                    // 添加本地依赖
                    StringBuilder buildTypes = new StringBuilder()
                    if (seed.buildTypes == null || seed.buildTypes.size() == 0) {
                        buildTypes.append("api")
                        project.dependencies.add(buildTypes.toString(), project.dependencies.project([path: ":" + seed.name]))
                    } else {
                        seed.buildTypes.each { buildType ->
                            buildTypes.append(buildType).append(" ")
                            if (project.configurations.findByName(buildType) != null) {
                                project.dependencies.add(buildType, project.dependencies.project([path: ":" + seed.name]))
                            } else {
                                System.err.println "Replace dependency error , build type " + buildType + " not exists."
                            }
                        }
                    }
                    println("Project:" + currentItem.name + " add local dependence,config name:" + buildTypes.toString() + " path:" + seed.name)
                }
            }
        }
    }
}