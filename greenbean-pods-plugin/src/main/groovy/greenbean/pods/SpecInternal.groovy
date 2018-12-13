package greenbean.pods

/**
 * 内部依赖描述文件，减少外部描述复杂度
 */
class SpecInternal {

    static String text = '''

    import xx
    @groovy.transform.BaseScript xx xx

    segment {
        on_off false
        name "sample"
        path "sample/path"
    }
    '''
}