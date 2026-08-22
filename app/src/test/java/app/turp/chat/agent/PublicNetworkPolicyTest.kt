package app.turp.chat.agent

import java.net.InetAddress
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicNetworkPolicyTest {
    @Test fun blocksLocalUlaCarrierNatAndBenchmarkRanges() {
        listOf("127.0.0.1", "10.1.2.3", "169.254.1.1", "100.64.0.1", "100.127.255.254", "198.18.0.1", "fc00::1", "fd12:3456::1")
            .forEach { literal -> assertTrue(literal, PublicNetworkPolicy.isBlockedAddress(InetAddress.getByName(literal))) }
    }

    @Test fun permitsRepresentativePublicAddresses() {
        listOf("8.8.8.8", "1.1.1.1", "2606:4700:4700::1111")
            .forEach { literal -> assertFalse(literal, PublicNetworkPolicy.isBlockedAddress(InetAddress.getByName(literal))) }
    }
}
