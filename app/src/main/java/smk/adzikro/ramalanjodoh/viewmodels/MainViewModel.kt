package smk.adzikro.ramalanjodoh.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.repo.RepoRamal
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import smk.adzikro.ramalanjodoh.ui.adapter.MainPagingSource
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject
constructor(
    private val repo: RepoRamal,
    private val remote: Repositories
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _token = MutableLiveData<Long>()
    val token: LiveData<Long> get() = _token

    fun getQueryCari(name: String) : Query = remote.getQueryCariRamal(name)

    fun useToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                remote.useToken()
                withContext(Dispatchers.Main) {
                    loadToken()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBeliToken(count: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                remote.addBeliToken(count)
                withContext(Dispatchers.Main) {
                    loadToken()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun toggleFavorite(ramalid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                remote.toggleFavorite(ramalid)
            } catch (e: Exception) {
                Log.e("TAG", "Error toggling favorite: ${e.localizedMessage}", e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun loadToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokenValue = remote.getToken()
                _token.postValue(tokenValue.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
                _token.postValue(0L)
            }

        }
    }

    fun getPagingData(): Flow<PagingData<Ramal>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { MainPagingSource(repo) }
        ).flow.cachedIn(viewModelScope)
    }

    val dataFlow: LiveData<PagingData<Ramal>> = getPagingData()
        .asLiveData()

    val data = Pager(
        config = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false,
            initialLoadSize = 5
        ),
        pagingSourceFactory = { MainPagingSource(repo) }
    ).flow.cachedIn(viewModelScope)

    fun delRamal(ramal: Ramal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteRamal(ramal)
        }
    }

    fun updateRamal(ramal: Ramal) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateRamal(ramal)
        }
    }

    private val _isInsertStatus = MutableLiveData<Boolean>()
    val insertStatus: LiveData<Boolean> = _isInsertStatus

    fun addRamal(ramal: Ramal) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repo.addRamal(ramal)
            _isInsertStatus.postValue(id > 0)
        }
    }

    val kataList: MutableLiveData<List<String>> = MutableLiveData()

    fun loadKata() {
        if (kataList.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                val loadedKataList = repo.getListKata()
                kataList.postValue(loadedKataList)
            }
        }
    }

    fun addRamalx(ramal: Ramal) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                remote.addRamalx(ramal)
            } catch (e: Exception) {
                Log.e("MainViewModel", "addRamalx: ${e.message}")
            }

        }
    }

    suspend fun publishRamal(ramal: Ramal): String {
        return try {
            remote.addRamalx(ramal)
        } catch (e: Exception) {
            return ("publish ramal gagal  ${e.message}")
        }

    }
}