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

    private val _tagsState = MutableLiveData<TagsState>(TagsState.Loading)
    val tagsState : LiveData<TagsState> = _tagsState

    init {
        viewModelScope.launch {
            getTagsUseCase().fold(::handleFailureTags, ::handleTags)
        }
    }


    private fun handleTags(tags: List<Tag>){
        if(tags.isNullOrEmpty()){
            _tagsState.value = TagsState.EmptyTagsList
        }else{
            _tagsState.value = TagsState.Success(tags)
        }
    }

    private fun handleFailureTags(failure: Failure) {
        _tagsState.value = when(failure) {
            is Failure.ProductsFailure.TagsNotFound ->
                TagsState.EmptyTagsList
            else ->
                TagsState.UnknownError(failure, failure.toString())
        }
    }

}

sealed class TagsState {
    object Loading : TagsState()
    class Success(val data: List<Tag>) : TagsState()
    object EmptyTagsList : TagsState()
    class UnknownError(val failure: Failure, val message: String?) : TagsState()
}