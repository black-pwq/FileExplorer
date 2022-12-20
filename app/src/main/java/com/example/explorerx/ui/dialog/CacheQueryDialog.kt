package com.example.explorerx.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class CacheQueryDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext()).create()
        return dialog.apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            setMessage("Detect cached result from last scanning. It may not showing correct figure if " +
                    "files changed or differ from last scanning, but if you are sure there is no change, " +
                    "reading from cache is the fastest way to see the result. Are you sure to RESCAN?")
            setButton(DialogInterface.BUTTON_POSITIVE, "rescan") { _, _ ->
                setFragmentResult(REQUEST_KEY_RES, bundleOf(BUNDLE_KEY_RES to true))
            }
            setButton(DialogInterface.BUTTON_NEGATIVE, "read from cache"){ _, _ ->
                setFragmentResult(REQUEST_KEY_RES, bundleOf(BUNDLE_KEY_RES to false))
            }
            show()
        }
    }

    companion object {
        const val REQUEST_KEY_RES = "REQUEST_KEY_RES"
        const val BUNDLE_KEY_RES = "BUNDLE_KEY_RES"
    }
}