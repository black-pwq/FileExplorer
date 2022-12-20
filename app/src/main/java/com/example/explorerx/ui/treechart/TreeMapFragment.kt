package com.example.explorerx.ui.treechart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.explorerx.databinding.FragmentTreePageBinding
import com.example.explorerx.repository.IORepo
import com.example.explorerx.ui.common.FileFragment
import com.example.explorerx.ui.dialog.CacheQueryDialog
import com.example.explorerx.utils.chart.fillRawHtml
import com.example.explorerx.utils.toast
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "TreeMapFragment"
private const val HEADER = "['Full Path', 'Parent Path', 'Size']"
private const val CACHE = "treemap.html"

class TreeMapFragment : FileFragment() {
	private var _binding: FragmentTreePageBinding? = null
	private val binding: FragmentTreePageBinding get() = _binding!!

	private val args: TreeMapFragmentArgs by navArgs()
	private val treeMapViewModel: TreeMapViewModel by viewModels {
		TreeMapViewModelFactory(args.fileUriString)
	}
	private var onBackCallback: OnBackPressedCallback? = null

	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = FragmentTreePageBinding.inflate(inflater, container, false)
		binding.treeMapWebView.apply {
			addJavascriptInterface(
				treeMapViewModel.OnEnterDirCallBack(),
				"OnEnterDirCallBack"
			)
			addJavascriptInterface(TreeMapIf(), "TreeMapIf")
			settings.apply {
				defaultTextEncodingName = "utf-8"
				javaScriptEnabled = true
				allowFileAccess = true
				allowContentAccess = true
				setSupportZoom(true)
				builtInZoomControls = true
				displayZoomControls = false
				useWideViewPort = true
				layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
				loadWithOverviewMode = true
			}
			webViewClient = object : WebViewClient() {
				override fun onPageStarted(
					view: WebView?,
					url: String?,
					favicon: Bitmap?
				) {
					if (_binding != null) {
						binding.progressHint.text = "Rendering"
						showBlocking()
					}
				}

				override fun onPageFinished(view: WebView?, url: String?) {
					if (_binding != null) {
						completeTask()
					}
				}
			}
			webChromeClient = object : WebChromeClient() {
				override fun onConsoleMessage(message: ConsoleMessage): Boolean {
					Log.d(
						TAG, "${message.message()} -- From line " +
								"${message.lineNumber()}"
					)
					return true
				}
			}
		}
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.progressHint.text = "Loading"
		showBlocking()
		viewLifecycleOwner.lifecycleScope.apply {
			val dataFile = File(requireActivity().filesDir, CACHE)
			treeMapViewModel.testIfCached()
			launch {
				treeMapViewModel.cached.collect { cached ->
					cached?.let {
						if (cached) {
							completeTask()
							findNavController().navigate(
								TreeMapFragmentDirections.cacheQuery()
							)
//                            treeMapViewModel.readCacheInDb()
						} else {
							rescan()
						}
					}
				}
			}
			launch {
				treeMapViewModel.data.collect { data ->
					data?.let {
						if (it.size > 1) {
							val filledHtml = fillRawHtml(readRawHtml(), HEADER, data)

							writeToInternal(filledHtml, CACHE)
							updateUiFromFile(dataFile)
						} else {
							completeTask()
							"No files here".toast()
						}
					}
				}
			}
			setFragmentResultListener(CacheQueryDialog.REQUEST_KEY_RES) { _, bundle ->
				val res = bundle.getBoolean(CacheQueryDialog.BUNDLE_KEY_RES)
				showBlocking()
				if (res) {
					binding.progressHint.text = "Scanning files"
					treeMapViewModel.scanAndStore()
				} else {
					binding.progressHint.text = "Loading from cache"
					treeMapViewModel.readCacheInDb()
				}
			}
		}
	}


	override fun onStart() {
		super.onStart()
		onBackCallback = requireActivity().onBackPressedDispatcher.addCallback {
			binding.treeMapWebView.loadUrl("javascript:rollUp()")
			isEnabled = false
		}
		viewLifecycleOwner.lifecycleScope.launch {
			treeMapViewModel.currDir.collect {
				if (it != treeMapViewModel.mountPath) {
					onBackCallback?.apply {
						isEnabled = true
					}

				} else {
					onBackCallback?.apply {
						isEnabled = false
					}
				}
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		onBackCallback?.remove()
		binding.treeMapWebView.stopLoading()
		_binding = null
	}

	private fun showBlocking() {
		binding.apply {
			progressBar.visibility = View.VISIBLE
			progressHint.visibility = View.VISIBLE
		}
	}

	private fun completeTask() {
		binding.apply {
			progressBar.visibility = View.GONE
			progressHint.visibility = View.GONE
		}
	}

	private fun updateUiFromFile(fromFile: File) {
		binding.treeMapWebView.apply {
			loadUrl(fromFile.toUri().toString())
		}
	}

	private fun rescan() {
		binding.progressHint.text = "Scanning files"
		treeMapViewModel.scanAndStore()
	}

	private suspend fun readRawHtml() = requireContext().assets.run {
		open("googlechart.html").use { htmlStream ->
			open("treemap.js").use { jsStream ->
				String.format(
					IORepo.readText(htmlStream),
					IORepo.readText(jsStream)
				)
			}
		}
	}

	private suspend fun writeToInternal(str: String, name: String) {
		requireContext().openFileOutput(name, Context.MODE_PRIVATE).use { fos ->
			IORepo.writeText(str, fos)
		}
	}

	inner class TreeMapIf() {
		@JavascriptInterface
		fun onClickFile(fileUriStr: String) {
			val file = File(Uri.parse(fileUriStr).path!!)
			if (file.isFile) {
				popFileChooser(file)
			}
		}
	}
}