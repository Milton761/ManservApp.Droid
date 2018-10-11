package org.codesolo.manservapp.model

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Report{

    var offName:String = ""
    var offCode:String = ""
    var admin:String = ""
    var dateStart:String = ""
    var dateEnd:String = ""
    var probServ:String = ""
    var serScope:String = ""

    var places:ArrayList<InfoPlace> = arrayListOf()

    constructor()

    constructor(offName: String,
                office_code:String,
                administrator:String,
                dateStart:String,
                dateEnd:String,
                probServ:String,
                serScope:String)
    {
        this.offName = offName
        this.offCode = offCode
        this.admin = admin
        this.dateStart = dateStart
        this.dateEnd = dateEnd
        this.probServ = probServ
        this.serScope = serScope
        this.places = arrayListOf()
    }


    fun toMap(): HashMap<String, Any> {
        val user = hashMapOf<String,Any>()
        user.put("offName ", this.offName)
        user.put("offCode ", this.offCode)
        user.put("admin ", this.admin)
        user.put("dateStart ", this.dateStart)
        user.put("dateEnd ", this.dateEnd)
        user.put("probServ ", this.probServ)
        user.put("serScope ", this.serScope)
        //user.put("places",this.places)

        return user
    }

}