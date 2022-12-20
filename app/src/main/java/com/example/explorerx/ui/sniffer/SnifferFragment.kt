package com.example.explorerx.ui.sniffer

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.explorerx.R
import com.example.explorerx.databinding.FragmentFileListBinding
import com.example.explorerx.ui.common.DupResultsAdapter
import com.example.explorerx.ui.common.FileFragment
import com.example.explorerx.utils.toast
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "SnifferFragment"

class SnifferFragment : FileFragment() {
    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!
    private val args: SnifferFragmentArgs by navArgs()
    private val snifferViewModel: SnifferViewModel by viewModels {
        SnifferViewModelFactory(args.fileUriString)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        binding.fileRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_sniffer, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.recheck -> {
                        snifferViewModel.scanAndStore()
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val listOfList = mutableListOf<MutableList<File>>()
        binding.apply {
            progressBar.visibility = View.VISIBLE
            fileRecyclerView.adapter = DupResultsAdapter(listOfList) { subBinding, f, ada ->
                subBinding.root.setOnClickListener {
                    popFileChooser(f)
                }
                subBinding.options.setOnClickListener { view ->
                    PopupMenu(requireContext(), view).apply {
                        inflate(R.menu.block_file_item)
                        setOnMenuItemClickListener { item ->
                            when(item.itemId) {
                                R.id.delete -> {
                                    binding.fileRecyclerView.adapter?.let {
                                        (it as DupResultsAdapter).apply {
                                            if (f.delete()) {
                                                snifferViewModel.deleteFile(f.absolutePath)
                                                ada.removeFile(f)
                                                if (ada.files.size == 1) {
                                                    removeList(ada.files)
                                                } else {
                                                    notifyItemChanged(it.listOfList.indexOf(ada.files))
                                                }
                                            } else {
                                                AlertDialog.Builder(requireContext())
                                                    .setMessage(getString(R.string.err_delete_file))
                                                    .show()
                                            }
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
        }
        snifferViewModel.testIfCached()
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                snifferViewModel.listOfList.collect {
                    it?.let {
                        if (it.isNotEmpty()) {
                            (binding.fileRecyclerView.adapter as DupResultsAdapter).addList(it)
                        } else {
                            "No duplicate files here!".toast()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
            launch {
                snifferViewModel.cached.collect { cached ->
                    cached?.let {
                        if (cached) {
                            snifferViewModel.sniff()
                        } else {
                            snifferViewModel.scanAndStore()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
