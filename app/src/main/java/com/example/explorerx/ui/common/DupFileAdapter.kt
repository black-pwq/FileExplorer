package com.example.explorerx.ui.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.explorerx.R
import com.example.explorerx.databinding.BlockFileItemBinding
import com.example.explorerx.databinding.BlockFilesBinding
import com.example.explorerx.utils.readableSize
import java.io.File
import java.text.DateFormat

private const val TAG = "DupFileListAdapter"

class DupFileHolder(
    private val binding: BlockFileItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var file: File
    var listIndex = 0

    private val df = DateFormat.getDateTimeInstance()

    fun bind(file: File, ada: DupFileListAdapter, onClickItem: (binding: BlockFileItemBinding, file: File, ada: DupFileListAdapter) -> Unit) {
        this.file = file
        this.listIndex = listIndex
        binding.apply {
            filePath.text = file.parent
            fileName.text = file.name
            fileSize.text = if (file.isFile) file.readableSize() else ""
            fileDate.text = df.format(file.lastModified())
            if (file.isFile) {
                fileIcon.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
            } else {
                fileIcon.setImageResource(R.drawable.ic_baseline_folder_24)
            }
            onClickItem(binding, file, ada)
        }

    }
}

class DupFileListAdapter(
    val files: MutableList<File>,
    private val onClickItem: (binding: BlockFileItemBinding, file: File, ada: DupFileListAdapter) -> Unit
) : RecyclerView.Adapter<DupFileHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DupFileHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlockFileItemBinding.inflate(inflater, parent, false)
        return DupFileHolder(binding)
    }

    override fun onBindViewHolder(holder: DupFileHolder, position: Int) {
        val file = files[position]
        holder.bind(file, this, onClickItem)
    }

    fun removeFile(file: File) {
        val ndx = files.indexOf(file)
        files.removeAt(ndx)
        notifyItemRemoved(ndx)
    }

    override fun getItemCount() = files.size
}


class DupResultsHolder(
    private val binding: BlockFilesBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(files: MutableList<File>, adapter: DupFileListAdapter): DupFileListAdapter {
        binding.apply {
            title.text = "${files.size} results"
            duplicateFileList.layoutManager = LinearLayoutManager(binding.root.context)
            duplicateFileList.adapter = adapter
        }
        return binding.duplicateFileList.adapter as DupFileListAdapter
    }
}

class DupResultsAdapter(
    var listOfList: MutableList<MutableList<File>>,
    private val onClickItem: (binding: BlockFileItemBinding, file: File, ada: DupFileListAdapter) -> Unit
) : RecyclerView.Adapter<DupResultsHolder>() {
    val adapters: MutableList<DupFileListAdapter> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DupResultsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlockFilesBinding.inflate(inflater, parent, false)
        return DupResultsHolder(binding)
    }

    override fun onBindViewHolder(holder: DupResultsHolder, position: Int) {
        val files = listOfList[position]
        holder.bind(files, adapters[position])
    }

    @Synchronized fun removeList(list: MutableList<File>) {
        val pos = listOfList.indexOf(list)
        if (pos != -1) {
            listOfList.removeAt(pos)
            adapters.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    @Synchronized fun addList(list: MutableList<File>) {
        listOfList += list
        adapters.add(DupFileListAdapter(list, onClickItem))
        notifyItemInserted(listOfList.size - 1)
    }

    override fun getItemCount() = listOfList.size
}