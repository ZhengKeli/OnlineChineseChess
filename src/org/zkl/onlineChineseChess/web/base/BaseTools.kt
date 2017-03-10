package org.zkl.onlineChineseChess.web.base

import org.zkl.onlineChineseChess.web.Configuration


fun newExpired(activeSpan:Long= Configuration.activeSpan): Long = System.currentTimeMillis() + activeSpan

