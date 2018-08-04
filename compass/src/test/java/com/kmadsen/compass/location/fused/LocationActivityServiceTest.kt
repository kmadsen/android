package com.kmadsen.compass.location.fused

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
//    lateinit var locationActivityService: LocationsController
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
//        locationActivityService = LocationsController(
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
//        val locationActivityService = LocationsController(
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