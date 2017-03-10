package org.zkl.onlineChineseChess.web.base

import org.zkl.onlineChineseChess.web.servlet.resp_exception
import org.zkl.onlineChineseChess.web.servlet.resp_message
import org.zkl.tools.java.data.json.JSONObject


open class WebException(message:String?):Exception(message){
	open fun toResponseJSON(): JSONObject {
		val respJSON = JSONObject()
		respJSON.put(resp_exception,this.javaClass.simpleName)
		respJSON.put(resp_message, this.message)
		return respJSON
	}
	open fun toResponseString(): String = toResponseJSON().toString()
	
}

class InternalException(message: String = "Sorry! An internal exception occurred!")
	: WebException(message)

