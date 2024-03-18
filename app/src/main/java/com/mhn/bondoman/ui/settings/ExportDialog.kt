package com.mhn.bondoman.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.database.TransactionDao
import com.mhn.bondoman.utils.ExcelAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExportDialog : DialogFragment() {
    private lateinit var exporter: ExcelAdapter
    private lateinit var transactionDao: TransactionDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao()

        return AlertDialog.Builder(requireContext())
            .setMessage("Choose Export type")
            .setPositiveButton(".xlsx") { _, _ -> export(".xlsx") }
            .setNegativeButton(".xls") { _, _ -> export(".xls") }
            .create()
    }

    private fun export(ext: String) {
        val context = requireContext()
        val email = KeyStoreManager.getInstance(requireContext()).getEmail() as String
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val transactions: List<Transaction> =
                    transactionDao.index(email)
                exporter = ExcelAdapter(transactions, context)

                if (ext == ".xlsx") {
                    exporter.convert(".xlsx", 1)
                } else if (ext == ".xls") {
                    exporter.convert(".xls",1)
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val TAG = "ExportDialog"
    }
}