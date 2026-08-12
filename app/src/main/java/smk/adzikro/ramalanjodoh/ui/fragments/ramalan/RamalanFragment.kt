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
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.databinding.FragmentRamalanBinding
import smk.adzikro.ramalanjodoh.ui.activities.MainActivity
import smk.adzikro.ramalanjodoh.ui.adapter.MainLoadStateAdapter
import smk.adzikro.ramalanjodoh.ui.adapter.RamalAdapter
import smk.adzikro.ramalanjodoh.utils.captureViewAsBitmap
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.confirmDialog
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
        when(data.status){
            0 -> data.status = 1
            1 -> data.status = 0
            2 -> data.status = 3
            3 -> data.status = 2
        }
        (context as MainActivity).viewModel.updateRamal(data)
        adapterRamal.refresh()
    }

    override fun onItemShareClicked(data: View) {
        val bitmap = captureViewAsBitmap(data)
        shareImage(requireContext(), bitmap)
    }
    private fun publishRamal(ramal: Ramal){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val x = (context as MainActivity).viewModel.publishRamal(ramal)
                (context as MainActivity).viewModel.useToken()
                when(ramal.status){
                    2 -> ramal.status = 0
                    3 -> ramal.status = 1
                }
                (context as MainActivity).publishRamal(ramal)
                (context as MainActivity).viewModel.updateRamal(ramal)
                adapterRamal.refresh()
                ConfirmationDialog(requireActivity(), message = x, negative = 0) {}
                //Snackbar.make(binding.root, x, Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                val x = e.message.toString()
                ConfirmationDialog(requireActivity(), message = x, negative = 0) {}
                // Snackbar.make(binding.root, x, Snackbar.LENGTH_LONG).show()
            }
        }
    }
    override fun onItemPublishClicked(data: Ramal) {
        Log.e("TAG","Silahkan publish")
        if(requireContext().config.userUid==null){
            ConfirmationDialog((context as MainActivity), getString(R.string.noyet_login), negative = 0){}
        }else if((context as MainActivity).token < 1){
            ConfirmationDialog((context as MainActivity), message = String.format(getString(R.string.info_publish), (context as MainActivity).token), negative = 0){}
        }else{
            publishRamal(data)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.listItem.adapter = null
        adapterRamal.submitData(lifecycle, PagingData.empty())
        _binding = null
    }
}