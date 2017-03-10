package org.zkl.onlineChineseChess.core


enum class ChessPlayer { red, black;
	fun getOpposite(): ChessPlayer {
		return when (this) {
			red -> black
			black -> red
		}
	}
}
enum class ChessType{
	/** 帅 */ shuai,
	/** 车 */ che,
	/** 马 */ ma,
	/** 象 */ xiang,
	/** 士 */ shi,
	/** 兵 */ bing,
	/** 炮 */ pao
}

data class Chess(val player: ChessPlayer, val type: ChessType)
data class ActionResult
@JvmOverloads constructor(val legal: Boolean=true,val won: Boolean=false)

interface Chessboard {
	
	/**
	 * 棋盘的行数
	 */
	val rowCount:Int get() = 10
	/**
	 * 棋盘的列数
	 */
	val columnCount:Int get() = 9
	
	/**
	 * 获取棋盘的某一处的棋子
	 * 若该处没有棋子则返回 `null`
	 * @return 棋盘的某一处的棋子，若该处没有棋子则返回 `null`
	 */
	operator fun get(row: Int, column: Int): Chess?
	/**
	 * 强制设置棋盘某处的棋子（不检验任何合法性）
	 * @param chess 要放到目标位置的棋子
	 */
	operator fun set(row: Int, column: Int, chess: Chess?)
	
	/**
	 * 正在走棋的玩家是哪个
	 * 再每次走棋之后其值应该自动变化
	 * （为了灵活性，这个可以被不检验合法性地设置）
	 */
	var actionPlayer: ChessPlayer
	/**
	 * 开局摆棋，
	 * 红在下，黑在上，
	 * 注意还要调整[actionPlayer]的值哦（红先走）
	 */
	fun initialSetup()
	/**
	 * 走棋
	 * 提供初始位置和目标位置，
	 * 执行过程中需要判断走法的合法性。
	 * 走棋成功后还要自动切换[actionPlayer]哦。
	 * @return 如果成功合法地执行了走棋命令则返回`true`，否则返回`false`
	 */
	fun action(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int): ActionResult
	
}


