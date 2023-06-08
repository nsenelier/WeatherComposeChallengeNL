package com.example.restconnection.services


import com.example.restconnection.utils.InternetConnectionException
import com.example.restconnection.utils.NetworkState
import com.example.restconnection.utils.NoResponseException
import com.example.restconnection.utils.ResponseFailedException
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
internal class ServiceCallApiImplTest {

    private lateinit var testObject: ServiceCallApi

    private val mockNetworkState = mockk<NetworkState>(relaxed = true)

    private val mockAnyResponse = mockk<Any>(relaxed = true)

    private val mockAction = mockk<suspend () -> Response<Any>>()
    private val mockSuccess = mockk<suspend (Any) -> Unit >()
    private val mockError = mockk<suspend (Exception) -> Unit>()

    //add testScope
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testObject = ServiceCallApiImpl(mockNetworkState)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `if internet is enabled and action returns a response execute success`(){

        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery { mockAction().isSuccessful } returns true
        coEvery {mockAction().body()} returns mockAnyResponse

        val job = testScope.launch {
            testObject.restServiceCall(
                action = mockAction,
                success = mockSuccess,
                error = mockError
            )
        }

        job.cancel()

        coVerify { mockSuccess(mockAnyResponse) }

    }

    @Test
    fun `if internet is enabled and action returns a null response throws no response error`(){

        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery { mockAction().isSuccessful } returns true
        coEvery {mockAction().body()} returns null

        val job = testScope.launch {
            try{
                testObject.restServiceCall(
                    action = mockAction,
                    success = mockSuccess,
                    error = mockError
                )
            }catch (e:Exception){
                assertThat(e).isInstanceOf(NoResponseException::class.java)
            }
        }

        job.cancel()

    }

    @Test
    fun `if internet is enabled and action is not successful throws response failed error`(){

        every { mockNetworkState.isInternetEnabled() } returns true
        coEvery { mockAction().isSuccessful } returns false

        val job = testScope.launch {
            try{
                testObject.restServiceCall(
                    action = mockAction,
                    success = mockSuccess,
                    error = mockError
                )
            }catch (e:Exception){
                assertThat(e).isInstanceOf(ResponseFailedException::class.java)
            }
        }

        job.cancel()


    }

    @Test
    fun `if internet is not enabled throws internet connection error`(){

        every { mockNetworkState.isInternetEnabled() } returns false

        val job = testScope.launch {
            try{
                testObject.restServiceCall(
                    action = mockAction,
                    success = mockSuccess,
                    error = mockError
                )
            }catch (e:Exception){
                assertThat(e).isInstanceOf(InternetConnectionException::class.java)
            }
        }

        job.cancel()

    }


}