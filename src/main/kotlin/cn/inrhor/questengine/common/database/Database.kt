package cn.inrhor.questengine.common.database

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.type.DatabaseLocal
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.database.type.DatabaseSQL
import cn.inrhor.questengine.common.database.type.DatabaseType
import cn.inrhor.questengine.common.dialog.DialogManager.quitDialog
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.*
import taboolib.common.platform.function.*

abstract class Database {

    /**
     * 为玩家拉取数据
     */
    abstract fun pull(player: Player)

    /**
     * 为玩家上载数据
     */
    abstract fun push(player: Player)

    /**
     * 清除任务数据
     */
    abstract fun removeQuest(player: Player, questID: String)

    /**
     * 创建任务数据
     */
    open fun createQuest(player: Player, questData: QuestData) {}

    companion object {

        lateinit var database: Database

        fun initDatabase() {
            database = when (DatabaseManager.type) {
                DatabaseType.MYSQL -> DatabaseSQL()
                else -> DatabaseLocal()
            }
        }

        @SubscribeEvent
        fun join(ev: PlayerJoinEvent) {
            playerPull(ev.player)
        }

        fun playerPull(player: Player) {
            val uuid = player.uniqueId
            val pData = PlayerData(uuid)
            DataStorage.addPlayerData(uuid, pData)
            database.pull(player)
        }

        @SubscribeEvent
        fun quit(ev: PlayerQuitEvent) {
            val p = ev.player
            database.push(p)
            val uuid = p.uniqueId
            p.quitDialog()
            DataStorage.removePlayerData(uuid)
        }

        @Awake(LifeCycle.DISABLE)
        fun cancel() {
            pushAll()
        }

        @Awake(LifeCycle.ACTIVE)
        fun updateDatabase() {
            submit(async = true, period = 200L) {
                pushAll()
            }
        }

        private fun pushAll() {
            Bukkit.getOnlinePlayers().forEach {
                database.push(it)
            }
        }

    }

}