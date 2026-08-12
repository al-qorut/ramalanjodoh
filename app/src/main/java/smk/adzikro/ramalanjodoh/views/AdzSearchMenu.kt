package smk.adzikro.ramalanjodoh.views

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.appbar.AppBarLayout
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.databinding.MenuSearchBinding

class AdzSearchMenu(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {
    companion object {
        const val MEDIUM_ALPHA = 0.5f
        const val LOWER_ALPHA = 0.25f
        const val DARK_GREY = 0xFF333333.toInt()
    }

    var isSearchOpen = false
    var useArrowIcon = false
    var onSearchOpenListener: (() -> Unit)? = null
    var onSearchClosedListener: (() -> Unit)? = null
    var onSearchTextChangedListener: ((text: String) -> Unit)? = null
    var onNavigateBackClickListener: (() -> Unit)? = null
    private var binding : MenuSearchBinding

    init {
        binding= MenuSearchBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun getToolbar() = binding.topToolbar

    fun Activity.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Activity.showKeyboard(et: EditText) {
        et.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
    }
    fun Int.addBit(bit: Int) = this or bit
    fun Int.removeBit(bit: Int) = addBit(bit) - bit
    fun Int.getContrastColor(): Int {
        val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
        return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.WHITE
    }

    fun Drawable.applyColorFilter(color: Int) =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)

    fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)
    fun Int.adjustAlpha(factor: Float): Int {
        val alpha = Math.round(Color.alpha(this) * factor)
        val red = Color.red(this)
        val green = Color.green(this)
        val blue = Color.blue(this)
        return Color.argb(alpha, red, green, blue)
    }

    fun Resources.getColoredDrawableWithColor(
        drawableId: Int,
        alpha: Int = 255
    ): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        drawable?.mutate()?.alpha = alpha
        return drawable
    }
    fun Context.getProperPrimaryColor() = resources.getColor(R.color.colorPrimary, theme)
    fun setupMenu() {
        binding.topToolbarSearchIcon.setOnClickListener {
            if (isSearchOpen) {
                closeSearch()
            } else if (useArrowIcon && onNavigateBackClickListener != null) {
                onNavigateBackClickListener!!()
            } else {
                binding.topToolbarSearch.requestFocus()
                (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
            }
        }

        post {
            binding.topToolbarSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    openSearch()
                }
            }
        }

        binding.topToolbarSearch.doAfterTextChanged {  text->
            onSearchTextChangedListener?.invoke(text.toString())
        }
    }

    fun focusView() {
        binding.topToolbarSearch.requestFocus()
    }

    private fun openSearch() {
        isSearchOpen = true
        onSearchOpenListener?.invoke()
        binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_arrow_left_vector)
    }

    fun closeSearch() {
        isSearchOpen = false
        onSearchClosedListener?.invoke()
        binding.topToolbarSearch.setText("")
        if (!useArrowIcon) {
            binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_search_vector)
        }
        (context as? Activity)?.hideKeyboard(binding.root)
    }

    fun getCurrentQuery() = binding.topToolbarSearch.text.toString()

    fun updateHintText(text: String) {
        binding.topToolbarSearch.hint = text
    }

    fun toggleHideOnScroll(hideOnScroll: Boolean) {
        val params = binding.topAppBarLayout.layoutParams as LayoutParams
        if (hideOnScroll) {
            params.scrollFlags = LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        } else {
            params.scrollFlags = params.scrollFlags.removeBit(LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        }
    }

    fun toggleForceArrowBackIcon(useArrowBack: Boolean) {
        this.useArrowIcon = useArrowBack
        val icon = if (useArrowBack) {
            R.drawable.ic_arrow_left_vector
        } else {
            R.drawable.ic_search_vector
        }

        binding.topToolbarSearchIcon.setImageResource(icon)
    }
    fun  updateStatusbarColor(color: Int) {
        (context as Activity).window.statusBarColor = color

        if (color.getContrastColor() == DARK_GREY || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                (context as Activity).window.setDecorFitsSystemWindows(false)
            } else if (color.getContrastColor() == DARK_GREY) {
                @Suppress("DEPRECATION")
                (context as Activity).window.decorView.systemUiVisibility =
                    @Suppress("DEPRECATION")
                    (context as Activity).window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                @Suppress("DEPRECATION")
                (context as Activity).window.decorView.systemUiVisibility =
                    @Suppress("DEPRECATION")
                    (context as Activity).window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }
    }

    fun updateTopBarColors(toolbar: Toolbar, color: Int) {
        val contrastColor =
            color.getContrastColor()

        toolbar.overflowIcon = resources.getColoredDrawableWithColor(R.drawable.ic_three_dots_vector, contrastColor)

        val menu = toolbar.menu
        for (i in 0 until menu.size()) {
            try {
                menu.getItem(i)?.icon?.setTint(contrastColor)
            } catch (ignored: Exception) {
            }
        }
    }

    fun updateColors() {
        val backgroundColor = ContextCompat.getColor(context,R.color.default_background_color)
        val contrastColor = backgroundColor.getContrastColor()

        setBackgroundColor(backgroundColor)
        binding.topAppBarLayout.setBackgroundColor(backgroundColor)
        binding.topToolbarSearchIcon.applyColorFilter(contrastColor)
        binding.topToolbarHolder.background?.applyColorFilter(context.getProperPrimaryColor().adjustAlpha(
            LOWER_ALPHA
        ))
        binding.topToolbarSearch.setTextColor(contrastColor)
        binding.topToolbarSearch.setHintTextColor(contrastColor.adjustAlpha(MEDIUM_ALPHA))
        updateTopBarColors(binding.topToolbar, backgroundColor)
    }
}
