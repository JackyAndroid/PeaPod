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

