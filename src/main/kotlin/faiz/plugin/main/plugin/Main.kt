package faiz.plugin.main.plugin

import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.*
import kotlin.random.Random
class Main: JavaPlugin(),Listener {
    private var process = false
    private lateinit var seeker: String
    private val bar:BossBar = Bukkit.createBossBar("300초 남았습니다.",BarColor.BLUE,BarStyle.SEGMENTED_6)
    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,{
                                                             if (!process){
                                                                 removeSB()
                                                             }
        },0L,0L)
        setupKommands()
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        if (!process) return
        else{
            if (e.player.name == seeker){
                seeker = e.player.killer?.name.toString()
                e.deathMessage(Component.text("§l§b"+seeker+"님이 술래"+e.player.name+"님를 죽였습니다!"))
                Bukkit.getServer().sendMessage(Component.text("§l§b이제 술래는 "+ seeker+"입니다!"))
                Bukkit.getScoreboardManager().mainScoreboard.getObjective("hs")?.getScore(e.player.killer?.name.toString())?.score =+ 5
            }else{
                e.deathMessage(Component.text("§l§b술래 "+seeker+"님이 "+e.player.name+"님을 죽였습니다!"))
                Bukkit.getScoreboardManager().mainScoreboard.getObjective("hs")?.getScore(seeker)?.score =+ 1
            }
        }
    }

    private fun setupKommands() = kommand {
        register("술래잡기") {
            then("시작"){
                executes {
                    if (!sender.isOp) return@executes
                    if (!process){
                        process = true
                        val randomNumber = Random.nextInt(Bukkit.getOnlinePlayers().size)
                        val pl = server.onlinePlayers.toTypedArray()[randomNumber] as Player
                        seeker = pl.name
                        Bukkit.getServer().sendMessage(Component.text("§l§b술래는 " + seeker + "입니다!"))
                        setTimer()
                        setScoreBoard()
                    }else{
                        sender.sendMessage(Component.text("이미 실행중입니다!"))
                    }
                }
            }
        }
    }

    private fun setTimer(){
        var time = 300
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this,{
                if (time <= 0){
                    for (player in Bukkit.getOnlinePlayers()){
                        bar.removePlayer(player)
                        process = false
                        return@scheduleSyncRepeatingTask
                    }
                }

                bar.isVisible = true
                bar.setTitle(time.toString() + "초 남았습니다.")
                for (player in Bukkit.getOnlinePlayers()){
                    bar.addPlayer(player)
                }
                bar.progress = time/300.0
                time--
            },0L,20L)
        }

    private fun setScoreBoard(){
        val manager: ScoreboardManager = Bukkit.getScoreboardManager()
        val board: Scoreboard = manager.newScoreboard
        val obj: Objective = board.registerNewObjective("hs","dummy", Component.text("§l§b술래잡기"))
        obj.displaySlot = DisplaySlot.SIDEBAR
        for (player in Bukkit.getOnlinePlayers()){
            val score: Score = obj.getScore(player.name)
            score.score = 0
        }
        for (player in Bukkit.getOnlinePlayers()){
            player.scoreboard = board
        }
    }

    private fun removeSB(){
        val manager: ScoreboardManager = Bukkit.getScoreboardManager()
        val board: Scoreboard = manager.mainScoreboard
        board.objectives.remove(board.getObjective("hs"))
    }

}