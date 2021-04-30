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

    private val _status = MutableLiveData<TagsStatus>()
    val status : LiveData<TagsStatus> = _status

    private val _tags = MutableLiveData<List<Tag>>(listOf())
    val tags: LiveData<List<Tag>> = _tags

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure

    init {
        viewModelScope.launch {
            _status.value = TagsStatus.LOADING
            getTagsUseCase().fold(::handleFailureTags, ::handleTags)
        }
    }

    private fun handleFailureTags(failure: Failure) {
        _status.value = TagsStatus.ERROR
        _failure.value = failure
    }

    private fun handleTags(tags: List<Tag>){
        _tags.value = tags
        _status.value = TagsStatus.DONE
    }

}

enum class TagsStatus { LOADING, ERROR, DONE }