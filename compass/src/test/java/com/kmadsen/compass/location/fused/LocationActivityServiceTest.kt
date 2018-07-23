package com.kmadsen.compass.location.fused

import android.app.Activity
import com.kmadsen.compass.location.LocationActivityService
import com.kmadsen.compass.location.LocationPermissions
import com.nhaarman.mockitokotlin2.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LocationActivityServiceTest {

//    private fun <T> any(): T {
//        Mockito.any<T>()
//        return uninitialized()
//    }
//    private fun <T> uninitialized(): T = null as T
//
//    @Mock
//    lateinit var locationPermissions: LocationPermissions
//
//    @Mock
//    lateinit var fusedLocationService: FusedLocationService
//
//    lateinit var locationActivityService: LocationActivityService
//
//    @Before
//    fun setup() {
//        val activity: Activity = mock()
//        val grantedPermissions: (Boolean) -> Unit = mock {
//            onGeneric { invoke(true) }
//        }
//        val locationPermissions: LocationPermissions = mock {
//            on { onActivityStart(activity, grantedPermissions ) }
//        }
//        val fusedLocationService: FusedLocationService = mock {
//            on { }
//        }
//        whenever(locationPermissions.onActivityStart(activity, grantedPermissions)
//
//        MockitoAnnotations.initMocks(this)
//
//        locationActivityService = LocationActivityService(
//                locationPermissions,
//                fusedLocationService
//        )
//    }
//
//    @Test
//    fun shouldWork() {
//        val activity: Activity = mock()
//        val grantedPermissions: (Boolean) -> Unit = mock {
//            onGeneric { invoke(true) }
//        }
//        val locationPermissions: LocationPermissions = mock {
//            on { onActivityStart(activity, grantedPermissions ) }
//        }
//        val fusedLocationService: FusedLocationService = mock {
//            on { }
//        }
//        val locationActivityService = LocationActivityService(
//                locationPermissions,
//                fusedLocationService
//        )
//
//        locationActivityService.onStart(activity)
//
//        verify(fusedLocationService).start { only() }
//    }

//    @Test
//    fun shouldStartWhenPermissionsAreGranted() {
//        assertEquals(1, 0)
//        val activity: Activity = Mockito.mock(Activity::class.java)
//        val grantedPermissions: (Boolean) -> Unit = mock {
//            onGeneric { invoke(true) }
//        }
//        whenever(locationPermissions.onActivityStart(activity, grantedPermissions))
//
//        locationActivityService.onStart(activity)
//    }
}