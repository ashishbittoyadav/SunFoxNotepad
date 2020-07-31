package com.example.sunfoxnotepad.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sunfoxnotepad.R
import com.example.sunfoxnotepad.adapter.NoteRecyclerViewAdapter
import com.example.sunfoxnotepad.adapter.OnItemLongClickListener
import com.example.sunfoxnotepad.databinding.FragmentFirstBinding
import com.example.sunfoxnotepad.db.NoteRoomDB
import com.example.sunfoxnotepad.model.Note
import com.example.sunfoxnotepad.realtimedb.FireBaseRealTimeDataBase
import com.example.sunfoxnotepad.repository.NoteRepository
import com.example.sunfoxnotepad.utility.Utility
import com.example.sunfoxnotepad.viewmodel.NoteViewModel
import com.example.sunfoxnotepad.viewmodel.NoteViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnItemLongClickListener {

    private lateinit var binding:FragmentFirstBinding
    private lateinit var viewModel:NoteViewModel
    private lateinit var adapter : NoteRecyclerViewAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_first,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dao = NoteRoomDB.getInstance(context?.applicationContext!!).noteDao
        val repository = NoteRepository(dao)
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(NoteViewModel::class.java)
        binding.noteViewModel = viewModel
        binding.lifecycleOwner=this
        binding.addNoteFab.setOnClickListener{
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    private fun initRecyclerView(){
        binding.noteRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteRecyclerViewAdapter (this) { position:Int,noteItem:Note->listItemClicked(position,noteItem)}
        binding.noteRecyclerView.adapter = adapter
        displayData()
    }

    private fun displayData(){
        viewModel.notes.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()) {
                adapter.setList(it)
                adapter.notifyDataSetChanged()
                binding.noNoteAvailableTextView.visibility = View.GONE
            }else {
                adapter.setList(it)
                adapter.notifyDataSetChanged()
                binding.noNoteAvailableTextView.visibility = View.VISIBLE
                viewModel.getDataFromRemoteDB()
            }
        })
    }

    private fun listItemClicked(position:Int,note: Note){
        val bundle = bundleOf(Utility.NOTE_ID to note.count,
            Utility.POSITION to position)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,bundle)
    }


    override fun onItemLongClicked(note: Note):Boolean{
        showDialog(requireContext().resources.getString(R.string.dialog_message)
                ,requireContext().resources.getString(R.string.dialog_positive_button)
                    , requireContext().resources.getString(R.string.dialog_negative_button)
                    ,requireContext().resources.getString(R.string.dialog_neutral_button)
                    ,note)
        return true
    }
    private fun showDialog(message:String,positiveButtonText:String,negativeButtonText:String,neutralButtonText:String?,note: Note){
        val builder = AlertDialog.Builder(this!!.requireContext())
        builder.setTitle(requireContext().resources.getString(R.string.dialog_title))
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonText){dialog, _ ->
            if(positiveButtonText.equals(requireContext().resources.getString(R.string.dialog_positive_button))){
                val bundle = bundleOf(Utility.NOTE_ID to note.count)
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment,bundle)
            }else if(positiveButtonText.equals(Utility.DELETE)) {
                viewModel.delete(note)
            }
        }
        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            if(negativeButtonText.equals(Utility.DELETE)){
                showDialog(
                    "Do you want to delete?"
                    , Utility.DELETE
                    , Utility.CANCEL
                    , null
                    , note
                )
            }
        }
        builder.setNeutralButton(neutralButtonText) { dialog, _ ->
        }
        builder.show()
    }
}