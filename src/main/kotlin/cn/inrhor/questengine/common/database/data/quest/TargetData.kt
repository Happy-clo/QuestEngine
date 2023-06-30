package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.finishTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.matchMode
import cn.inrhor.questengine.common.record.QuestRecord
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 任务目标存储
 */
data class TargetData(
    override val id: String ="?",
    val questID: String = "?",
    var schedule: Int = 0,
    var state: StateType = StateType.DOING): QuestRecord.ActionFunc {

    constructor(questID: String, target: TargetFrame): this(target.id, questID)

    /**
     * @return 目标模块
     */
    fun getTargetFrame(): TargetFrame? {
        return id.getTargetFrame(questID)
    }

    fun load(player: Player) {
        if (state == StateType.DOING) {
            val target = getTargetFrame()?: return
            if (target.event.uppercase().startsWith("TASK ")) {
                target.task(player)
            }
        }
    }

    private fun TargetFrame.task(player: Player) {
        val quest = questID.getQuestFrame()
        val p = period.toLong()
        submit(delay = p, async = async, period = p) {
            if (!player.isOnline || state != StateType.DOING) {
                cancel(); return@submit
            }
            if (quest?.matchMode(player) == true && runEvalSet(quest.mode.type.objective(player), condition)) {
                player.finishTarget(this@TargetData, quest.mode.type)
            }
        }
    }

}