package pea.pod.extension;

public class PeaPodExt {

    def peaPods = new ArrayList<PeaPod>()

    PeaPodExt() {}

    def peaPods(peaPods) {
        this.peaPods = peaPods
    }

    def getPeaPods() {
        return peaPods
    }

    @Override
    public String toString() {
        return "PeaPodExt{" +
                "peaPods=" + peaPods +
                '}';
    }

    class PeaPod {

        def on_off
        def name
        def group
        def path
        def absPath
        def branch
        def cmd
        def excludes = new ArrayList<>()
        def buildTypes = new ArrayList<>()
        def seeds = new ArrayList<>()

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

        void absPath(String absPath) {
            this.absPath = absPath
        }

        void branch(String branch) {
            this.branch = branch
        }

        void cmd(String cmd) {
            this.cmd = cmd
        }

        void excludes(ArrayList<String> excludes) {
            this.excludes = excludes
        }

        void buildTypes(ArrayList<String> buildTypes) {
            this.buildTypes = buildTypes
        }

        void seeds(ArrayList<String> seeds) {
            this.seeds = seeds
        }

        @Override
        public String toString() {
            return "PeaPodExtension{" +
                    "on_off=" + on_off +
                    ", name='" + name + '\'' +
                    ", group='" + group + '\'' +
                    ", path='" + path + '\'' +
                    ", absPath='" + absPath + '\'' +
                    ", branch='" + branch + '\'' +
                    ", cmd='" + cmd + '\'' +
                    ", excludes=" + excludes +
                    ", buildTypes=" + buildTypes +
                    ", seeds=" + seeds +
                    '}';
        }
    }


}
