import timber.log.Timber
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

data class UPnPDevice(val name: String, val address: String, val port: Int) {
  fun buildAddress(): String {
    return "http://$address:$port/"
  }
}

sealed class UPnPDetectorResult {
  data class Success(val addresses: List<UPnPDevice>) : UPnPDetectorResult()
  object NoResults : UPnPDetectorResult()
  data class FatalError(val exception: Exception) : UPnPDetectorResult()
}

class UPnPDiscovery(val host: String = "239.255.255.250", val port: Int = 1900, val scanDuration: Int = 1000) {

  fun detect(): UPnPDetectorResult {
    val addresses = mutableListOf<UPnPDevice>()

    var socket: DatagramSocket? = null

    try {
      val group = InetAddress.getByName(host)

      val query = "M-SEARCH * HTTP/1.1\r\n" +
          "HOST: $host:$port\r\n" +
          "MAN: \"ssdp:discover\"\r\n" +
          "MX: 1\r\n" +
          "ST: urn:schemas-upnp-org:service:AVTransport:1\r\n" +
          "\r\n"

      socket = DatagramSocket(port).apply {
        reuseAddress = true
      }

      val time = System.currentTimeMillis()
      var curTime = System.currentTimeMillis()

      while (curTime - time < scanDuration) {

        val datagramPacket = DatagramPacket(query.toByteArray(), query.length, group, port)
        socket.send(datagramPacket)

        val packet = DatagramPacket(ByteArray(12), 12)
        socket.receive(packet)

        val protocol = String(packet.data, 0, packet.length)
        if (protocol.toUpperCase() == "HTTP/1.1 200") {
          addresses.add(UPnPDevice(packet.address.hostName, packet.address.hostAddress, packet.port))
        }

        curTime = System.currentTimeMillis()
      }

    } catch (exception: Exception) {
      Timber.e(exception)
      return UPnPDetectorResult.FatalError(exception)
    } finally {
      socket?.close()
    }

    return if (addresses.isEmpty()) {
      UPnPDetectorResult.NoResults
    } else {
      UPnPDetectorResult.Success(addresses)
    }
  }
}
