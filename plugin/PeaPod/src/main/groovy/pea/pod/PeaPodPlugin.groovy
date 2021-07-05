package pea.pod

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import pea.pod.extension.PeaPodExt

/**
 * PeaPodPlugin
 *
 * @author JackyWang since 2018.11.7
 */
class PeaPodPlugin implements Plugin<PluginAware> {

    def TAG = "PeaPodPlugin"
    def dslPath = "gradle/pea_pod.gradle"
    PeaPodExt peaPodExtension

    @Override
    void apply(PluginAware pluginAware) {
        peaPodExtension = project.getExtensions().create("PeaPod", PeaPodExt)
        if (pluginAware instanceof Settings) {
            includeStage(pluginAware)
        } else if (pluginAware instanceof Project) {
            projectStage(pluginAware)
        }
    }

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
    def includeStage(PluginAware pluginAware) {
        def settings = (Settings) pluginAware
        def rootDir = settings.rootDir
//        evaluateDSL(rootDir.path + File.separator + dslPath)
        peaPodExtension.peaPods.each { pod ->
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
                    Log.i(TAG, "exec hook cmd:" + pod.cmd + " result:" + outputStream.toString())
                }
                // checkout branch.
                if (pod.branch != null && !pod.branch.isEmpty()) {
                    String cmd = "git -C " + path + " checkout " + pod.branch
                    def proc = cmd.execute()
                    def outputStream = new StringBuffer()
                    proc.waitForProcessOutput(outputStream, System.err)
                    Log.i(TAG, "module:" + pod.name + " checkout:" + pod.branch + " result:" + outputStream.toString())
                }
                def projectName = ":" + pod.name
                settings.include(projectName)
                settings.project(projectName).projectDir = new File(path)
                Log.i(TAG, "included module name:" + pod.name + " path:" + path)
            }
        }
    }

    def projectStage(PluginAware pluginAware) {
        def project = (Project) pluginAware
//        evaluateDSL(project.rootDir.path + File.separator + dslPath)
        def peaPods = peaPodExtension.peaPods
        project.afterEvaluate {
            //寻找当前节点
            def currentNode = peaPods.find {
                project.name == it.name
            }
            if (currentNode == null) return
            // 寻找子节点
            List<String> seeds = currentNode.seeds
            if (seeds == null) return
            seeds.each { seedName ->
                // 寻找子节点
                def seed = peaPods.find {
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
//                        project.configurations.compile.exclude(map)
                        project.configurations.each {
                            it.exclude(map)
                        }
                    } else {
                        seed.excludes.each {
                            Map<String, String> map = new HashMap<>()
                            excludeModule = it
                            map.put("module", excludeModule)
                            map.put("group", excludeGroup)
                            project.configurations.each {
                                it.exclude(map)
                            }
                        }
                    }
                    Log.i(TAG, "Project:" + currentNode.name + " exclude online dependence, group:" + excludeGroup + " module:" + excludeModule)
                    // add local module.
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
                                Log.e(TAG, "Replace dependency error , build type " + buildType + " not exists.")
                            }
                        }
                    }
                    Log.i(TAG, "Project:" + currentNode.name + " add local dependence, build type:" + buildTypes.toString() + " path:" + seed.name)
                }
            }
        }
    }
}