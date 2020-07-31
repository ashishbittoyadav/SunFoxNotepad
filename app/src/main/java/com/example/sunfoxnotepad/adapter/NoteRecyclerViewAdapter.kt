package com.example.sunfoxnotepad.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunfoxnotepad.R
import com.example.sunfoxnotepad.databinding.NoteListViewBinding
import com.example.sunfoxnotepad.model.Note

class NoteRecyclerViewAdapter(private val onItemLongClickListener:OnItemLongClickListener,private val clickListener: (Int,Note) -> Unit) : RecyclerView.Adapter<NoteRecyclerViewAdapter.NoteViewHolder>(){

    private val noteList = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val noteListViewBinding:NoteListViewBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.note_list_view,parent,false)
        return NoteViewHolder(noteListViewBinding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(position,noteList[position],clickListener = clickListener)
        holder.binding.noteItemContainerCardView.setOnLongClickListener {
            onItemLongClickListener.onItemLongClicked(noteList[position])
        }
    }

    fun setList(notesList:List<Note>){
        noteList.clear()
        noteList.addAll(notesList)
    }

    inner class NoteViewHolder(val binding:NoteListViewBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int,note: Note,clickListener:(Int,Note)->Unit){
            binding.noteContentTextView.text=note.content
            binding.noteDateTextView.text=note.dateStamp
            binding.noteItemContainerCardView.setOnClickListener{
                clickListener(position,note)
            }
        }
    }
}
interface OnItemLongClickListener{
    fun onItemLongClicked(note: Note):Boolean
}