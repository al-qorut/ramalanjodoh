package smk.adzikro.ramalanjodoh.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import smk.adzikro.ramalanjodoh.utils.IS_GOOD
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
    var status : Int = 0,
    @ColumnInfo(name = "result", defaultValue = "0")
    var result : Int = IS_GOOD
){
    companion object {
        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}