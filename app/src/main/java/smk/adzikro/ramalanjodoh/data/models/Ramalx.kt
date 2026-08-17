package smk.adzikro.ramalanjodoh.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import kotlinx.parcelize.Parcelize
import smk.adzikro.ramalanjodoh.utils.IS_GOOD

@Keep
@Parcelize
data class Ramalx(
    val ramalid : String = "",
    var uid : String = "",
    var displayName : String? = "",
    val pria : String = "",
    val wanita : String ="",
    val desc : String = "",
    @ServerTimestamp
    var date : Date? = null,
    var favorite : List<String> = listOf(),
    val img : Int = 0,
    var message : Int = 0,
    var result : Int = IS_GOOD
) : Parcelable

