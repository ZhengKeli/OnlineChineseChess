package org.zkl.teach.onlineChineseChess.web.servlet

import org.zkl.teach.onlineChineseChess.web.base.*
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "quitGame",urlPatterns = arrayOf("/api/quitGame.jsp"))
class QuitGameServlet: HttpServlet(){
	val resp_succeed = "succeed"
	override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			val gameToken = readToken(req)
			val game=GameData.getGameOrThrow(gameToken.gameId)
			
			val respJSON = JSONObject()
			quitGame(game, gameToken)
			
			respJSON.put(resp_succeed , true)
			respJSON.toString()
		}
	}
	
	companion object{
		fun quitGame(game: Game, gameToken: GameToken) {
			synchronized(game) {
				when (game.state) {
					GameState.infant -> GameData.deleteGame(game.id)
					GameState.playing -> {
						game.state = GameState.finished
						game.winner = gameToken.chessPlayer.getOpposite()
						game.version++
					}
					GameState.finished -> throw IllegalGameStateException("The game has been finished.")
				}
				Unit
			}
		}
	}
	
}