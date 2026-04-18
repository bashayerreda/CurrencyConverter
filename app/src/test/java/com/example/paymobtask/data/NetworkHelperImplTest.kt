package com.example.paymobtask.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.paymobtask.domain.NetworkHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class NetworkHelperImplTest {

    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var network: Network
    private lateinit var capabilities: NetworkCapabilities
    private lateinit var networkHelper: NetworkHelper

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        connectivityManager = mock(ConnectivityManager::class.java)
        network = mock(Network::class.java)
        capabilities = mock(NetworkCapabilities::class.java)
        networkHelper = NetworkHelperImpl(context)
    }

    @Test
    fun `isConnected returns false when connectivity manager is null`() {
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null)
        val result = networkHelper.isConnected()
        assertFalse(result)
    }

    @Test
    fun `isConnected returns false when active network is null`() {
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(null)
        val result = networkHelper.isConnected()
        assertFalse(result)
    }

    @Test
    fun `isConnected returns false when capabilities are null`() {
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(null)
        val result = networkHelper.isConnected()
        assertFalse(result)
    }

    @Test
    fun `isConnected returns true when wifi transport is available`() {
        mockConnectedTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val result = networkHelper.isConnected()
        assertTrue(result)
    }

    @Test
    fun `isConnected returns true when cellular transport is available`() {
        mockConnectedTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val result = networkHelper.isConnected()
        assertTrue(result)
    }

    @Test
    fun `isConnected returns true when ethernet transport is available`() {
        mockConnectedTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        val result = networkHelper.isConnected()
        assertTrue(result)
    }

    @Test
    fun `isConnected returns true when vpn transport is available`() {
        mockConnectedTransport(NetworkCapabilities.TRANSPORT_VPN)
        val result = networkHelper.isConnected()
        assertTrue(result)
    }

    @Test
    fun `isConnected returns false when no supported transport is available`() {
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(false)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(false)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)).thenReturn(false)
        val result = networkHelper.isConnected()
        assertFalse(result)
    }

    private fun mockConnectedTransport(transport: Int) {
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(capabilities)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            .thenReturn(transport == NetworkCapabilities.TRANSPORT_WIFI)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            .thenReturn(transport == NetworkCapabilities.TRANSPORT_CELLULAR)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
            .thenReturn(transport == NetworkCapabilities.TRANSPORT_ETHERNET)
        `when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
            .thenReturn(transport == NetworkCapabilities.TRANSPORT_VPN)
    }
}