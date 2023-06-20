package cn.inrhor.questengine.common.dialog

enum class FlagDialog {
    KEEP_CONTENT, // 保持聊天对话
    BLINDNESS, // 失明
    SLOW, // 缓慢
    NO_CLEAR, // 对话结束后不清空聊天框
    NO_CACHE_CHAT, // 屏蔽聊天信息的在对话结束后，不返回历史聊天记录
    NO_SCREEN, // 不屏蔽聊天信息
    WS, // WS选择回复
    AD_CHOOSE, // AD确定回复,
    NO_END, // 选择回复不结束对话
}

fun List<String>.hasFlag(flagDialog: FlagDialog): Boolean = contains(flagDialog.toString())