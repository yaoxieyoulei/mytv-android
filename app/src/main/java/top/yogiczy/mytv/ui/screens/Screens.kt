package top.yogiczy.mytv.ui.screens

/**
 * 界面定义
 */
enum class Screens(
    private val args: List<String>? = null,
) {
    /**
     * 直播
     */
    Live();

    operator fun invoke(): String {
        val argList = StringBuilder()
        args?.let { nnArgs ->
            nnArgs.forEach { arg -> argList.append("/{$arg}") }
        }
        return name + argList
    }

    /**
     * 路由跳转携带参数
     */
    fun withArgs(vararg args: Any): String {
        val destination = StringBuilder()
        args.forEach { arg -> destination.append("/$arg") }
        return name + destination
    }
}
