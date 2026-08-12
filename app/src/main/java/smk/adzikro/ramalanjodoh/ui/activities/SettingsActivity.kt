package smk.adzikro.ramalanjodoh.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.databinding.ActivitySettingsBinding
import smk.adzikro.ramalanjodoh.utils.config
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            setSupportActionBar(purchaseToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.settings)
        }
        init_value()
        save_value()
        v = binding.adViewContainer
    }

    private fun init_value(){
        lifecycleScope.launch {
            viewModel.loadToken()
        }
        binding.email.text = config.email
        binding.displayName.text = config.displayName
        binding.resultPublis.isChecked = config.isResulPublish
        viewModel.token.observe(this) {
            binding.countToken.text = getString(R.string.count_token, it.toInt())
        }
        when (Locale.getDefault().language) {
            "id" -> setImageEnd(R.drawable.ic_flag_id)
            "en" -> setImageEnd(R.drawable.ic_flag_us)
            "ar" -> setImageEnd(R.drawable.ic_flag_sa)
        }

    }

    private fun save_value(){
        config.isResulPublish = binding.resultPublis.isChecked
        binding.publishHolder.setOnClickListener {
            binding.resultPublis.toggle()
            config.isResulPublish = binding.resultPublis.isChecked
        }
        binding.bonusTokenHolder.setOnClickListener {
            showRewadedAds()
        }
        binding.beliTokenHolder.setOnClickListener {
            beliToken()
        }
        binding.tvLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

    }
    private fun setImageEnd(drawable: Int) {
        binding?.tvLanguage?.setCompoundDrawablesWithIntrinsicBounds(
            null, null, ContextCompat.getDrawable(this, drawable), null
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}