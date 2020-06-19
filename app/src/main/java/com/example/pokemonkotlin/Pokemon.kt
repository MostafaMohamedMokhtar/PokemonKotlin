package com.example.pokemonkotlin

import android.location.Location

class Pokemon {
    var name:String ?= null
    var descrip:String ?= null
    var image:Int ?= null
    var location: Location?=null
    var power:Double ?= null
    var isCatch:Boolean ?= false
    constructor(image:Int ,name:String , descrip:String ,power:Double , lat:Double , log:Double  ){
        this.image = image
        this.name = name
        this.descrip = descrip
        this.power = power
        location = Location(name)
        location!!.latitude = lat
        location!!.longitude = log
    }
}