import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.time.Duration
import java.time.Instant

private const val PORT = 8000
private const val CONNECTION_END_FLAG = "END"
private val startTime = Instant.now()
private val clients = mutableMapOf<Int, MutableList<Pair<String, String>>>()
private var clientsCount: Int = 0

fun main() {
    val serverSocket = ServerSocket(PORT)
    while (true) {
        clientsCount++
        val socket = serverSocket.accept()
        val client = Client(socket, clientsCount)
        val thread = Thread(client)
        thread.start()
    }
}

class Client(private val socket: Socket, private val clientNumber: Int) : Runnable {

    private val input = DataInputStream(socket.getInputStream())
    private val output = DataOutputStream(socket.getOutputStream())

    override fun run() {
        clients[clientNumber] = mutableListOf()

        output.writeUTF(""" 
            U -> Get uptime (send U)
            G -> Get current data (send G)
            S -> Store a data pair (send S followed by a pair of values; separate by space)
            D -> Delete a data pair (send D followed by the first value in the desired pair)
        """)

        var message = input.readUTF()
        while (message != CONNECTION_END_FLAG) {
            when (message.first().toUpperCase()) {
                'U' -> outputUptime()
                'G' -> outputData()
                'S' -> storeData(message)
                'D' -> deleteData(message)
                else -> output.writeUTF("Bad command!")
            }
            message = input.readUTF()
        }

        socket.close()
    }

    private fun outputUptime() {
        val duration = Duration.between(startTime, Instant.now())
        output.writeUTF("${duration.seconds}s")
    }

    private fun outputData() {
        val data = clients.getValue(clientNumber)
        output.writeUTF("$data")
    }

    private fun storeData(message: String) {
        val (first, second) = message.removeRange(0, 2).split(' ')
        val pair = Pair(first, second)
        clients.getValue(clientNumber).add(pair)
        output.writeUTF("Stored $pair")
    }

    private fun deleteData(message: String) {
        val key = message.removeRange(0, 2)
        clients.getValue(clientNumber).removeIf { it.first == key }
        output.writeUTF("Deleted the pair with key $key")
    }
}
