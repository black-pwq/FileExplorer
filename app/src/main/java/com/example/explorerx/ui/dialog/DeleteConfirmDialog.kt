package com.example.explorerx.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs

class DeleteConfirmDialog : DialogFragment() {
    private val args: DeleteConfirmDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext()).create()
        return dialog.apply {
            setMessage("Sure to delete this file: ${args.fileName}?")
            setButton(DialogInterface.BUTTON_POSITIVE, "yes") { _, _ ->
                setFragmentResult(REQUEST_KEY_RES, bundleOf(BUNDLE_KEY_RES to true))
            }
            setButton(DialogInterface.BUTTON_NEGATIVE, "no"){ _, _ ->
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