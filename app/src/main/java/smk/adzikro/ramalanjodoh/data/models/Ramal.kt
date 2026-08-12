package smk.adzikro.ramalanjodoh.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "ramal")
data class Ramal(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var pria : String,
    var wanita : String,
    var desc : String,
    var date : String= getCurrentDateTime(),
    var ilustratsi : Int,
    /*
    *  0 = lokal & not favorite
    *  1 = lokal & favorite
    *  2 = global & not favorite
    *  3 = global & favorite
    * */
    var status : Int = 0
){
    companion object {
        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}