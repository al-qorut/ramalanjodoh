package smk.adzikro.ramalanjodoh.ui.fragments.online

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.alqorut.mystory.views.ConfirmationDialog
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.R
import smk.adzikro.ramalanjodoh.data.models.Ramalx
import smk.adzikro.ramalanjodoh.databinding.FragmentOnlineBinding
import smk.adzikro.ramalanjodoh.ui.activities.CommentActivity
import smk.adzikro.ramalanjodoh.ui.activities.ResultCariActivity
import smk.adzikro.ramalanjodoh.ui.activities.ResultCariActivity.Companion.EXTRA_DATA
import smk.adzikro.ramalanjodoh.ui.activities.SettingsActivity
import smk.adzikro.ramalanjodoh.ui.adapter.MainLoadStateAdapter
import smk.adzikro.ramalanjodoh.ui.adapter.RamalxAdapter
import smk.adzikro.ramalanjodoh.utils.InternetCheck
import smk.adzikro.ramalanjodoh.utils.captureViewAsBitmap
import smk.adzikro.ramalanjodoh.utils.config
import smk.adzikro.ramalanjodoh.utils.shareImage
import smk.adzikro.ramalanjodoh.utils.toRamal
import smk.adzikro.ramalanjodoh.utils.toast
import smk.adzikro.ramalanjodoh.viewmodels.RemoteViewModel
import java.security.MessageDigest
import java.util.UUID

@AndroidEntryPoint
class OnlineFragment : Fragment(), RamalxAdapter.OnItemClickCallback {
    private val TAG="OnlineFragment"
    private var _binding: FragmentOnlineBinding? = null
    private val binding get() = _binding!!
    val viewModel by viewModels<RemoteViewModel>()
    private lateinit var auth: FirebaseAuth
    //private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var adapterRamal: RamalxAdapter
   // private val RC_SIGN_IN = 9001
    private var cariText: String? = ""

  //  private lateinit var credentialManager: CredentialManager
    // Lakukan ini di level class atau onCreate()
    private val credentialManager by lazy { CredentialManager.create(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnlineBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
    //    credentialManager = CredentialManager.create(requireContext())
        auth = Firebase.auth
        loading()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        binding.viewLogin.signInButton.setOnClickListener {
            val accept = binding.viewLogin.accept.isChecked
            if(accept) {
                requestLogin()
            }else{
                ConfirmationDialog(requireActivity(), getString(R.string.no_accept), negative = 0){}
            }
        }

    }
    private fun setUpView() {
        adapterRamal = RamalxAdapter(this)
        val s = resources.openRawResource(R.raw.pprm).bufferedReader().use {
            it.readText() }
        binding.viewLogin.tvLogin.text = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY)
        binding.apply {
            listItemOnline.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = adapterRamal
                    .withLoadStateFooter(
                        MainLoadStateAdapter()
                    )
            }

            menuSearch.getToolbar().inflateMenu(R.menu.menu_search)
            menuSearch.toggleHideOnScroll(false)
            menuSearch.setupMenu()
            menuSearch.getToolbar().menu.apply {
                findItem(R.id.action_search).isVisible = false
                findItem(R.id.action_setting).isVisible = true
                findItem(R.id.action_logout).isVisible = true
            }
            menuSearch.onSearchClosedListener = {
                //getAllFragments().forEach {
                //    it?.searchQueryChanged("")
                // }
            }

            menuSearch.onSearchTextChangedListener = { text ->
                if(text.length>2){
                    cariText = text
                    menuSearch.getToolbar().menu.findItem(R.id.action_search).isVisible = true
                }else{
                    cariText = ""
                    menuSearch.getToolbar().menu.findItem(R.id.action_search).isVisible = false
                }
            }
            menuSearch.getToolbar().setOnMenuItemClickListener { menuItem ->


                when (menuItem.itemId) {
                    R.id.action_setting -> showSetting()
                    R.id.action_logout -> signOut()
                    R.id.action_search -> showSearch()
                    else -> return@setOnMenuItemClickListener false
                }
                return@setOnMenuItemClickListener true
            }

            menuSearch.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(menuSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        viewModel.observeLoadState(adapterRamal)
    }
    private fun showSetting(){
        startActivity(Intent(requireContext(), SettingsActivity::class.java))
    }

    private fun showSearch(){
        if(cariText!="") {
            val intent = Intent(Intent(requireContext(), ResultCariActivity::class.java))
            intent.putExtra(EXTRA_DATA, cariText)
            startActivity(intent)
        }else{
            ConfirmationDialog(requireActivity(), getString(R.string.text_cari_kosong), negative = 0){}
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            viewModel.dataRamalx.observe(viewLifecycleOwner){  pagingData ->
                Log.e(TAG, "ini paging data ${pagingData}")
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
    override fun onResume() {
        super.onResume()
        adapterRamal.refresh()
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        updateUI(user)
        adapterRamal.refresh()
    }



    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            loadData()
            binding.viewLogin.root.visibility = View.GONE
            binding.listItemOnline.visibility = View.VISIBLE
            binding.viewEmpties.root.visibility = View.GONE
            binding.menuSearch.visibility = View.VISIBLE
        }else{
            binding.viewLogin.root.visibility = View.VISIBLE
            binding.listItemOnline.visibility = View.GONE
            binding.menuSearch.visibility = View.GONE
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    context?.config?.userUid = user?.uid
                    context?.config?.displayName = user?.displayName
                    context?.config?.email = user?.email
                    viewModel.adduser(user)
                    updateUI(user)
                } else {
                    Log.w("SignInFragment", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun requestLogin() {
        InternetCheck {
            if (it) {
                signInWithCredentialManager()
            } else {
                toast(requireContext(), R.string.no_internet.toString())
            }
        }
    }
    private fun signInWithCredentialManager() {
        // 1. Buat request Google ID Option
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

//        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false) // Wajib false agar muncul pilihan akun jika belum pernah login
//            .setServerClientId(getString(R.string.default_web_client_id))
//            .setAutoSelectEnabled(false) // Set false dulu untuk testing agar dialog selalu muncul
//            .setNonce(hashedNonce)
//            .build()
        val googleIdOption = GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id))
            .setNonce(hashedNonce)
            .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireActivity()
                )
                handleSignIn(result)
            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                // Ditangkap khusus jika tidak ada akun Google di HP/Emulator
                Log.e(TAG, "No credentials available: ${e.message}", e)
                toast(requireContext(),"Tidak ada akun Google yang ditemukan di perangkat ini. Silakan tambahkan akun terlebih dahulu.")
            } catch (e: GetCredentialException) {
                Log.e(TAG, "GetCredentialException: ${e.message}", e)
                toast(requireContext(),"Gagal melakukan login: ${e.localizedMessage}")
            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "GoogleIdTokenParsingException: ${e.message}", e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        firebaseAuthWithGoogle(googleIdToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected credential type")
                }
            }
            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
    private fun signOut() {
        Firebase.auth.signOut()
        requireContext().config.userUid = ""
        binding.viewLogin.root.visibility = View.VISIBLE
        binding.listItemOnline.visibility = View.GONE
        binding.viewEmpties.root.visibility = View.GONE
        binding.menuSearch.visibility = View.GONE
    }
    private fun loading(){
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
           binding.progressBar.isVisible = isLoading
        })
    }



    override fun onItemCommentClicked(data: Ramalx) {
        val intent  = Intent(requireContext(), CommentActivity::class.java)
        intent.putExtra(CommentActivity.DETAIL, data)
        startActivity(intent)
    }

    override fun onItemFavoriteClicked(data: Ramalx) {
        viewModel.toggleFavorite(data.ramalid)
        adapterRamal.refresh()
    }

    override fun onItemShareClicked(data: View) {
        val bitmap = captureViewAsBitmap(data)
        shareImage(requireContext(), bitmap)
    }

    override fun onItemSaveClicked(data: Ramalx) {
        ConfirmationDialog(requireActivity(), getString(R.string.add_favorite)) {
            viewModel.addRamal(toRamal(data))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listItemOnline.adapter = null
        adapterRamal.submitData(lifecycle, PagingData.empty())
        _binding = null
    }
}