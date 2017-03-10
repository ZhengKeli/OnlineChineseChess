package org.zkl.teach.onlineChineseChess.web.servlet

import org.zkl.teach.onlineChineseChess.web.Configuration
import org.zkl.teach.onlineChineseChess.web.base.*
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "newGame",urlPatterns = arrayOf("/api/newGame.jsp"))
class NewGameServlet: HttpServlet(){
	val req_name = "name"
	override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			val parameters = req.parameterMap
			val respJSON = JSONObject()
			
			
			//检查是否有旧的 gameToken
			val oldToken = readTokenOrNull(req)
			if (oldToken != null) {
				val oldGame = GameData.getGameOrNull(oldToken.gameId)
				if (oldGame != null) {
					synchronized(oldGame) {
						if (oldGame.state != GameState.finished) {
							//要退出旧的Game
							if (parameters.getBooleanOfFalse(req_quitOldGame)) {
								QuitGameServlet.quitGame(oldGame, oldToken)
							} else {
								throw MultiGameException(oldGame)
							}
						}
					}
				}
			}
			
			
			//先锁game
			val newGame = GameData.newGame()
			synchronized(newGame) {
				//对game做初始化
				newGame.name = parameters.getStringOrDefault(req_name, newGame.name)
				newGame.chessboard.initialSetup()
				newGame.version = 1
				
				//返回 gameToken
				val newGameToken = newGame.createToken(Configuration.launcherPlayType)
				val tokenString = newGameToken.toTokenString()
				respJSON.put(resp_gameToken, tokenString)
				putTokenToCookie(resp, tokenString,newGameToken.expired)
			}
			
			respJSON.toString()
		}
	}
	
	companion object{
		val resp_oldGameId = "oldGameId"
		val resp_oldGameName = "oldGameName"
		val req_quitOldGame = "quitOldGame"
		class MultiGameException(
			val oldGame:Game,
			message: String?="You've took part in an other game. " +
				"Send request with argument \"$req_quitOldGame\" with value true to quit it."
		) :WebException(message){
			override fun toResponseJSON(): JSONObject {
				val respJSON = super.toResponseJSON()
				respJSON.put(resp_oldGameId, oldGame.id)
				respJSON.put(resp_oldGameName, oldGame.name)
				return respJSON
			}
		}
	}
}

