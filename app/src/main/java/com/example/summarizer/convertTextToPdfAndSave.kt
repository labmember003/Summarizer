package com.example.summarizer

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.io.FileOutputStream


fun convertTextToPdfAndSave(context: Context, text: String) {
    PDFBoxResourceLoader.init(context)
    val document = PDDocument()
    val page = PDPage(PDRectangle.A4)
    document.addPage(page)

    val contentStream = PDPageContentStream(document, page)

    val margin = 25f
    val startY = page.mediaBox.height - margin
    val contentWidth = page.mediaBox.width - 2 * margin
    val fontSize = 12f
    val leading = 1.5 * fontSize

    contentStream.beginText()
    contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize)
    contentStream.newLineAtOffset(margin, startY)

    val lines = text.split("\n")
    for (line in lines) {
        val words = line.trim().split(" ")
        var currentLine = ""
        for (word in words) {
            val width = PDType1Font.HELVETICA_BOLD.getStringWidth(currentLine + word) / 1000 * fontSize
            if (width > contentWidth) {
                contentStream.showText(currentLine)
                contentStream.newLineAtOffset(0f, (-leading).toFloat())
                currentLine = "$word "
            } else {
                currentLine += "$word "
            }
        }
        contentStream.showText(currentLine)
        contentStream.newLineAtOffset(0f, (-leading).toFloat())
    }

    contentStream.endText()
    contentStream.close()

    val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsFolder, "output.pdf")

    FileOutputStream(pdfFile).use { outputStream ->
        document.save(outputStream)
        Toast.makeText(context, "File Saved In Downloads", Toast.LENGTH_SHORT).show()
    }

    document.close()
}