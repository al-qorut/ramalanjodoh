package smk.adzikro.ramalanjodoh.data.models

import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Keep
data class Comment(
    val commentid : String = "",
    val uid : String = "",
    val ramalid : String = "",
    val displayName : String = "",
    val message : String = "",
    @ServerTimestamp
    val date : Date = Date()
){
    constructor() : this("", "", "", "", "", Date())
}