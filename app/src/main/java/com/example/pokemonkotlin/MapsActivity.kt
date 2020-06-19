package com.example.pokemonkotlin

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadPokemon()
        checkPermission()
    }
    val request_Code:Int = 123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(this ,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), request_Code)
                return
            }
        }
        getUserLocation()

    } // end checkPermission()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            request_Code->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }
                else {
                    Toast.makeText(this , " Access denied " , Toast.LENGTH_LONG).show()
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    } // end onRequestPermissionsResult

    fun getUserLocation(){
        Toast.makeText(this , " Location Access Now " , Toast.LENGTH_LONG).show()
        val myLocation = MyLocationListener()
        val locationManger = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER ,2 , 2f , myLocation)
        val myThread = MyThread()
        myThread.start()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    } // end onMapReady()

    var myLocation:Location ?=null
    inner class MyLocationListener:LocationListener {
        constructor(){
            myLocation = Location("me")
            myLocation!!.latitude = 30.503515
            myLocation!!.longitude =31.216595
        } // end constructor()
        override fun onLocationChanged(location: Location?) {
            myLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

    } // end MyLocationListener class
    var oldLocation:Location?=null
    inner class MyThread:Thread{
        constructor():super(){
            oldLocation = Location("old Location")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run() {
            while (true){
               try {
                   if(oldLocation!!.distanceTo(myLocation)==0f){
                       continue
                   }
                   oldLocation = myLocation
                   runOnUiThread {
                       mMap.clear()
                       // Add a marker in Sydney and move the camera
                       val sydney = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                       mMap.addMarker(MarkerOptions().position(sydney)
                           .title(" Me ")
                           .snippet("here is my Location")
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 14f))

                       // show pokemon
                       for(index in 0..listOfPokemon.size-1){
                           var newPokemon = listOfPokemon[index]
                           if(newPokemon.isCatch == false){
                               val pokemonLocation = LatLng(newPokemon.location!!.latitude, newPokemon.location!!.longitude)
                               mMap.addMarker(MarkerOptions()
                                   .position(pokemonLocation)
                                   .title(newPokemon.name)
                                   .snippet(newPokemon.descrip)
                                   .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))
                               if(myLocation!!.distanceTo(newPokemon.location)<2){
                                   mypower+= newPokemon.power!!
                                   newPokemon.isCatch = true
                                   listOfPokemon[index] = newPokemon
                                   Toast.makeText(applicationContext ,
                                       " you catch the pokemon and your power = "+mypower , Toast.LENGTH_LONG).show()
                               }

                           }
                       } // end for loop
                   }
                   Thread.sleep(1000)
               }
               catch (ex:Exception){

               }
            } // end while

        } // end run()
    } // end MyThread class
    var mypower:Double = 0.0
    var listOfPokemon = ArrayList<Pokemon>()
    fun loadPokemon(){
        listOfPokemon.add(Pokemon(R.drawable.charmender , "Charmender" , "charmender living in japan " ,55.0 , 30.503515 , 31.217595))
        listOfPokemon.add(Pokemon(R.drawable.balbasaur , "Balbasaur" , "Balbasaur living in USA " ,33.0 , 30.504515 , 31.217595))
        listOfPokemon.add(Pokemon(R.drawable.squirtle , "squirtle" , "squirtle living in Iraq " ,99.3 , 30.505515 , 31.217595))
    }
} // end class
