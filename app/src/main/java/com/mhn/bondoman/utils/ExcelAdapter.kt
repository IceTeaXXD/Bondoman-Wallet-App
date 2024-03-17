package com.mhn.bondoman.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.mhn.bondoman.database.Transaction
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.io.path.outputStream

class ExcelAdapter(val transactions: List<Transaction>, val context: Context) {
    fun convert(type: String) {
        val workbook = XSSFWorkbook()
        val worksheet = workbook.createSheet()
        val cellStyle = workbook.createCellStyle()

        // Define Header
        val columns: MutableList<String> = mutableListOf()
        columns.apply {
            add("Tanggal")
            add("Kategori")
            add("Nominal")
            add("Nama")
            add("Lokasi")
        }

        // Iterate one by one
        val row = worksheet.createRow(0)
        for (i in columns.indices) {
            row.createCell(i).setCellValue(columns[i])
        }

        // Define body
        for (i in transactions.indices) {
            val row = worksheet.createRow(i + 1)
            val transaction = transactions[i]

            row
                .createCell(0)
                .setCellValue(transaction.transaction_date)

            row
                .createCell(1)
                .setCellValue(transaction.transaction_category)

            row
                .createCell(2)
                .setCellValue(transaction.transaction_price.toDouble())

            row
                .createCell(3)
                .setCellValue(transaction.transaction_name)

            row
                .createCell(4)
                .setCellValue(transaction.transaction_location)
        }

        try {
            val tempFile = kotlin.io.path.createTempFile("test_output_", type)
            workbook.write(tempFile.outputStream())
            workbook.close()

            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val dateSignature = dateFormat.format(Date())
            val fileName = "transactions_${dateSignature}$type"
            val targetFile = File(downloadFolder, fileName)
            tempFile.toFile().copyTo(targetFile, overwrite = true)
            tempFile.toFile().delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}