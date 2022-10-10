package com.example.mynotes.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.models.NoteRequest
import com.example.mynotes.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val notesRepository: NotesRepository) : ViewModel() {

    val notesLiveData get() = notesRepository.notesLiveData
    val statusLiveData get() = notesRepository.statusLiveData

    fun getNotes()
    {
        viewModelScope.launch {
            notesRepository.getNotes()
        }
    }

    fun createNote(noteRequest: NoteRequest)
    {
        viewModelScope.launch{
            notesRepository.createNote(noteRequest)
        }
    }

    fun updateNote(noteId :String,noteRequest: NoteRequest)
    {
        viewModelScope.launch{
            notesRepository.updateNote(noteId,noteRequest)
        }
    }

    fun deleteNote(noteId: String)
    {
        viewModelScope.launch {
            notesRepository.deleteNote(noteId)
        }
    }
}