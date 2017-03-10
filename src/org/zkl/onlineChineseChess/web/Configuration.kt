package org.zkl.onlineChineseChess.web

import org.zkl.onlineChineseChess.core.ChessPlayer
import org.zkl.onlineChineseChess.core.Chessboard
import org.zkl.onlineChineseChess.core.YHChessboard

object Configuration{
	val hmacKey: ByteArray = "I'm HMAC key".toByteArray()
	val activeSpan: Long = 3600 * 1000 //一小时
	val insurerTime: Long = activeSpan
	
	val NEW_CHESSBOARD: () -> Chessboard
		= { YHChessboard() }
	val launcherPlayType = ChessPlayer.red
	
	
}
	
