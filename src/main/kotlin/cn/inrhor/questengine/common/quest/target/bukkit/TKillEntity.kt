package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget
import cn.inrhor.questengine.script.kether.runEval

import cn.inrhor.questengine.utlis.subAfter
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent

/**
 * 重写
 */
object TKillEntity: TargetExtend<EntityDeathEvent>() {

    override val name = "player kill entity"

    init {
        event = EntityDeathEvent::class
        tasker {
            val player = entity.killer?: return@tasker null
            player.triggerTarget(name) { _, pass ->
                val entityTypes = pass.entityTypes
                val name = pass.name
                pass.exp >= droppedExp &&
                        (pass.entityTypes.isEmpty() || entityTypes.contains(entityType.name)) &&
                        (name.isEmpty() || name == entity.)
            }
            /*player.doingTargets(name).forEach {
                val target = it.getTargetFrame()?: return@forEach
                if (checkEntity(target, entityType) && checkCondition(player, target, entity, droppedExp)) {
                    Schedule.isNumber(player, "number", it)
                }
            }*/
            player
        }
    }

    private fun checkEntity(target: TargetFrame, type: EntityType): Boolean {
        val condition = target.nodeMeta("entity")?: return true
        return condition.contains(type.name)
    }

    private fun checkCondition(player: Player, target: TargetFrame, entity: Entity, dropExp: Int): Boolean {
        val condition = target.nodeMeta("condition")
        if (condition.isEmpty()) return true
        val checkNumber = target.nodeMeta("check", "0")[0].toInt()
        var i = 0
        condition.forEach {
            if (!checkNumber(checkNumber, i)) return true
            val s = it.lowercase()
            if (s.startsWith("entity name")) {
                val get = it.subAfter("@")
                if (!matchName(player, entity, get)) return false
            }else if (s.startsWith("eval ")) {
                val get = it.subAfter("@").replace("dropExp", dropExp.toString(), true)
                if (!runEval(player, get)) return false
            }
            i++
        }
        return true
    }

    fun matchName(player: Player, entity: Entity, str: String): Boolean {
        if (runEval(player, "strMatch type $str *'${entity.name}'")) return true
        if (runEval(player, "strMatch type $str *'${entity.customName}'")) return true
        return false
    }

    private fun checkNumber(check: Int, forEach: Int): Boolean {
        if (check <= 0 || check >= forEach) return true
        return false
    }

}