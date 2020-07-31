package com.example.sunfoxnotepad.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.sunfoxnotepad.R
import com.example.sunfoxnotepad.databinding.FragmentSecondBinding
import com.example.sunfoxnotepad.db.NoteRoomDB
import com.example.sunfoxnotepad.model.Note
import com.example.sunfoxnotepad.realtimedb.FireBaseRealTimeDataBase
import com.example.sunfoxnotepad.repository.NoteRepository
import com.example.sunfoxnotepad.utility.Utility
import com.example.sunfoxnotepad.viewmodel.NoteViewModel
import com.example.sunfoxnotepad.viewmodel.NoteViewModelFactory


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var binding:FragmentSecondBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var fireBaseRealTimeDataBase: FireBaseRealTimeDataBase

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_second,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dao = NoteRoomDB.getInstance(context?.applicationContext!!).noteDao
        val repository = NoteRepository(dao)
        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(NoteViewModel::class.java)
        if(arguments!=null) {
            var count = requireArguments().getInt(Utility.NOTE_ID)
            viewModel.updateNote(count)
        }
        binding.noteViewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {status->
                if(status.toString().equals(Utility.SUCCESS,false)||status.toString().equals(Utility.CANCEL,false)) {
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                }
                else if(status.toString().equals(Utility.EMPTY)){
                    showDialog("can not save empty note?"
                    ,"ok","cancel",null)
                }
            }
        })
    }

    private fun showDialog(message:String,positiveButtonText:String,negativeButtonText:String,neutralButtonText:String?){
        val builder = AlertDialog.Builder(this!!.requireContext())
        builder.setTitle(requireContext().resources.getString(R.string.dialog_title))
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonText){dialog, _ ->
            if(positiveButtonText.equals("yes")){
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }
        builder.setNegativeButton(negativeButtonText) { dialog, _ ->

        }
        builder.setNeutralButton(neutralButtonText) { dialog, _ ->
        }
        builder.show()
    }


    override fun onPause() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        super.onPause()
    }

}