package smk.adzikro.ramalanjodoh.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataAdapter
import androidx.paging.cachedIn
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import smk.adzikro.ramalanjodoh.ui.adapter.RamalPagingSource
import smk.adzikro.ramalanjodoh.utils.PAGE_SIZE
import smk.adzikro.ramalanjodoh.utils.toUser
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val repo: Repositories
) : ViewModel() {
    private val TAG = "RemoteViewModel"

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _token = MutableLiveData<Long>()
    val token: LiveData<Long> get() = _token

    fun addTokenBonus(){
        viewModelScope.launch(Dispatchers.IO) {
           repo.addTokenBonus()
        }
    }

    fun addBeliToken(count : Long){
        viewModelScope.launch(Dispatchers.IO) {
            repo.addBeliToken(count)
        }
    }
    
    fun loadToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val tokenValue = repo.getToken()
            _token.postValue(tokenValue.toLong())
        }
    }
    val dataRamalx = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE
        )
    ) {
        RamalPagingSource(repo)
    }.flow.cachedIn(viewModelScope).asLiveData()

    fun observeLoadState(adapter: PagingDataAdapter<*, *>) {
        viewModelScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                _isLoading.postValue(loadStates.refresh is LoadState.Loading)
            }
        }
    }


    fun adduser(user: FirebaseUser?) {
        user?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repo.adduser(toUser(it))
                    repo.addUserx(toUser(it))
                } catch (e: Exception) {
                    Log.e("RemoteViewModel", "Error adding user: ${e.localizedMessage}", e)
                }
            }
        } ?: run {
            Log.e("RemoteViewModel", "User is null, cannot add user.")
        }
    }

    val flow = Pager(
        PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { RamalPagingSource(repo) }
    ).flow.cachedIn(viewModelScope)



    fun toggleFavorite(ramalid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                repo.toggleFavorite(ramalid)
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling favorite: ${e.localizedMessage}", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addRamal(ramal: Ramal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addRamal(ramal)
        }
    }

}