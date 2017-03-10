package org.zkl.teach.onlineChineseChess.test

import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.Chess
import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.ChessType
import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.Chessboard
import org.zkl.teach.onlineChineseChess.chessBoard.instant.YHChessboard


fun main(args:Array<String>){
	val chessboard: Chessboard = YHChessboard()
	chessboard.initialSetup()
	
	//如下可以将棋盘输出到控制栏
	chessboard.printSelf()
	
	val result=chessboard.action(1, 7, 7, 7)
	println(result)
	
	chessboard.printSelf()
	
}



fun Chessboard.printSelf(){
	val toPrint = StringBuilder()
	toPrint.append("\n")
	for (row in 0 until rowCount) {
		for (column in 0 until columnCount) {
			toPrint.append(" "+(this[row, column]?.toChinese() ?: "十"))
			
		}
		toPrint.append("\n")
	}
	println(toPrint.toString())
}

fun Chess.toChinese():String{
	return when (this.type) {
		ChessType.shuai -> "帅"
		ChessType.che -> "车"
		ChessType.ma -> "马"
		ChessType.xiang -> "象"
		ChessType.shi -> "士"
		ChessType.bing -> "兵"
		ChessType.pao -> "炮"
	}
}
