package app.xylune.chat.agent

import java.net.InetAddress

internal object PublicNetworkPolicy {
    fun isBlockedAddress(address: InetAddress): Boolean {
        if (address.isAnyLocalAddress || address.isLoopbackAddress || address.isLinkLocalAddress ||
            address.isSiteLocalAddress || address.isMulticastAddress
        ) return true
        val bytes = address.address
        if (bytes.size == 16) {
            val first = bytes[0].toInt() and 0xff
            if ((first and 0xfe) == 0xfc) return true // fc00::/7 unique-local addresses
            if (isIpv4Mapped(bytes)) return isBlockedIpv4(bytes.copyOfRange(12, 16))
        }
        return bytes.size == 4 && isBlockedIpv4(bytes)
    }

    private fun isBlockedIpv4(bytes: ByteArray): Boolean {
        val a = bytes[0].toInt() and 0xff
        val b = bytes[1].toInt() and 0xff
        return (a == 100 && b in 64..127) || // 100.64.0.0/10 carrier-grade NAT
            (a == 198 && b in 18..19) // 198.18.0.0/15 benchmarking/private test networks
    }

    private fun isIpv4Mapped(bytes: ByteArray): Boolean =
        bytes.size == 16 && bytes.take(10).all { it.toInt() == 0 } &&
            (bytes[10].toInt() and 0xff) == 0xff && (bytes[11].toInt() and 0xff) == 0xff
}
