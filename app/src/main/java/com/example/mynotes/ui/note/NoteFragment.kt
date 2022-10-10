package com.example.mynotes.ui.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mynotes.R
import com.example.mynotes.databinding.FragmentMainBinding
import com.example.mynotes.databinding.FragmentNoteBinding
import com.example.mynotes.models.NoteRequest
import com.example.mynotes.models.NoteResponse
import com.example.mynotes.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding : FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private var note : NoteResponse? = null

    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNoteBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        bindHandlers()
        bindObservers()
    }

    private fun bindObservers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner,{

            binding.progressBar.isVisible = false

            when(it)
            {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener(View.OnClickListener {
            note?.let { noteViewModel.deleteNote(it._id) }
        })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            val title = binding.txtTitle.text.toString()
            val dec = binding.txtDescription.text.toString()
            val noteRequest = NoteRequest(dec,title)
            if(note == null)
            {
                noteViewModel.createNote(noteRequest)
            }
            else
            {
                noteViewModel.updateNote(note!!._id,noteRequest)
            }
        })
    }

    private fun setInitialData() {
        val jsonNote = arguments?.getString("note")
        if(jsonNote != null)
        {
            note = Gson().fromJson(jsonNote,NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.description)
            }
        }
        else
        {
            binding.addEditText.text = "Add Note"
            binding.btnDelete.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}