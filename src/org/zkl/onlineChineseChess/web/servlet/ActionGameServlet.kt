package org.zkl.onlineChineseChess.web.servlet

import org.zkl.onlineChineseChess.web.base.GameData
import org.zkl.onlineChineseChess.web.base.GameState
import org.zkl.onlineChineseChess.web.base.IllegalGameStateException
import org.zkl.onlineChineseChess.web.base.createToken
import org.zkl.tools.java.data.json.JSONObject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "actionGame",urlPatterns = arrayOf("/api/actionGame.jsp"))
class ActionGameServlet : HttpServlet(){
	val req_fromRow="fromRow"
	val req_fromColumn="fromColumn"
	val req_toRow="toRow"
	val req_toColumn="toColumn"
	val resp_legal ="legal"
	val resp_won ="won"
	
	override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
		serveWithCover(resp) {
			val gameToken = readToken(req)
			
			val parameters=req.parameterMap
			val fromRow=parameters.getIntOrThrow(req_fromRow)
			val fromColumn=parameters.getIntOrThrow(req_fromColumn)
			val toRow=parameters.getIntOrThrow(req_toRow)
			val toColumn=parameters.getIntOrThrow(req_toColumn)
			
			val respJSON= JSONObject()
			val game = GameData.getGameOrThrow(gameToken.gameId)
			synchronized(game) {
				if (game.state != GameState.playing)
					throw IllegalGameStateException("You can only action when the game is playing.")
				
				val chessboard = game.chessboard
				val (legal,won) = chessboard.action(fromRow, fromColumn, toRow, toColumn)
				if (!legal) {
					respJSON.put(resp_legal, false)
				}else{
					game.version++
					respJSON.put(resp_legal, true)
					if (won) {
						game.state = GameState.finished
						game.winner = gameToken.chessPlayer
						respJSON.put(resp_won,true)
					}
				}
				
				//顺便更新 token
				val newGameToken = game.createToken(gameToken.chessPlayer)
				val tokenString = newGameToken.toTokenString()
				respJSON.put(resp_gameToken, tokenString)
				putTokenToCookie(resp, tokenString, newGameToken.expired)
				
				Unit
			}
			
			respJSON.toString()
		}
	}
	
	
}
