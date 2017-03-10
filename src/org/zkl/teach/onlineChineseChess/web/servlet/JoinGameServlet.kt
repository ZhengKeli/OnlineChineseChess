package org.zkl.teach.onlineChineseChess.web.servlet

import org.zkl.teach.onlineChineseChess.web.Configuration
import org.zkl.teach.onlineChineseChess.web.base.GameData
import org.zkl.teach.onlineChineseChess.web.base.GameState
import org.zkl.teach.onlineChineseChess.web.base.IllegalGameStateException
import org.zkl.teach.onlineChineseChess.web.base.createToken
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "joinGame",urlPatterns = arrayOf("/api/joinGame.jsp"))
class JoinGameServlet: HttpServlet(){
	val req_gameId = "gameId"
	val resp_ReenterWarn="reenterWarn"
	override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			val parameters=req.parameterMap
			val respJSON = JSONObject()
			
			val gameId=parameters.getIntOrThrow(req_gameId)
			val game=GameData.getGameOrThrow(gameId)
			synchronized(game) {
				//检查是否已有 gameToken
				var alreadyHasToken = false
				val oldToken = readTokenOrNull(req)
				if (oldToken != null) {
					if (oldToken.gameId == game.id) {
						//已有这个房间的 gameToken 了
						alreadyHasToken=true
					} else {
						//不是这个房间的 gameToken
						val oldGame = GameData.getGameOrNull(oldToken.gameId)
						if (oldGame != null) {
							synchronized(oldGame) {
								if (oldGame.state != GameState.finished) {
									//需要要退出原棋局
									if (parameters.getBooleanOfFalse(NewGameServlet.req_quitOldGame)) {
										QuitGameServlet.quitGame(oldGame, oldToken)
									}else{
										throw NewGameServlet.Companion.MultiGameException(oldGame)
									}
								}
							}
						}
					}
				}
				
				if (alreadyHasToken) {
					//已有 gameToken，发个信息给它
					respJSON.put(resp_ReenterWarn, true)
					
					// 那就更新一下 gameToken 吧
					val newToken = game.createToken(oldToken!!.chessPlayer)
					val tokenString = newToken.toTokenString()
					respJSON.put(resp_gameToken, tokenString)
					putTokenToCookie(resp, tokenString, newToken.expired)
				}
				else{
					//没有 gameToken 是新来的
					
					//检查 game 状态是否合适
					if (game.state != GameState.infant)
						throw IllegalGameStateException("The game has been joined by someone else.")
					
					//开始 game
					game.state=GameState.playing
					game.version++
					
					//返回 gameToken
					val gameToken=game.createToken(Configuration.launcherPlayType.getOpposite())
					val tokenString = gameToken.toTokenString()
					respJSON.put(resp_gameToken, tokenString)
					putTokenToCookie(resp, tokenString,gameToken.expired)
				}
			}
			
			respJSON.toString()
		}
	}
}

