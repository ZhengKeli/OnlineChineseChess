package org.zkl.onlineChineseChess.web.servlet

import org.zkl.onlineChineseChess.web.base.GameData
import org.zkl.onlineChineseChess.web.base.GameState
import org.zkl.onlineChineseChess.web.base.createToken
import org.zkl.tools.java.data.json.JSONArray
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@WebServlet(name = "refreshGame",urlPatterns = arrayOf("/api/refreshGame.jsp"))
class RefreshGameServlet : HttpServlet(){
	
	val req_latestVersion = "latestVersion"
	val resp_needRefresh = "needRefresh"
	val resp_game = "game"
	val resp_game_id = "id"
	val resp_game_version = "version"
	val resp_game_name = "name"
	val resp_game_state = "state"
	val resp_game_player ="player"
	val resp_game_actionPlayer ="actionPlayer"
	val resp_game_winner = "winner"
	val resp_game_chessboard = "chessboard"
	val resp_chess_row = "row"
	val resp_chess_column = "column"
	val resp_chess_type = "type"
	val resp_chess_player = "player"
	
	override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			//读取api参数
			val parameters = req.parameterMap
			val latestVersion = parameters.getIntOfDefault(req_latestVersion, 0)
			
			//解析并检验token
			val gameToken = readToken(req)
			val game = GameData.getGameOrThrow(gameToken.gameId)
			
			//组织返回
			val respJSON = JSONObject()
			//为了防止线程冲突，先锁上
			synchronized(game) {
				if (latestVersion >= game.version) {
					//没变化，不用管
					respJSON.put(resp_needRefresh, false)
				}
				else {
					//已经有变化了
					respJSON.put(resp_needRefresh, true)
					
					//加入 game 的信息
					val gameJSON = JSONObject().also { respJSON.put(resp_game,it) }
					gameJSON.put(resp_game_id, game.id)
					gameJSON.put(resp_game_version, game.version)
					gameJSON.put(resp_game_name, game.name)
					gameJSON.put(resp_game_state, game.state.name)
					gameJSON.put(resp_game_player, gameToken.chessPlayer.name)
					gameJSON.put(resp_game_actionPlayer, game.actionPlayer.name)
					if(game.state==GameState.finished)
						gameJSON.put(resp_game_winner, game.winner?.name)
					
					//加入棋盘信息
					val chessBoard=game.chessboard
					val chessBoardJSA = JSONArray().also { gameJSON.put(resp_game_chessboard, it) }
					for (row in 0 until chessBoard.rowCount) {
						for (column in 0 until chessBoard.columnCount) {
							chessBoard[row, column]?.let { (playerType, type) ->
								val chessJSON = JSONObject().also { chessBoardJSA.put(it) }
								chessJSON.put(resp_chess_row, row)
								chessJSON.put(resp_chess_column, column)
								chessJSON.put(resp_chess_type, type.name)
								chessJSON.put(resp_chess_player, playerType.name)
							}
						}
					}
					
					
				}
				
				//更新token （此时会顺便更新 game 的 expired）
				val newToken=game.createToken(gameToken.chessPlayer)
				val tokenString = newToken.toTokenString()
				respJSON.put(resp_gameToken, tokenString)
				putTokenToCookie(resp, tokenString, newToken.expired)
				
			}
			
			respJSON.toString()
		}
	}
	
}
