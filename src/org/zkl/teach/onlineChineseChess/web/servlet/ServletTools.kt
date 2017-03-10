package org.zkl.teach.onlineChineseChess.web.servlet

import org.zkl.teach.onlineChineseChess.web.base.GameToken
import org.zkl.teach.onlineChineseChess.web.base.InternalException
import org.zkl.teach.onlineChineseChess.web.base.WebException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


//exception cover
val resp_exception = "exception"
val resp_message = "message"
fun serveWithCover(resp: HttpServletResponse, runnable: () -> String) {
	resp.contentType = "text/html; charset=UTF-8";
	var toWrite:String
	try {
		toWrite= runnable()
	} catch (webException: WebException) {
		toWrite= webException.toResponseString()
	} catch (exception: Exception) {
		exception.printStackTrace()
		toWrite= InternalException().toResponseString()
	}
	resp.writer.write(toWrite)
}


//token
val resp_gameToken = "gameToken"
val req_gameToken = resp_gameToken
val cookie_gameToken = resp_gameToken
fun putTokenToCookie(resp: HttpServletResponse, tokenString: String,expired:Long) {
	val cookie = Cookie(cookie_gameToken, tokenString)
	cookie.maxAge = ((expired-System.currentTimeMillis())/1000).toInt()
	resp.addCookie(cookie)
}
fun readTokenFromCookie(request: HttpServletRequest):GameToken {
	val tokenString: String = request.cookies.firstOrNull { it.name == cookie_gameToken }!!.value
	return GameToken.fromTokenString(tokenString)
}
fun readToken(request: HttpServletRequest):GameToken{
	try {
		return readTokenFromCookie(request)
	} catch (e: Exception) {
		try {
			val tokenString = request.parameterMap.getStringOrThrow(req_gameToken)
			return GameToken.fromTokenString(tokenString)
		} catch (e: APIIncompleteException) {
			throw LackTokenException()
		}
	}
}
fun readTokenOrNull(request: HttpServletRequest): GameToken? {
	try {
		return readToken(request)
	} catch (e: WebException) {
		return null
	}
}
class LackTokenException : APIIncompleteException("","You need a gameToken to enter the game!")


//api
fun Map<String, Array<String>>.getStringOrThrow(key: String):String {
	return this.getOrElse(key) { throw APIIncompleteException(key) }[0]
}
fun Map<String, Array<String>>.getStringOrDefault(key: String, default: String): String {
	return this[key]?.get(0) ?: default
}
fun Map<String, Array<String>>.getIntOrThrow(key: String): Int {
	return getStringOrThrow(key).let { it.toIntOrNull() ?: throw APIIllegalException(key, it) }
}
fun Map<String, Array<String>>.getIntOfDefault(key: String,default:Int):Int{
	this[key]?.get(0)?.let {
		return it.toIntOrNull()?:throw APIIllegalException(key, it)
	}
	return default
}
fun Map<String, Array<String>>.getLongOrThrow(key: String): Long {
	return getStringOrThrow(key).let { it.toLongOrNull() ?: throw APIIllegalException(key, it) }
}
fun Map<String, Array<String>>.getLongOfDefault(key: String,default:Long):Long{
	this[key]?.get(0)?.let {
		return it.toLongOrNull()?:throw APIIllegalException(key, it)
	}
	return default
}
fun Map<String, Array<String>>.getBooleanOfFalse(key: String):Boolean{
	return this[key]?.get(0)?.toBoolean()?:false
}


open class APIException(message:String):WebException(message)
class APIMalformedException(message: String = "the request is malformed!"):APIException(message)
open class APIIncompleteException(
	itemName: String, message: String = "the request lacks an item $itemName!"):APIException(message)
class APIIllegalException(
	itemName: String="some argument", itemValue: String,
	message: String = "the value \"$itemValue\" of $itemName is not expected!"
):APIException(message)


