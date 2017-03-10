package org.zkl.onlineChineseChess.web.base

import org.zkl.onlineChineseChess.core.ChessPlayer
import org.zkl.onlineChineseChess.core.Chessboard
import org.zkl.onlineChineseChess.web.Configuration
import java.util.*


internal object GameData {
	private var nextId: Int = 0
	private val games: MutableMap<Int, Game> = HashMap<Int, Game>()
	@Synchronized inline fun withGames(action:(MutableMap<Int, Game>)->Unit){
		action(games)
	}
	@Synchronized fun getGameOrNull(id:Int): Game? = games[id]
	@Synchronized fun getGameOrThrow(id:Int): Game = games[id] ?: throw GameNotFoundException(id)
	@Synchronized fun newGame(chessboard: Chessboard = Configuration.NEW_CHESSBOARD()): Game {
		nextId++
		val game = Game(nextId, chessboard)
		games.put(nextId, game)
		return game
	}
	@Synchronized fun deleteGame(id:Int): Game? {
		return games.remove(id)
	}
}
class GameNotFoundException(id: Int, message: String = "The game of id $id was not found!") : WebException(message)



enum class GameState{ infant,playing,finished }
class IllegalGameStateException(message: String?="The game state is unexrpected!") : WebException(message)
class Game internal constructor(val id: Int, val chessboard: Chessboard, var name:String="game$id"){
	var version:Long =0
	
	val expired: Long get() = Math.max(redExpired, blackExpired)
	var redExpired:Long = newExpired()
	var blackExpired:Long = redExpired
	fun resetExpired(chessPlayer: ChessPlayer? = null, expired: Long = newExpired()) {
		when (chessPlayer) {
			ChessPlayer.red -> redExpired = expired
			ChessPlayer.black -> blackExpired = expired
			null->{
				redExpired = expired
				blackExpired = expired
			}
		}
	}
	
	var state: GameState = GameState.infant
	val actionPlayer get() = chessboard.actionPlayer
	var winner: ChessPlayer?=null
}

