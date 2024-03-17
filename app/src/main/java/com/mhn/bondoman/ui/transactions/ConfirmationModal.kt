package com.mhn.bondoman.ui.transactions

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
class ConfirmationModal : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener?.onDialogPositiveClick()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface ConfirmationDialogListener {
        fun onDialogPositiveClick()
    }

    private var listener: ConfirmationDialogListener? = null

    fun setConfirmationDialogListener(listener: ConfirmationDialogListener) {
        this.listener = listener
    }
}