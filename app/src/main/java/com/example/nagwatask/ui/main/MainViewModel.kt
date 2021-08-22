package com.example.nagwatask.ui.main

import androidx.lifecycle.*
import com.example.nagwatask.model.ItemModel
import com.example.nagwatask.repository.ItemsRepository
import com.example.nagwatask.util.AppResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch


class MainViewModel(private val repository: ItemsRepository) : ViewModel() {

    val userIntent = Channel<ItemsIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<ItemsState>(ItemsState.Idle)
    val state: StateFlow<ItemsState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is ItemsIntent.FetchItems -> fetchJobs()
                    is ItemsIntent.FetchFakeItems->fetchFakeData()
                }
            }
        }
    }


    private fun fetchFakeData(){
        var fakeItems=ArrayList<ItemModel>()
        fakeItems.add(ItemModel("1",
            "VIDEO",
            "https://bestvpn.org/html5demos/assets/dizzy.mp4",
            "Video 1"

        ))
        fakeItems.add(ItemModel("2",
            "VIDEO",
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            "Video 2"

        ))
        fakeItems.add(ItemModel("3",
            "PDF",
            "https://kotlinlang.org/docs/kotlin-reference.pdf",
            "PDF 3"

        ))
        fakeItems.add(ItemModel("4",
            "VIDEO",
            "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4",
            "Video 4"

        ))
        fakeItems.add(ItemModel("5",
            "PDF",
            "https://www.cs.cmu.edu/afs/cs.cmu.edu/user/gchen/www/download/java/LearnJava.pdf",
            "PDF 5"

        ))
        fakeItems.add(ItemModel("6",
            "VIDEO",
            "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
            "Video 6"

        ))
        fakeItems.add(ItemModel("7",
            "VIDEO",
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            "Video 7"

        ))
        fakeItems.add(ItemModel("8",
            "VIDEO",
            "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-25s.mp4",
            "Video 8"

        ))
        fakeItems.add(ItemModel("9",
            "PDF",
            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            "PDF 9"

        ))
        fakeItems.add(ItemModel("10",
            "PDF",
            "https://en.unesco.org/inclusivepolicylab/sites/default/files/dummy-pdf_2.pdf",
            "PDF 10"

        ))
        fakeItems.add(ItemModel("11",
            "VIDEO",
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            "Video 11"

        ))
        fakeItems.add(ItemModel("12",
            "VIDEO",
            "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
            "Video 12"

        ))

        _state.value=ItemsState.Items(fakeItems)

    }
    private fun fetchJobs() {
        viewModelScope.launch {
            _state.value = ItemsState.Loading
            val result = repository.getAllJobs()
            _state.value = when (result) {
                is AppResult.Success -> {
                    ItemsState.Items(result.successData as ArrayList<ItemModel>)
                }
                is AppResult.Error -> {
                    ItemsState.Error(result.exception.message)
                }
            }
        }
    }


}