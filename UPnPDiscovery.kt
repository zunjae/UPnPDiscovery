sealed class UPnPDiscoveryResult {
  data class Success(val addresses: HashSet<String>) : UPnPDiscoveryResult()
  data class FatalError(val exception: Exception) : UPnPDiscoveryResult()
}

class UPnPDiscovery(val host: String = "239.255.255.250", val port: Int = 1900) {

  fun detect(): UPnPDetectorResult {
    val addresses = HashSet<String>()

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

      val dataGramPacket = DatagramPacket(query.toByteArray(), query.length, group, port)
      socket.send(dataGramPacket)

      val packet = DatagramPacket(ByteArray(12), 12)
      socket.receive(packet)

      val protocol = String(packet.data, 0, packet.length)
      if (protocol.toUpperCase() == "HTTP/1.1 200") {
        addresses.add(packet.address.hostAddress)
      }

    } catch (exception: Exception) {
      Timber.e(exception)
      return UPnPDiscoveryResult.FatalError(exception)
    } finally {
      socket?.close()
    }

    return UPnPDiscoveryResult.Success(addresses)
  }
}
