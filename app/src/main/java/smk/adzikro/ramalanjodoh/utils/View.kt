package smk.adzikro.ramalanjodoh.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.applySystemBarsPadding(applyTop: Boolean = false, applyBottom: Boolean = false) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(
            view.paddingLeft,
            if (applyTop) insets.top else view.paddingTop,
            view.paddingRight,
            if (applyBottom) insets.bottom else view.paddingBottom
        )
        windowInsets
    }
}
