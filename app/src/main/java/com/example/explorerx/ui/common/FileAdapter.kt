package com.example.explorerx.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.explorerx.R
import com.example.explorerx.databinding.ListItemFileBinding
import com.example.explorerx.utils.readableSize
import java.io.File
import java.text.DateFormat

private const val TAG = "FileAdapter"

class FileHolder(
    private val binding: ListItemFileBinding
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var file: File

    private val df = DateFormat.getDateTimeInstance()

    fun bind(file: File, onFileClicked: () -> Unit, onOptionsClick: (v: View) -> Unit) {
        this.file = file
        binding.apply {
            fileName.text = file.name
            fileSize.text = if (file.isFile) file.readableSize() else ""
            fileDate.text = df.format(file.lastModified())
            if (file.isFile) {
                fileIcon.setImageResource(R.drawable.ic_baseline_insert_drive_file_24)
            } else {
                fileIcon.setImageResource(R.drawable.ic_baseline_folder_24)
            }
            root.setOnClickListener {
                onFileClicked()
            }
            options.setOnClickListener {
                onOptionsClick(it)
            }
        }
    }
}

class FileListAdapter(
    val files: MutableList<File>,
    private val onFileClicked: (file: File) -> Unit,
    private val onOptionsClick: (file: File, v: View) -> Unit
) : RecyclerView.Adapter<FileHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemFileBinding.inflate(inflater, parent, false)
        return FileHolder(binding)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val file = files[position]
        holder.bind(file, { onFileClicked(file) }) { view ->
            onOptionsClick(file, view)
        }
    }

    override fun getItemCount() = files.size
}