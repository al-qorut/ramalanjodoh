package smk.adzikro.ramalanjodoh.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alqorut.mystory.views.ConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.databinding.ActivityMainBinding
import smk.adzikro.ramalanjodoh.utils.AppUpdateManagerUtil
import smk.adzikro.ramalanjodoh.utils.InternetCheck
import smk.adzikro.ramalanjodoh.utils.applySystemBarsPadding

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var appUpdateManagerUtil : AppUpdateManagerUtil
    var token = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainCoordinator.applySystemBarsPadding(applyTop = true, applyBottom = true)
        v = binding.adViewContainer
        viewModel.loadKata()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        val nav = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(nav)
        isLoaded.observe(this){
            setmargin(it)
            lifecycleScope.launch {
                viewModel.loadToken()
            }
        }
        appUpdateManagerUtil = AppUpdateManagerUtil(this)
        appUpdateManagerUtil.cekUpdate()

        viewModel.token.observe(this) {
            Log.e(TAG, "token $it")
            token = it.toInt()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        appUpdateManagerUtil.handleActivityResult(requestCode, resultCode)
    }

    private fun cek_show(){
        CoroutineScope(Dispatchers.IO).launch {
            InternetCheck {
                if (!it) {
                    runOnUiThread {
                        val params =
                            binding.mainNavHost.layoutParams as CoordinatorLayout.LayoutParams
                        params.topMargin = 0
                        binding.mainNavHost.layoutParams = params
                    }
                }else{
                    //runOnUiThread { setmargin() }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
       // cek_show()
        lifecycleScope.launch {
            viewModel.loadToken()
        }
    }

    fun publishRamal(ramal: Ramal){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                viewModel.publishRamal(ramal)
                viewModel.useToken()
                runOnUiThread {
                    ConfirmationDialog(this@MainActivity, message = getString(R.string.sukses_publish), negative = 0) {}
                }
                //Snackbar.make(binding.root, x, Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                val x = e.message.toString()
                runOnUiThread {
                    ConfirmationDialog(this@MainActivity, message = x, negative = 0) {}
                }
                // Snackbar.make(binding.root, x, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    private fun setmargin(isLoad : Boolean){
        Log.e(TAG, "banner isLoaded ${isLoad}")
        val params = binding.mainNavHost.layoutParams as CoordinatorLayout.LayoutParams
        params.topMargin = if(!isLoad) 0 else resources.getDimensionPixelSize(R.dimen.triple_margin)
        binding.mainNavHost.layoutParams = params
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}