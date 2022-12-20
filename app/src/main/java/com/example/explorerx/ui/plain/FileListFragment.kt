package com.example.explorerx.ui.plain

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.explorerx.R
import com.example.explorerx.databinding.FragmentFileListBinding
import com.example.explorerx.ui.common.FileFragment
import com.example.explorerx.ui.common.FileListAdapter
import com.example.explorerx.utils.toast
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "FileListFragment"

class FileListFragment : FileFragment() {

    private val args: FileListFragmentArgs by navArgs()
    private val fileListViewModel: FileListViewModel by viewModels {
        FileListViewModelFactory(args.fileUriString)
    }

    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        binding.fileRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.GONE
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.fragment_file_list, menu)
                searchItem = menu.findItem(R.id.search)
                searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    @Override
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null && newText != "") {
                            fileListViewModel.search(newText)
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.view_in_tree_map -> {
                        findNavController().navigate(
                            FileListFragmentDirections.viewInTreeMap(fileListViewModel.mountPath)
                        )
                        true
                    }
                    R.id.duplicate_files -> {
                        findNavController().navigate(
                            FileListFragmentDirections.detectDupFiles(fileListViewModel.mountPath)
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileListViewModel.files.collect {
                    it?.let {
                        updateUI(it)
                        if (it.isEmpty()) {
                            "No files here".toast()
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(files: MutableList<File>) {
        binding.fileRecyclerView.adapter = FileListAdapter(files, { file ->
            if (file.isDirectory) {
                findNavController().navigate(
                    FileListFragmentDirections.intoSubdir(
                        Uri.fromFile(file).toString()
                    )
                )
            } else {
                popFileChooser(file)
            }
        }) { file, view ->
            PopupMenu(requireContext(), view).apply {
                inflate(R.menu.list_item_file)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete -> {
                            binding.fileRecyclerView.adapter?.let { it ->
                                if (file.deleteRecursively()) {
                                    (it as FileListAdapter).apply {
                                        val pos = it.files.indexOf(file)
                                        files.removeAt(pos)
                                        notifyItemRemoved(pos)
                                    }
                                    "delete success".toast()
                                } else {
                                    AlertDialog.Builder(requireContext())
                                        .setMessage(getString(R.string.err_delete_file))
                                        .show()
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }
                show()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        fileListViewModel.refresh()
    }

    override fun onPause() {
        super.onPause()
        searchItem.collapseActionView()
    }
}