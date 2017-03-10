package org.zkl.teach.onlineChineseChess.web.servlet

import org.zkl.teach.onlineChineseChess.web.base.Game
import org.zkl.teach.onlineChineseChess.web.base.GameData
import org.zkl.teach.onlineChineseChess.web.base.GameState
import org.zkl.tools.java.data.json.JSONArray
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "queryGames",urlPatterns = arrayOf("/api/queryGames.jsp"))
class QueryGamesServlet: HttpServlet(){
	val req_offset = "offset"
	val req_limit = "limit"
	val resp_games = "games"
	val resp_game_id = "id"
	val resp_game_name = "name"
	val resp_oldGameId = "oldGameId"
	val resp_oldGameName = "oldGameName"
	override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			val parameters = req.parameterMap
			val respJSON = JSONObject()
			
			val offset = parameters.getIntOfDefault(req_offset, 0)
			val limit = parameters.getIntOfDefault(req_limit, 20)
			val returnGames = ArrayList<Game>()
			var scanned = 0
			GameData.withGames { games->
				for ((_, game) in games) {
					if (game.state != GameState.infant) continue
					if (scanned < offset) continue
					returnGames.add(game)
					scanned++
					if (returnGames.size >= limit) break
				}
			}
			val gamesJSA=JSONArray().also { gamesJSA->
				returnGames.forEach { game->
					synchronized(game) {
						val gameJSON = JSONObject()
						gameJSON.put(resp_game_id, game.id)
						gameJSON.put(resp_game_name, game.name)
						gamesJSA.put(gameJSON)
					}
				}
			}
			respJSON.put(resp_games, gamesJSA)
			
			val token=readTokenOrNull(req)
			if (token != null) {
				val game = GameData.getGameOrNull(token.gameId)
				if (game != null) {
					respJSON.put(resp_oldGameId, game.id)
					respJSON.put(resp_oldGameName, game.name)
				}
			}
			
			respJSON.toString()
		}
	}
}
