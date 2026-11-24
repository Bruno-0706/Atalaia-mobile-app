package com.example.atalaia.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.atalaia.data.fb.FBDatabase
import com.example.atalaia.domain.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TagUiState (
    val tags: List<Tag> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class TagViewModel(
    private val database: FBDatabase = FBDatabase()
) : ViewModel(), FBDatabase.TagListener {

    private val _uiState = MutableStateFlow(TagUiState())
    val uiState: StateFlow<TagUiState> = _uiState.asStateFlow()

    init {
        database.setTagListener(this)
    }

    override fun onTagsChanged(tags: List<Tag>) {
        _uiState.value = TagUiState(
            tags = tags,
            carregando = false
        )
    }

    override fun onError(exception: Exception) {
        _uiState.value = _uiState.value.copy(
            carregando = false,
            erro = exception.message
                ?: "Não foi possível carregar as tags."
        )
    }

    override fun onCleared() {
        database.removerTagListener()
        super.onCleared()
    }
}