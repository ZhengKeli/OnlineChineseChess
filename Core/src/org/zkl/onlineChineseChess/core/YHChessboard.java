package org.zkl.onlineChineseChess.core;


import org.jetbrains.annotations.NotNull;

/**
 * Created by dell1 on 2017/2/25.
 */
public class YHChessboard implements Chessboard {
	@Override
	public int getRowCount() {
		return 10;
	}
	
	@Override
	public int getColumnCount() {
		return 9;
	}
	
	ChessPlayer winnerPlayer = null;
	ChessPlayer actionPlayer = null;
	
	@NotNull
	@Override
	public ChessPlayer getActionPlayer() {
		return actionPlayer;
	}
	
	@Override
	public void setActionPlayer(@NotNull ChessPlayer chessPlayer) {
		this.actionPlayer = chessPlayer;
	}
	
	Chess[][] Map = new Chess[getColumnCount()][getRowCount()];
	
	public void initialSetup() {
		actionPlayer = ChessPlayer.red;
		
		for (int column = 0; column < getColumnCount(); column++) {
			for (int row = 0; row < getRowCount(); row++) {
				Map[column][row] = null;
			}
		}
		
		Map[0][0] = new Chess(ChessPlayer.black, ChessType.che);
		Map[1][0] = new Chess(ChessPlayer.black, ChessType.ma);
		Map[2][0] = new Chess(ChessPlayer.black, ChessType.xiang);
		Map[3][0] = new Chess(ChessPlayer.black, ChessType.shi);
		Map[4][0] = new Chess(ChessPlayer.black, ChessType.shuai);
		Map[5][0] = new Chess(ChessPlayer.black, ChessType.shi);
		Map[6][0] = new Chess(ChessPlayer.black, ChessType.xiang);
		Map[7][0] = new Chess(ChessPlayer.black, ChessType.ma);
		Map[8][0] = new Chess(ChessPlayer.black, ChessType.che);
		Map[1][2] = new Chess(ChessPlayer.black, ChessType.pao);
		Map[7][2] = new Chess(ChessPlayer.black, ChessType.pao);
		Map[0][3] = new Chess(ChessPlayer.black, ChessType.bing);
		Map[2][3] = new Chess(ChessPlayer.black, ChessType.bing);
		Map[4][3] = new Chess(ChessPlayer.black, ChessType.bing);
		Map[6][3] = new Chess(ChessPlayer.black, ChessType.bing);
		Map[8][3] = new Chess(ChessPlayer.black, ChessType.bing);
		
		Map[4][9] = new Chess(ChessPlayer.red, ChessType.shuai);
		Map[3][9] = new Chess(ChessPlayer.red, ChessType.shi);
		Map[5][9] = new Chess(ChessPlayer.red, ChessType.shi);
		Map[2][9] = new Chess(ChessPlayer.red, ChessType.xiang);
		Map[6][9] = new Chess(ChessPlayer.red, ChessType.xiang);
		Map[1][9] = new Chess(ChessPlayer.red, ChessType.ma);
		Map[7][9] = new Chess(ChessPlayer.red, ChessType.ma);
		Map[0][9] = new Chess(ChessPlayer.red, ChessType.che);
		Map[8][9] = new Chess(ChessPlayer.red, ChessType.che);
		Map[1][7] = new Chess(ChessPlayer.red, ChessType.pao);
		Map[7][7] = new Chess(ChessPlayer.red, ChessType.pao);
		Map[0][6] = new Chess(ChessPlayer.red, ChessType.bing);
		Map[2][6] = new Chess(ChessPlayer.red, ChessType.bing);
		Map[4][6] = new Chess(ChessPlayer.red, ChessType.bing);
		Map[6][6] = new Chess(ChessPlayer.red, ChessType.bing);
		Map[8][6] = new Chess(ChessPlayer.red, ChessType.bing);
	}
	
	public Chess get(int row, int column) {
		return Map[column][row];
	}
	
	public void set(int row, int column, Chess chess) {
		Map[column][row] = chess;
	}
	
	@NotNull
	public ActionResult action(int fromRow, int fromColumn, int toRow, int toColumn) {
		if (IsAbleToPut(fromColumn, fromRow, toColumn, toRow)) {
			if (actionPlayer == ChessPlayer.red) {
				actionPlayer = ChessPlayer.black;
			} else {
				actionPlayer = ChessPlayer.red;
			}
			Chess firstChess = get(fromRow,
				fromColumn);
			for (int column = 0; column < 9; column++) {
				for (int row = 0; row < 10; row++) {
					if (Map[column][row] != null && Map[column][row].getPlayer() == get(fromRow, fromColumn).getPlayer()) {
						if (get(fromRow, fromColumn).getPlayer() == ChessPlayer.red) {
							if (IsAbleToPut(column, row, 4, 0)) {
								Map[fromColumn][fromRow] = null;
								Map[toColumn][toRow] = firstChess;
								return new ActionResult(true, false);
							}
						} else if (get(fromRow, fromColumn).getPlayer() == ChessPlayer.black) {
							if (IsAbleToPut(column, row, 4, 9)) {
								Map[fromColumn][fromRow] = null;
								Map[toColumn][toRow] = firstChess;
								return new ActionResult(true, false);
							}
						}
					} else if (Map[column][row] != null && Map[column][row].getPlayer() != get(fromRow, fromColumn).getPlayer()) {
						if (get(fromRow, fromColumn).getPlayer() == ChessPlayer.black) {
							if (IsAbleToPut(column, row, 4, 0)) {
								Map[fromColumn][fromRow] = null;
								Map[toColumn][toRow] = firstChess;
								return new ActionResult(true, true);
							}
						} else if (get(fromRow, fromColumn).getPlayer() == ChessPlayer.red) {
							if (IsAbleToPut(column, row, 4, 9)) {
								Map[fromColumn][fromRow] = null;
								Map[toColumn][toRow] = firstChess;
								return new ActionResult(true, true);
							}
						}
					} else {
						Map[fromColumn][fromRow] = null;
						Map[toColumn][toRow] = firstChess;
						return new ActionResult(true, false);
					}
				}
				
			}
		}
		return new ActionResult(false, false);
	}
	
	public boolean IsAbleToPut(int x1, int y1, int x, int y) {
		ChessType oldChessType = Map[x1][y1].getType();
		if (oldChessType == ChessType.shuai) {
			if ((x - x1) * (y - y1) != 0) {
				return false;
			} else if (Math.abs(x - x1) > 1 || Math.abs(y - y1) > 1) {
				return false;
			} else if (x < 4 || x > 6 || (y > 3 && y < 8)) {
				return false;
			}
			return true;
		} else if (oldChessType == ChessType.shi) {
			if ((x - x1) * (y - y1) == 0) {
				return false;
			} else if (Math.abs(x - x1) > 1 || Math.abs(y - y1) > 1) {
				return false;
			} else return !(x < 4 || x > 6 || (y > 3 && y < 8));
		} else if (oldChessType == ChessType.xiang) {
			if ((x - x1) * (y - y1) == 0) {
				return false;
			} else if (Math.abs(x - x1) != 2 || Math.abs(y - y1) != 2) {
				return false;
			} else if (y <5) {
				return false;
			}
			int i = 0;
			int j = 0;
			if (x - x1 == 2) {
				i = x - 1;
			}
			if (x - x1 == -2) {
				i = x + 1;
			}
			if (y - y1 == 2) {
				j = y - 1;
			}
			if (y - y1 == -2) {
				j = y + 1;
			}
			return Map[i][j] == null;
		} else if (oldChessType == ChessType.ma) {
			if (Math.abs(x - x1) * Math.abs(y - y1) != 2) {
				return false;
			}
			if (x - x1 == 2) {
				if (Map[x - 1][y1] != null) {
					return false;
				}
			}
			if (x - x1 == -2) {
				if (Map[x + 1][y1] != null) {
					return false;
				}
			}
			if (y - y1 == 2) {
				if (Map[x1][y - 1] != null) {
					return false;
				}
			}
			if (y - y1 == -2) {
				if (Map[x1][y + 1] != null) {
					return false;
				}
			}
			return true;
		} else if (oldChessType == ChessType.che) {
			if ((x - x1) * (y - y1) != 0) {
				return false;
			}
			int oldx1=x1;
			int oldx=x;
			int oldy1=y1;
			int oldy=y;
			if (x1 != x) {
				if (x1 > x) {
					int c = x1;
					x1 = x;
					x = c;
				} for (int i = x1 + 1; i < x; i++) {
					if(Map[i][y1]!=null){
						return false;
					}
				}
				x1=oldx1;
				x=oldx;
				return true;
			}
			if (y != y1) {
				if (y1 > y) {
					int t = y1;
					y1 = y;
					y = t;
				} for (int j = y1 + 1; j < y; j++) {
					if (Map[x1][j] != null) {
						return false;
					}
				}
				y1=oldy1;
				y=oldy;
				return true;
			}
		} else if (oldChessType == ChessType.pao) {
			if ((x - x1) * (y - y1) != 0) {
				return false;
			}
			int c = 0;
			int oldx1 = x1;
			int oldx = x;
			int oldy1 = y1;
			int oldy = y;
			if (x != x1) {
				if (x1 > x) {
					int t = x1;
					x1 = x;
					x = t;
				}
				for (int i = x1 + 1; i < x; i++) {
					if (Map[i][y] != null) {
						c++;
					}
				}
				x1 = oldx1;
				x = oldx;
			}
			if (y1 != y) {
				if (y1 > y) {
					int t = y1;
					y1 = y;
					y = t;
				} else for (int j = y1; j < y; j++) {
					if (Map[x][j] != null) {
						c++;
					}
				}
				y = oldy;
				y1 = oldy1;
			}
			if (c > 1) {
				return false;
			}
			if (c == 0) {
				if (Map[x][y] != null) {
					return false;
				}
			}
			if (c == 1) {
				if (Map[x][y] == null) {
					return false;
				}
			}
			return true;
		} else if (oldChessType == ChessType.bing) {
			if ((x - x1) * (y - y1) != 0) {
				return false;
			}
			if (Math.abs(x - x1) > 1 || Math.abs(y - y1) > 1) {
				return false;
			}
			return !(y >= 5 && (x - x1) != 0);
		}
		return false;
	}
	
}
	
	
	

