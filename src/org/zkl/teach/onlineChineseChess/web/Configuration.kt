package org.zkl.teach.onlineChineseChess.web

import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.ChessPlayer
import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.Chessboard
import org.zkl.teach.onlineChineseChess.chessBoard.instant.YHChessboard

object Configuration{
	val hmacKey: ByteArray = "I'm HMAC key".toByteArray()
	val activeSpan: Long = 3600 * 1000 //一小时
	val insurerTime: Long = activeSpan
	
	val NEW_CHESSBOARD: () -> Chessboard
		= { YHChessboard() }
	val launcherPlayType = ChessPlayer.red
	
	
}
	
