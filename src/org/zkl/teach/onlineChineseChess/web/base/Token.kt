package org.zkl.teach.onlineChineseChess.web.base

import org.zkl.teach.onlineChineseChess.chessBoard.abstracts.ChessPlayer
import org.zkl.teach.onlineChineseChess.web.Configuration
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.jvm.internal.impl.utils.StringsKt


//session token
data class GameToken(
	val gameId: Int,
	val chessPlayer: ChessPlayer,
	val random: String = getRandom(),
	val expired: Long = newExpired()
){
	fun toTokenString() = Companion.toTokenString(this)
	
	companion object{
		private fun toRawString(gameToken: GameToken): String {
			val items= hashMapOf(
				GameToken::gameId.name to gameToken.gameId.toString(),
				GameToken::chessPlayer.name to gameToken.chessPlayer.name,
				GameToken::expired.name to gameToken.expired.toString(),
				GameToken::random.name to gameToken.random
			)
			return joinQueryString(items)
		}
		private fun fromRawString(rawString: String): GameToken {
			val items = parseQueryString(rawString)
			return GameToken(
				gameId = items[GameToken::gameId.name]!!.toInt(),
				chessPlayer = ChessPlayer.valueOf(items[GameToken::chessPlayer.name]!!),
				expired = items[GameToken::expired.name]!!.toLong(),
				random = items[GameToken::random.name]!!
			)
		}
		
		private fun toTokenString(gameToken: GameToken):String{
			val rawString = toRawString(gameToken)
			val shaResult = hmacSHA1Encrypt(rawString.toByteArray())
			val rawBytes = shaResult + rawString.toByteArray()
			return base64Encode(rawBytes)
		}
		@JvmStatic fun fromTokenString(tokenString:String): GameToken {
			val shaResult:ByteArray
			val rawStringBytes:ByteArray
			try {
				val rawBytes = base64Decode(tokenString)
				val shaResultSize = hmacSHA1EncryptResultSize
				shaResult = rawBytes.copyOfRange(0, shaResultSize)
				rawStringBytes = rawBytes.copyOfRange(shaResultSize, rawBytes.size)
			} catch (e: Exception) {
				throw TokenMalformedException()
			}
			
			if (!Arrays.equals(hmacSHA1Encrypt(rawStringBytes),shaResult))
				throw TokenTamperedException("the token is tempered!")
			
			val token: GameToken
			try {
				val rawString = String(rawStringBytes)
				token = fromRawString(rawString)
			}catch (e:Exception){
				throw TokenMalformedException()
			}
			
			if (token.expired < System.currentTimeMillis())
				throw TokenExpiredException()
			
			return token
		}
	}
}
@Synchronized fun Game.createToken(chessPlayer: ChessPlayer): GameToken {
	val token= GameToken(id, chessPlayer)
	resetExpired(chessPlayer,token.expired)
	return token
}

open class TokenInvalidException(message:String?="the token is invalid!") : WebException(message)
class TokenMalformedException(message:String?="the token is malformed!") : TokenInvalidException(message)
class TokenTamperedException(message:String?="the token is tempered!") : TokenInvalidException(message)
class TokenExpiredException(message:String?="the token is expired!") : TokenInvalidException(message)



//tools
fun parseQueryString(queryString: String): Map<String, String> {
	val itemsStrings= queryString.split("&")
	val map = HashMap<String, String>(itemsStrings.size)
	for(itemsString in itemsStrings) {
		val equalIndex = itemsString.indexOf("=")
		if(equalIndex==-1) continue
		val key = itemsString.substring(0, equalIndex)
		val value = itemsString.substring(equalIndex + 1, itemsString.length)
		map.put(key, value)
	}
	return map
}
fun joinQueryString(items:Map<String, String>):String{
	return StringsKt.join(items.map { "${it.key}=${it.value}" }, "&")
}

fun getRandom(): String = (Math.random() * 1000000).toInt().toString()


//crypto
fun base64Encode(byteArray: ByteArray): String {
	return Base64.getUrlEncoder().encodeToString(byteArray)
}
fun base64Decode(base64String: String): ByteArray {
	return Base64.getUrlDecoder().decode(base64String)
}

val messageDigest: MessageDigest = MessageDigest.getInstance("md5")
fun messageDigest(byteArray: ByteArray): ByteArray = messageDigest.digest(byteArray)

/**
 * HMAC-SHA1
 * @param byteArray 被签名的数据
 * @param encryptKey  密钥
 * @return 签名后的消化了的数据
 */
fun hmacSHA1Encrypt(byteArray: ByteArray, encryptKey: ByteArray=Configuration.hmacKey): ByteArray {
	//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
	val secretKey = SecretKeySpec(encryptKey, "HmacSHA1")
	//生成一个指定 Mac 算法 的 Mac 对象
	val mac = Mac.getInstance("HmacSHA1")
	//用给定密钥初始化 Mac 对象
	mac.init(secretKey)
	//完成 Mac 操作
	return mac.doFinal(byteArray)
}
val hmacSHA1EncryptResultSize = 20

