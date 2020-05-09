import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

private const val PORT = 8000
private val startTime = Instant.now()

fun main() {
    val serverSocket = ServerSocket(PORT)
    while (true) {
        val socket = serverSocket.accept()
        val client = Client(socket)
        val thread = Thread(client)
        thread.start()
    }
}

class Client(socket: Socket) : Runnable {

    private val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val output = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    override fun run() {
        val requestLine = input.readLine()
        val path = requestLine.split(" ")[1]
        dispatch(path)
    }

    private fun dispatch(path: String) = when (path) {
        "/" -> index()
        "/uptime" -> uptime()
        "/date" -> date()
        else -> error()
    }

    private fun index() {
        val payload = """
            / -> Index page
            /uptime -> Get service uptime
            /date -> Get current date
        """
        writeResponse(payload)
    }

    private fun uptime() {
        val duration = Duration.between(startTime, Instant.now())
        val payload = "${duration.seconds}s"
        writeResponse(payload)
    }

    private fun date() {
        val payload = "${LocalDate.now()}"
        writeResponse(payload)
    }

    private fun error() {
        val payload = "Error!"
        writeResponse(payload)
    }

    private fun writeResponse(payload: String) {
        output.appendln("HTTP/1.1 200 OK")
        output.appendln("Content-type: text/plain")
        output.appendln("Content-length: ${payload.toByteArray().size}")

        output.newLine() // Required extra newline between headers and payload

        output.write(payload)
        output.flush()
    }
}
