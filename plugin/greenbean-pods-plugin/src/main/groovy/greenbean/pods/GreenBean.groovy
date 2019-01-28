package greenbean.pods

/**
 * DSL 基础脚本
 *
 * @author jacky
 * @description Basic Script
 * @since 2018.11.7
 */
abstract class GreenBean extends Script {

    public static ArrayList<Pod> pods = new ArrayList<>()

    /**
     * 接收闭包函数
     * @param script the closure
     * @return
     */
    Pod pod(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Pod) Closure script) {
        script.resolveStrategy = Closure.DELEGATE_FIRST
        Pod pod = new Pod()
        script.delegate = pod
        script()
        addOrUpdatePods(script.delegate)
        return script.delegate
    }

    /**
     * 添加或者更新旧的Pod
     * @param newer the newer
     */
    private void addOrUpdatePods(Pod newer) {
        Pod older = pods.find { it.name == newer.name }
        if (older == null) {//add
            pods.add(newer)
        } else {//update
            if (newer.on_off != null) {
                older.on_off = newer.on_off
            }
            if (newer.name != null && !newer.name.isEmpty()) {
                older.name = newer.name
            }
            if (newer.group != null && !newer.group.isEmpty()) {
                older.group = newer.group
            }
            if (newer.path != null && !newer.path.isEmpty()) {
                older.path = newer.path
            }
            if (newer.branch != null && !newer.branch.isEmpty()) {
                older.branch = newer.branch
            }
            // cmd hook以外部DSL为准
            older.cmd = newer.cmd
            if (newer.excludes != null && newer.excludes.size() != 0) {
                older.excludes = newer.excludes
            }
            if (newer.buildTypes != null && newer.buildTypes.size() != 0) {
                older.buildTypes = newer.buildTypes
            }
            if (newer.seeds != null && newer.seeds.size() != 0) {
                older.seeds = newer.seeds
            }
            int index = pods.indexOf(older)
            pods.set(index, older)
        }
    }

    static class Pod {

        boolean on_off
        String name
        String group
        String path
        String branch
        String cmd
        ArrayList<String> excludes = new ArrayList<>()
        ArrayList<String> buildTypes = new ArrayList<>()
        ArrayList<String> seeds = new ArrayList<>()

        void on_off(boolean on_off) {
            this.on_off = on_off
        }

        void name(String name) {
            this.name = name
        }

        void group(String group) {
            this.group = group
        }

        void path(String path) {
            this.path = path
        }

        void branch(String branch) {
            this.branch = branch
        }

        void cmd(String cmd) {
            this.cmd = cmd
        }

        void excludes(String[] excludes) {
            this.excludes = excludes.toList()
        }

        void buildTypes(String[] types) {
            this.buildTypes = types.toList()
        }

        void seeds(String[] pods) {
            this.seeds = pods.toList()
        }

        @Override
        String toString() {
            String s = "Pod:"
            +"\n"
            +"on_off:$on_off"
            +"\n"
            +"name:$name"
            +"\n"
            +"group:$group"
            +"\n"
            +"path:$path"
            +"\n"
            +"excludes:${excludes.size()}"
            +"\n"
            +"build buildTypes:${buildTypes.size()}"
            +"\n"
            +"pods:${seeds.size()}"
            return s
        }
    }
}