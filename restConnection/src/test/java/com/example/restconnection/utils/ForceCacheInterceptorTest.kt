package com.example.restconnection.utils


import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class ForceCacheInterceptorTest {

    private lateinit var testObject: ForceCacheInterceptor

    private val mockNetworkState = mockk<NetworkState>(relaxed = true)

    private val mockChain = mockk<Interceptor.Chain>(relaxed = true)

    private val mockRequestBuilder = mockk<Request.Builder>(relaxed = true)

    @Before
    fun setUp() {

        testObject = ForceCacheInterceptor(mockNetworkState)

    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `if internet is not enabled call cacheControl`(){

        every { mockNetworkState.isInternetEnabled() } returns false
        every { mockChain.request() } returns mockk{
            every { newBuilder() } returns mockRequestBuilder
        }
        every { mockRequestBuilder.cacheControl(CacheControl.FORCE_CACHE) } returns mockRequestBuilder
        every { mockRequestBuilder.build() } returns mockk()

        testObject.intercept(mockChain)

        verify { mockRequestBuilder.cacheControl(any()) }

    }

    @Test
    fun `if internet is enabled don't call cacheControl`(){

        every { mockNetworkState.isInternetEnabled() } returns true
        every { mockChain.request() } returns mockk{
            every { newBuilder() } returns mockRequestBuilder
        }
        every { mockRequestBuilder.cacheControl(CacheControl.FORCE_CACHE) } returns mockRequestBuilder
        every { mockRequestBuilder.build() } returns mockk()

        testObject.intercept(mockChain)

        verify(exactly = 0) { mockRequestBuilder.cacheControl(any()) }

    }

}