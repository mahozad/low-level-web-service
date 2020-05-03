import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

private const val SERVER_PORT = 8000
private const val CONNECTION_END_FLAG = "END"

fun main() {
    val socket = Socket("localhost", SERVER_PORT)
    val input = DataInputStream(socket.getInputStream())
    val output = DataOutputStream(socket.getOutputStream())

    println(input.readUTF())


    output.writeUTF("S name Mahdi")
    println(input.readUTF())

    output.writeUTF("S initial M")
    println(input.readUTF())

    output.writeUTF("G")
    println(input.readUTF())


    output.writeUTF(CONNECTION_END_FLAG)
    socket.close()
}
