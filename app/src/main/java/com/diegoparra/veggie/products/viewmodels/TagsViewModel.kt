package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.usecases.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure

    private val _tags = MutableLiveData<List<Tag>>(listOf())
    val tags: LiveData<List<Tag>> = _tags


    init {
        viewModelScope.launch {
            _loading.value = true
            getTagsUseCase().fold(::handleFailureTags, ::handleTags)
            _loading.value = false
        }
    }

    private fun handleFailureTags(failure: Failure) {
        _failure.value = failure
    }

    private fun handleTags(tags: List<Tag>){
        _tags.value = tags
    }

}