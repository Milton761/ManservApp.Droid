package org.codesolo.manservapp.model

import android.net.Uri


class InfoAgency{
    var code:String = ""
    var name:String = ""

    constructor(code:String,name:String,description: String){
        this.code = code
        this.name = name
    }

    fun toMap():HashMap<String,Any>    {
        val agency = hashMapOf<String,Any>()

        agency["code"] = this.code
        agency["name"] = this.name
        return agency
    }
}


class InfoPlace
{
    var code:String = ""
    var name:String = ""
    var description:String = ""

    constructor(code:String,name:String,description: String){
        this.code = code
        this.name = name
        this.description = description
    }

    constructor()

    fun toMap():HashMap<String,Any>    {
        val place = hashMapOf<String,Any>()

        place["code"] = this.code
        place["name"] = this.name
        place["description"] = this.description

        return place
    }


}

class InfoPlaceCard
{

    var code:String = ""
    var imgUrl:String = ""
    var title:String = ""
    var description:String = ""

    constructor(code:String,imgUrl:String,title:String,description:String)
    {
        this.code = code
        this.imgUrl = imgUrl
        this.title = title
        this.description = description
    }
    constructor()

    fun toMap():HashMap<String, Any>{
        val placePic = hashMapOf<String,Any>()

        placePic["code"] = this.code
        placePic["imgUrl"] = this.imgUrl
        placePic["title"] = this.title
        placePic["description"] = this.description

        return  placePic
    }
}
