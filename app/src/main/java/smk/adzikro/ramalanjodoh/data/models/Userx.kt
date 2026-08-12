package smk.adzikro.ramalanjodoh.data.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "user")
data class Userx(
    @PrimaryKey()
    var uid: String = "",
    var displayName: String = "",
    var email : String = "",
    var token : Int = 0
){
    fun isValid(): Boolean {
        return  email.contains("@") && email.isNotEmpty()
    }
}
