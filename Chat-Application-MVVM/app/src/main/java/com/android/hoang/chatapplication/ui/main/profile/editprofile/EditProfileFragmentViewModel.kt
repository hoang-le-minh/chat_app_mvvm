package com.android.hoang.chatapplication.ui.main.profile.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseViewModel
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.di.qualifier.IoDispatcher
import com.android.hoang.chatapplication.domain.usecase.UpdateUseCase
import com.android.hoang.chatapplication.util.Resource
import com.android.hoang.chatapplication.util.State
import com.android.hoang.chatapplication.util.dateOfBirthValidator
import com.blankj.utilcode.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileFragmentViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val updateUseCase: UpdateUseCase

): BaseViewModel() {

    private val _result = MutableLiveData<Resource<UserFirebase>>()
    val result: LiveData<Resource<UserFirebase>>
        get() = _result

    val name = MutableLiveData<String>()
    val dob = MutableLiveData<String>()
    val checkResult = MutableLiveData<String>()
    val isSaveEnable = MutableLiveData<Boolean>(false)

    fun updateCheckResultState() {
        val nameValue = name.value ?: ""
        val dobValue = dob.value ?: ""
        if (nameValue == ""){
            checkResult.postValue(StringUtils.getString(R.string.name_empty))
        }
        if (!dateOfBirthValidator(dobValue)){
            checkResult.postValue(StringUtils.getString(R.string.invalid_dob))
        }
    }


    fun updateProfile(name: String, imageUrl: String, phoneNumber: String, dateOfBirth: String){
        if (isSaveEnable.value == true){
            viewModelScope.launch(ioDispatcher) {
                updateUseCase.invokeUpdateUser(name, imageUrl, phoneNumber, dateOfBirth).collect{
                    when (it) {
                        is State.Loading -> {
                            _result.postValue(Resource.loading())
                        }
                        is State.Success -> {
                            _result.postValue(Resource.success(it.data))
                        }
                        is State.Error -> {
                            _result.postValue(
                                Resource.error(
                                    message = it.message ?: StringUtils.getString(
                                        R.string.something_went_wrong
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }

    }

}