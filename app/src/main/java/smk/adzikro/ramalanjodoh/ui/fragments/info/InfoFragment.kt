package smk.adzikro.ramalanjodoh.ui.fragments.info

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import smk.adzikro.ramalanjodoh.BuildConfig
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val versionName = BuildConfig.VERSION_NAME
        binding.apply {
            appVersion.text = getString(R.string.app_ver, versionName)
            //webPp.loadUrl("https://adzikro.ct.ws/product/pprj.html")
            val s = resources.openRawResource(R.raw.pprm).bufferedReader().use {
                it.readText() }
            textPprj.text = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}