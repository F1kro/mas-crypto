package com.example.mascrypto

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Base64

class MainActivity : AppCompatActivity() {

    private lateinit var inputTeks: EditText
    private lateinit var outputTeks: TextView
    private lateinit var spinnerMetode: Spinner
    private lateinit var tombolEnkripsi: Button
    private lateinit var tombolDekripsi: Button
    private lateinit var tombolHapus: Button

    private val daftarMetode = arrayOf(
        "Base64",
        "ROT13",
        "Caesar Cipher",
        "Vigenere Cipher",
        "Atbash"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inisialisasiKomponen()
        aturSpinner()
        aturTombol()
    }

    private fun inisialisasiKomponen() {
        inputTeks = findViewById(R.id.inputTeks)
        outputTeks = findViewById(R.id.outputTeks)
        spinnerMetode = findViewById(R.id.spinnerMetode)
        tombolEnkripsi = findViewById(R.id.tombolEnkripsi)
        tombolDekripsi = findViewById(R.id.tombolDekripsi)
        tombolHapus = findViewById(R.id.tombolHapus)
    }

    private fun aturSpinner() {
        val adaptor = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            daftarMetode
        )
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetode.adapter = adaptor
    }

    private fun aturTombol() {
        tombolEnkripsi.setOnClickListener {
            prosesKriptografi(true)
        }
        tombolDekripsi.setOnClickListener {
            prosesKriptografi(false)
        }
        tombolHapus.setOnClickListener {
            inputTeks.text.clear()
            outputTeks.text = ""
        }
    }

    private fun prosesKriptografi(enkripsi: Boolean) {
        val teksInput = inputTeks.text.toString().trim()

        if (teksInput.isEmpty()) {
            Toast.makeText(this, "Masukkan teks terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        val metodePilihan = spinnerMetode.selectedItem.toString()

        try {
            val hasil = when (metodePilihan) {
                "Base64" -> if (enkripsi) enkripsiBase64(teksInput) else dekripsiBase64(teksInput)
                "ROT13" -> prosesROT13(teksInput)
                "Caesar Cipher" -> if (enkripsi) enkripsiCaesar(teksInput, 3) else dekripsiCaesar(teksInput, 3)
                "Vigenere Cipher" -> if (enkripsi) enkripsiVigenere(teksInput, "KUNCI") else dekripsiVigenere(teksInput, "KUNCI")
                "Atbash" -> prosesAtbash(teksInput)
                else -> "Metode tidak dikenal"
            }
            outputTeks.text = hasil
        } catch (e: Exception) {
            outputTeks.text = "Error: ${e.message}"
            Toast.makeText(this, "Gagal memproses teks!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enkripsiBase64(teks: String): String {
        val bytes = teks.toByteArray(Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun dekripsiBase64(teks: String): String {
        val bytes = Base64.getDecoder().decode(teks)
        return String(bytes, Charsets.UTF_8)
    }

    private fun prosesROT13(teks: String): String {
        val hasilBuilder = StringBuilder()
        for (karakter in teks) {
            val hasilKode: Int = when {
                karakter in 'A'..'M' || karakter in 'a'..'m' -> karakter.code + 13
                karakter in 'N'..'Z' || karakter in 'n'..'z' -> karakter.code - 13
                else -> karakter.code
            }
            hasilBuilder.append(hasilKode.toChar())
        }
        return hasilBuilder.toString()
    }

    private fun enkripsiCaesar(teks: String, geser: Int): String {
        val hasilBuilder = StringBuilder()
        for (karakter in teks) {
            val hasilKarakter = when {
                karakter.isUpperCase() -> ((karakter.code - 'A'.code + geser) % 26 + 'A'.code).toChar()
                karakter.isLowerCase() -> ((karakter.code - 'a'.code + geser) % 26 + 'a'.code).toChar()
                else -> karakter
            }
            hasilBuilder.append(hasilKarakter)
        }
        return hasilBuilder.toString()
    }

    private fun dekripsiCaesar(teks: String, geser: Int): String {
        return enkripsiCaesar(teks, 26 - geser)
    }

    private fun enkripsiVigenere(teks: String, kunci: String): String {
        val hasilBuilder = StringBuilder()
        var indexKunci = 0
        val kunciUpper = kunci.uppercase()

        for (karakter in teks) {
            if (karakter.isLetter()) {
                val geser = kunciUpper[indexKunci % kunciUpper.length] - 'A'
                val hasilKarakter = if (karakter.isUpperCase()) {
                    ((karakter.code - 'A'.code + geser) % 26 + 'A'.code).toChar()
                } else {
                    ((karakter.code - 'a'.code + geser) % 26 + 'a'.code).toChar()
                }
                hasilBuilder.append(hasilKarakter)
                indexKunci++
            } else {
                hasilBuilder.append(karakter)
            }
        }
        return hasilBuilder.toString()
    }

    private fun dekripsiVigenere(teks: String, kunci: String): String {
        val hasilBuilder = StringBuilder()
        var indexKunci = 0
        val kunciUpper = kunci.uppercase()

        for (karakter in teks) {
            if (karakter.isLetter()) {
                val geser = kunciUpper[indexKunci % kunciUpper.length] - 'A'
                val hasilKarakter = if (karakter.isUpperCase()) {
                    ((karakter.code - 'A'.code - geser + 26) % 26 + 'A'.code).toChar()
                } else {
                    ((karakter.code - 'a'.code - geser + 26) % 26 + 'a'.code).toChar()
                }
                hasilBuilder.append(hasilKarakter)
                indexKunci++
            } else {
                hasilBuilder.append(karakter)
            }
        }
        return hasilBuilder.toString()
    }

    private fun prosesAtbash(teks: String): String {
        val hasilBuilder = StringBuilder()
        for (karakter in teks) {
            val hasilKarakter = when {
                karakter.isUpperCase() -> ('A'.code + 'Z'.code - karakter.code).toChar()
                karakter.isLowerCase() -> ('a'.code + 'z'.code - karakter.code).toChar()
                else -> karakter
            }
            hasilBuilder.append(hasilKarakter)
        }
        return hasilBuilder.toString()
    }
}