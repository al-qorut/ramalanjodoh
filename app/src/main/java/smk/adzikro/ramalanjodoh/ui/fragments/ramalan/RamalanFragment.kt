package smk.adzikro.ramalanjodoh.ui.fragments.ramalan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.alqorut.mystory.views.ConfirmationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.databinding.FragmentRamalanBinding
import smk.adzikro.ramalanjodoh.ui.activities.MainActivity
import smk.adzikro.ramalanjodoh.ui.adapter.MainLoadStateAdapter
import smk.adzikro.ramalanjodoh.ui.adapter.RamalAdapter
import smk.adzikro.ramalanjodoh.utils.OFFLINE_FAVORITE
import smk.adzikro.ramalanjodoh.utils.OFFLINE_NONFAVORITE
import smk.adzikro.ramalanjodoh.utils.ONLINE_FAVORITE
import smk.adzikro.ramalanjodoh.utils.ONLINE_NONFAVORITE
import smk.adzikro.ramalanjodoh.utils.captureViewAsBitmap
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.confirmDialog
import smk.adzikro.ramalanjodoh.utils.mydebug
import smk.adzikro.ramalanjodoh.utils.shareImage

class RamalanFragment : Fragment(), RamalAdapter.OnItemClickCallback {
    private var _binding: FragmentRamalanBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterRamal: RamalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRamalanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        loadData()
    }

    private fun setUpView() {
        adapterRamal = RamalAdapter(this)
        binding.listItem.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adapterRamal.withLoadStateFooter(
                MainLoadStateAdapter()
            )
        }


    }

    override fun onResume() {
        super.onResume()
        adapterRamal.refresh()
    }

    private fun loadData() {
        lifecycleScope.launch {
            (context as MainActivity).viewModel.dataFlow.observe(viewLifecycleOwner) { pagingData ->
                adapterRamal.submitData(lifecycle, pagingData)
                lifecycleScope.launch {
                    adapterRamal.loadStateFlow.collectLatest { loadStates ->
                        val isListEmpty =
                            loadStates.refresh is LoadState.NotLoading && adapterRamal.itemCount == 0
                            binding.viewEmpties.root.visibility = if (!isListEmpty) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }


    override fun onItemDeleteClicked(data: Ramal) {
        isDelete(data)
    }

    private fun isDelete(s: Ramal) {
        confirmDialog(requireContext(), getString(R.string.hapus, s.pria, s.wanita),
            onYesClicked = {
                (context as MainActivity).viewModel.delRamal(s)
                adapterRamal.refresh()
            },
            onNoClicked = {
                return@confirmDialog
            })
    }

    override fun onItemFavoriteClicked(data: Ramal) {
        activity?.mydebug("inih sebelum ${data.status}")
        val statusBaru  = when(data.status){
            OFFLINE_NONFAVORITE ->  OFFLINE_FAVORITE
            OFFLINE_FAVORITE ->  OFFLINE_NONFAVORITE

            ONLINE_NONFAVORITE ->  ONLINE_FAVORITE
            ONLINE_FAVORITE ->  ONLINE_NONFAVORITE
            else -> data.status
        }
        val dataDiperbarui = data.copy(status = statusBaru )
        (context as MainActivity).viewModel.updateRamal(dataDiperbarui)
         adapterRamal.refresh()
        activity?.mydebug("inih sesudah ${data.status}")
    }

    override fun onItemShareClicked(data: View) {
        val bitmap = captureViewAsBitmap(data)
        shareImage(requireContext(), bitmap)
    }
    private fun publishRamal(ramal: Ramal) {
        // 1. Kunci tombol atau view segera setelah diklik agar tidak bisa ditekan lagi
        // Ganti 'binding.btnPublish' dengan ID tombol asli Anda yang memicu fungsi ini
        val mainActivity = context as? MainActivity
        mainActivity?.onProgress("Tunggu..", true)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                (context as MainActivity).viewModel.publishRamal(ramal)
                (context as MainActivity).viewModel.useToken()

                when (ramal.status) {
                    OFFLINE_NONFAVORITE -> ramal.status = ONLINE_NONFAVORITE
                    OFFLINE_FAVORITE -> ramal.status = ONLINE_FAVORITE
                }

              //  (context as MainActivity).publishRamal(ramal)
                (context as MainActivity).viewModel.updateRamal(ramal)
                val x = "Analisis ${ramal.pria } dengan ${ramal.wanita} sudah di publikasi"
                // 2. Pindahkan operasi UI ke Main Thread menggunakan withContext
                mainActivity?.onProgress("Tunggu..", false)
                withContext(Dispatchers.Main) {
                    adapterRamal.refresh()
                    // Buka kembali kunci tombol setelah proses sukses selesai
                    ConfirmationDialog(requireActivity(), message = x, negative = 0) {}
                }

            } catch (e: Exception) {
                val x = e.message.toString()
                mainActivity?.onProgress("Tunggu..", false)
                // 3. Tangani error UI di Main Thread juga
                withContext(Dispatchers.Main) {
                    // Buka kembali kunci tombol agar pengguna bisa mencoba lagi jika terjadi error
                    ConfirmationDialog(requireActivity(), message = x, negative = 0) {}
                }
            }
        }
    }

    override fun onItemPublishClicked(data: Ramal) {
        val mainActivity = context as? MainActivity
        if (mainActivity == null) {
            Log.e("TAG", "Activity context tidak valid")
            return
        }

        val userUid = requireContext().config.userUid
        Log.e("TAG", "Silahkan publish $userUid")

        // 1. Cek Login (Aman dari null maupun string kosong "")
        if (userUid.isNullOrEmpty()) {
            ConfirmationDialog(mainActivity, getString(R.string.noyet_login), negative = 0) {}
            return
        }

        // 2. Cek Kecukupan Token (Sesuaikan ongkos publish Anda, misal butuh 1 token)
        val tokenSekarang = mainActivity.token
        if (tokenSekarang < 1) {
            // Menggunakan String.format untuk menyusun pesan informasi token habis
            val pesanInfo = String.format(getString(R.string.info_publish), tokenSekarang)
            ConfirmationDialog(mainActivity, message = pesanInfo, negative = 0) {}
            return
        }

        // 3. Semua syarat terpenuhi, jalankan fungsi publish
        ConfirmationDialog(mainActivity, message = "Mau di publikasi?, semua orang akan tahu") {
            if(it) {
                publishRamal(data)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.listItem.adapter = null
        adapterRamal.submitData(lifecycle, PagingData.empty())
        _binding = null
    }
}