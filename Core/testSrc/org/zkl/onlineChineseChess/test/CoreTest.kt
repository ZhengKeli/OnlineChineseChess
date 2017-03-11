package org.zkl.onlineChineseChess.test

import org.zkl.onlineChineseChess.core.Chess
import org.zkl.onlineChineseChess.core.ChessType
import org.zkl.onlineChineseChess.core.Chessboard
import org.zkl.onlineChineseChess.core.YHChessboard


fun main(args:Array<String>){
	val chessboard: Chessboard = YHChessboard()
	chessboard.initialSetup()
	
	//如下可以将棋盘输出到控制栏
	chessboard.printSelf()
	
	println(chessboard.action(6, 0, 5, 0))
	
	chessboard.printSelf()
	
	println(chessboard.action(3, 0, 4, 0))
	
	chessboard.printSelf()
	
}



fun Chessboard.printSelf(){
	val toPrint = StringBuilder()
	toPrint.append("\n")
	toPrint.append("\n   零 一 二 三 四 五 六 七 八\n")
	for (row in 0 until rowCount) {
		toPrint.append("$row ")
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
