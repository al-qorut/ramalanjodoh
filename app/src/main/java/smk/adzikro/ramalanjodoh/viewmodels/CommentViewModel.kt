package smk.adzikro.ramalanjodoh.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import smk.adzikro.ramalanjodoh.data.repo.Repositories
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repo: Repositories
):ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _ramalid = MutableLiveData<String>()
    val ramalid: LiveData<String> get() = _ramalid

    fun setRamalid(ramalid: String) {
        _ramalid.value = ramalid
    }

    fun addComment(ramalid: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                _isSuccess.postValue(false)
                repo.addComment(ramalid = ramalid, message = message)
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error adding user: ${e.localizedMessage}", e)
                _isSuccess.postValue(false)
            }finally {
                _isLoading.postValue(false)
                _isSuccess.postValue(true)
            }
        }
    }



    fun getQueryMessage(ramalid: String) : Query
    {
        return repo.getQueryMessage(ramalid)
    }



}