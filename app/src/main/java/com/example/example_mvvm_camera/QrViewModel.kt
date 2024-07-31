package com.example.example_mvvm_camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrViewModel : ViewModel(){
    private val _result = MutableLiveData<String>()
    val result : LiveData<String> get() = _result

    fun setResult(result : String){
        _result.value = result
    }



}