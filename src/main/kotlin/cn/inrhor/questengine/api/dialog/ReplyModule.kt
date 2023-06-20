package cn.inrhor.questengine.api.dialog


/**
 * 回复属性模块
 */
data class ReplyModule(
    val replyID: String = "reply",
    val tagDefault: String = "",
    val tagChoose: String = "",
    val condition: String = "",
    val content: List<String> = emptyList(),
    val script: String = "",
    val cases: MutableList<LogicModule> = mutableListOf()
)