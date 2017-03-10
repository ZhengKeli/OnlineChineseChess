package org.zkl.onlineChineseChess.web.servlet

import org.zkl.onlineChineseChess.web.Configuration
import org.zkl.onlineChineseChess.web.base.GameData
import java.time.LocalDateTime
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import kotlin.concurrent.thread

@WebServlet(name = "maintain")
class MaintainServlet: HttpServlet(){
	
	var maintainThread: Thread? = null
	
	override fun init() {
		maintainThread= thread {
			println("maintain started up at "+ LocalDateTime.now())
			while (true) {
				val nowTime = System.currentTimeMillis()
				GameData.withGames { games->
					games.keys.forEach { key->
						val game = games[key]!!
						synchronized(game) {
							if (nowTime - game.expired > Configuration.insurerTime) {
								games.remove(key)
							}
						}
					}
				}
				println("maintain processed at "+ LocalDateTime.now())
				
				try {
					Thread.sleep(3600 * 1000)
				} catch (e: InterruptedException) {
					break
				}
			}
		}
	}
	
	override fun destroy() {
		maintainThread?.interrupt()
		maintainThread?.join()
		println("maintain shut down at "+ LocalDateTime.now())
	}
}