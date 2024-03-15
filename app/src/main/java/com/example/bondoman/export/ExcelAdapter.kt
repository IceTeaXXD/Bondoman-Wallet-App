package com.example.bondoman.export

import com.example.bondoman.database.Transaction
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import kotlin.io.path.outputStream

class ExcelAdapter(val transactions: List<Transaction>) {
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

        val tempFile = kotlin.io.path.createTempFile("test_output_", type)
        workbook.write(tempFile.outputStream())
        workbook.close()
    }
}