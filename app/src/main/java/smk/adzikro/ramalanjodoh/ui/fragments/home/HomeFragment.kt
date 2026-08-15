package smk.adzikro.ramalanjodoh.ui.fragments.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alqorut.mystory.views.ConfirmationDialog
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.databinding.FragmentHomeBinding
import smk.adzikro.ramalanjodoh.ui.activities.MainActivity
import smk.adzikro.ramalanjodoh.utils.JodohHelper
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.confirmDialog
import smk.adzikro.ramalanjodoh.utils.mydebug

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var click = 0
    private var hitung = false
    private var naw: String = ""
    private var nal: String = ""
    private var ha: Ramal? = null
    private var loading: CountDownTimer? = null
    // private var kata: List<String> = emptyList()

    private var forbiddenWords: Set<String> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {

        animasi()
        binding.apply {
            getBonus.setOnClickListener {
                if (requireContext().config.userUid != null) {
                    if (requireContext().config.isInfoShow) {
                        ConfirmationDialog(
                            requireActivity(),
                            message = getString(R.string.info_bonus)
                        ) {
                            requireContext().config.isInfoShow = false
                            (context as MainActivity).showRewadedAds()
                        }
                    } else {
                        (context as MainActivity).showRewadedAds()
                    }
                } else {
                    ConfirmationDialog(
                        requireActivity(),
                        message = getString(R.string.noyet_login),
                        negative = 0
                    ) {}
                }
            }
            proses.setOnClickListener {
                context?.mydebug("tah di klik $nal, dan $naw")
                if (hitung) {
                    reset()
                } else {
                    nal = binding.jalu.text.toString()
                    naw = binding.bikang.text.toString()
                    if (requireContext().config.userUid != null && requireContext().config.isResulPublish && (context as MainActivity).token < 1) {
                        ConfirmationDialog(
                            requireActivity(),
                            message = String.format(
                                getString(R.string.info_publish),
                                (context as MainActivity).token
                            ),
                            negative = 0
                        ) {}
                        return@setOnClickListener
                    }
                    val jodohHelper = JodohHelper()
                    jodohHelper.forbiddenWords = forbiddenWords
                    jodohHelper.genResult(
                        context = requireContext(),
                        kata1 = nal,
                        kata2 = naw,
                        onSuccess = { dataRamal ->
                            // Lolos validasi & berhasil membuat objek Ramal
                            // Simpan ke DB / Tampilkan ke UI
                            println("Hasil Ramalan: ${dataRamal.desc}")
                            ha = dataRamal
                            hasilHitung(dataRamal)
                        },
                        onError = { errorMessage ->
                            // Gagal validasi input (Tampilkan Toast / error di EditText)
                            showInfo(errorMessage)
                        })
                    // hitung()
                }

            }
            jalu.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(jalu, InputMethodManager.SHOW_IMPLICIT)
        }

//        (context as MainActivity).viewModel.kataList.observe(viewLifecycleOwner) {
//            kata = it
//        }

        (context as MainActivity).viewModel.forbiddenWords.observe(viewLifecycleOwner) {
            forbiddenWords = it
        }


    }


    private fun showInfo(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    private fun showMessage(s: String) {
        confirmDialog(
            requireContext(), s,
            onYesClicked = {
               // hitung()
            },
            onNoClicked = {
                return@confirmDialog
            })
    }





    private fun reset() {
        binding.apply {
            jalu.setText("")
            bikang.setText("")
            viewHasil.visibility = View.GONE
            hasilText.text = ""
            imageView2.setImageResource(R.drawable.bg)
            textHitung.text = getString(R.string.action_hitung)
            input.visibility = View.VISIBLE
        }
        hitung = false
        (context as MainActivity).viewModel.loadToken()
        click++
        if (click >= 2 && requireContext().config.userUid == null) {
            (context as MainActivity).showInterstitial()
            click = 0
        } else if ((context as MainActivity).token == 0 && requireContext().config.userUid != null) {
            (context as MainActivity).showInterstitial()
            click = 0
        }
    }

    /*fun dump(){
        for (i in 1..100){
           // var y = Ramal(i, pria = "icih", wanita = "Acah", desc = getString(kataBaik[(0..10).random()]), ilustratsi = imgBad[(0..49).random()])
           // viewModel.addRamal(y)
        }
    } */
    private fun animasi() {
        val textView = binding.getBonus
        val fadeInOut = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f, 0f)
        fadeInOut.duration = 2000 // Durasi untuk fade in dan fade out
        fadeInOut.repeatCount = ObjectAnimator.INFINITE // Ulangi tanpa henti
        fadeInOut.repeatMode = ObjectAnimator.RESTART
        // Animasi scale
        val scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.5f, 1f)
        scaleX.duration = 2000
        scaleY.duration = 2000
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.RESTART
        // Menggabungkan kedua animasi
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeInOut, scaleX, scaleY)
        animatorSet.start()
    }

    private fun hasilHitung(ramal: Ramal?) {
        if(ramal==null) return
        context?.mydebug("$nal, dan $naw")
        hitung = true
        binding.proses.visibility = View.INVISIBLE
        binding.input.visibility = View.GONE
        val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
        binding.imageView2.startAnimation(pulseAnimation)
        if (requireContext().config.isResulPublish && (context as MainActivity).token > 0) {
            (context as MainActivity).publishRamal(ramal)
            (context as MainActivity).viewModel.addRamal(ramal)
        } else {
            ramal.status = 2
            (context as MainActivity).viewModel.addRamal(ramal)
        }
        insertDataObserver()
        hitungJodoh()
        loading?.start()
    }

    private fun insertDataObserver() {
        (context as MainActivity).viewModel.insertStatus.observe(viewLifecycleOwner) { isInsert ->
            if (isInsert) {
                showInfo(getString(R.string.berhasil))
            } else {
                showInfo(getString(R.string.gagal))
            }

        }
    }

    private fun hitungJodoh() {
        if (loading != null) {
            loading!!.cancel()
        }
        loading = object : CountDownTimer(10000L, 50) {
            override fun onTick(millisUntilFinished: Long) {
                //  Log.e(TAG,"seconds remaining: " + (millisUntilFinished / 1000) + 1)
            }

            override fun onFinish() {
                tampil()
                binding.proses.visibility = View.VISIBLE
                binding.textHitung.text = getString(R.string.action_lagi)
            }
        }
    }

    fun tampil() {
        binding.apply {
            viewHasil.visibility = View.VISIBLE
            hasilText.text = ha?.desc
            imageView2.setImageResource(ha!!.ilustratsi)
            input.visibility = View.GONE
        }
        binding.imageView2.clearAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}