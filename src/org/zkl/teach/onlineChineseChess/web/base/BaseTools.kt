package org.zkl.teach.onlineChineseChess.web.base

import org.zkl.teach.onlineChineseChess.web.Configuration


fun newExpired(activeSpan:Long= Configuration.activeSpan): Long = System.currentTimeMillis() + activeSpan

