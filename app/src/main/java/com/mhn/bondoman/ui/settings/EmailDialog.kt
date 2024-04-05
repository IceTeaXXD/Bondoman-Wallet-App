package com.mhn.bondoman.ui.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.database.TransactionDao
import com.mhn.bondoman.utils.ExcelAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmailDialog : DialogFragment(){
    private lateinit var exporter: ExcelAdapter
    private lateinit var transactionDao: TransactionDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao()

        return AlertDialog.Builder(requireContext())
            .setMessage("Choose Export type")
            .setPositiveButton(".xlsx", null)
            .setNegativeButton(".xls", null)
            .create()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            export(".xlsx")
            dismiss()
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            export(".xls")
            dismiss()
        }
    }

    @SuppressLint("IntentReset")
    @OptIn(DelicateCoroutinesApi::class)
    private fun export(ext: String) {
        val context = requireContext()
        val email = KeyStoreManager.getInstance(requireContext()).getEmail() as String
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val transactions: List<Transaction> = transactionDao.index(email)
                exporter = ExcelAdapter(transactions, context)

                val file = if (ext == ".xlsx") {
                    exporter.convert(".xlsx",0)
                } else {
                    exporter.convert(".xls",0)
                }
                Log.d("File", file.toString())
                if (file != null) {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val dateSignature = dateFormat.format(Date())
                    val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", File(file))
                    val mIntent = Intent(Intent.ACTION_SEND)
                    mIntent.data = Uri.parse("mailto:")
                    mIntent.type = "application/octet-stream"
                    mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(KeyStoreManager.getInstance(context).getEmail()))
                    mIntent.putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan ${dateSignature}")
                    mIntent.putExtra(Intent.EXTRA_TEXT, "Laporan Transaksi")
                    mIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                    try {
                        context.startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                    } catch (e: Exception) {
                        Log.e("Email Error", e.message.toString())
                    }
                } else {
                    throw Exception("Failed to create Excel file.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("FileException", e.message.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to export file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    companion object {
        const val TAG = "EmailDialog"
    }

}