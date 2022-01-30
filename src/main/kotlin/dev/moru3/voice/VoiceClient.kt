package dev.moru3.voice

import com.iwebpp.crypto.TweetNaclFast
import dev.moru3.connector.GatewayConnector
import dev.moru3.connector.VoiceConnector
import dev.moru3.opcode.voice.ReadyStructure
import dev.moru3.opcode.voice.SelectProtocolStructure
import dev.moru3.opcode.voice.SessionDescriptionStructure
import dev.moru3.opcode.voice.identify.VoiceOpCode
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import kotlin.experimental.and


class VoiceClient(token: String, endpoint: String, gatewayConnector: GatewayConnector, userId: String, serverId: String, sessionId: String): VoiceConnector(token, endpoint, gatewayConnector, userId, serverId, sessionId) {
    private var keyBox: TweetNaclFast.SecretBox? = null

    private lateinit var secretKey: ByteArray

    override fun onSessionDescription(sessionDescriptionStructure: SessionDescriptionStructure) {
        println("シークレットキー！: ${sessionDescriptionStructure.secret_key.toMutableList()}")
        secretKey = sessionDescriptionStructure.secret_key
        keyBox = TweetNaclFast.SecretBox(sessionDescriptionStructure.secret_key)
    }

    override fun onReady(readyStructure: ReadyStructure) {
        runBlocking {
            val address = InetSocketAddress(readyStructure.ip, readyStructure.port)
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).udp().connect(address)
            thread {
                runBlocking {
                    val byteBuffer = ByteBuffer.allocate(70)
                    byteBuffer.putShort(1)
                    byteBuffer.putShort(70)
                    byteBuffer.putInt(readyStructure.ssrc)
                    socket.send(Datagram(ByteReadPacket(byteBuffer.array()),address))

                    val data = socket.receive().packet.readBytes()
                    val myIp = String(data, 4,data.size-6).trim { (it.digitToIntOrNull()?.let { return@trim false })?:return@trim it != '.' }
                    val port = ByteBuffer.wrap(data,data.size-2,2).short.toInt() and 0xffff
                    outGoing.send(VoiceOpCode.SELECT_PROTOCOL, SelectProtocolStructure("udp", myIp, port, "xsalsa20_poly1305"))
                    while(true) {
                        socket.incoming.tryReceive().getOrNull()?.also { packet ->
                            val bytes = packet.packet.readBytes().toMutableList().map { it.toInt() and 0xff }
                            val is_rtcp = 200 <= bytes[1] && bytes[1] < 205

                        }
                    }
                }
            }
        }
    }
}