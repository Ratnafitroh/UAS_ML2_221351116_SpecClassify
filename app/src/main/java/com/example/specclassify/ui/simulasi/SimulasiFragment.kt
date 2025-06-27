package com.example.specclassify.ui.simulasi

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.specclassify.R
import com.google.android.material.textfield.TextInputEditText
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class SimulasiFragment : Fragment() {

    // Deklarasi Interpreter TFLite
    private lateinit var tflite: Interpreter

    private lateinit var inputU: TextInputEditText
    private lateinit var inputG: TextInputEditText
    private lateinit var inputR: TextInputEditText
    private lateinit var inputI: TextInputEditText
    private lateinit var inputZ: TextInputEditText
    private lateinit var inputRedshift: TextInputEditText
    private lateinit var btnPredict: Button
    private lateinit var cardResult: CardView
    private lateinit var imageResult: ImageView
    private lateinit var textResult: TextView

    private val scale = floatArrayOf(
        9.96831930e-05f, 9.96949112e-05f, 5.06334498e-02f,
        4.41081113e-02f, 9.97169660e-05f, 1.42425478e-01f
    )
    private val offset = floatArrayOf(
        0.99673225f, 0.99684942f, -0.49732529f,
        -0.41769954f, 0.99706994f, 0.00142008f
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_simulasi, container, false)

        // Inisialisasi Views
        inputU = view.findViewById(R.id.input_u)
        inputG = view.findViewById(R.id.input_g)
        inputR = view.findViewById(R.id.input_r)
        inputI = view.findViewById(R.id.input_i)
        inputZ = view.findViewById(R.id.input_z)
        inputRedshift = view.findViewById(R.id.input_redshift)
        btnPredict = view.findViewById(R.id.btn_predict)
        cardResult = view.findViewById(R.id.card_result)
        imageResult = view.findViewById(R.id.image_result)
        textResult = view.findViewById(R.id.text_result)

        // Memuat model TFLite saat fragment dibuat
        try {
            tflite = Interpreter(loadModelFile(requireActivity().assets, "stellar.tflite"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal memuat model TFLite.", Toast.LENGTH_SHORT).show()
        }
        btnPredict.setOnClickListener {
            if (validateInputs()) {
                val result = doInference()
                displayResult(result)
            }
        }

        return view
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun doInference(): String {
        val raw = floatArrayOf(
            inputU.text.toString().toFloat(),
            inputG.text.toString().toFloat(),
            inputR.text.toString().toFloat(),
            inputI.text.toString().toFloat(),
            inputZ.text.toString().toFloat(),
            inputRedshift.text.toString().toFloat()
        )
        val inputBuffer = ByteBuffer.allocateDirect(4 * 6)
            .order(ByteOrder.nativeOrder())
        for (i in raw.indices) {
            val scaled = raw[i] * scale[i] + offset[i]   // x_scaled
            inputBuffer.putFloat(scaled)
        }
        inputBuffer.rewind()

        val outputBuffer = ByteBuffer.allocateDirect(4 * 3)
            .order(ByteOrder.nativeOrder())

        // Menjalankan interpreter
        tflite.run(inputBuffer, outputBuffer)

        // ekstrak probabilitas
        outputBuffer.rewind()
        val probs = FloatArray(3)
        outputBuffer.asFloatBuffer().get(probs)

        // kelas dengan probabilitas tertinggi
        val classes = arrayOf("GALAXY", "QSO", "STAR")
        val maxIdx = probs.indices.maxByOrNull { probs[it] } ?: 0
        return classes[maxIdx]
    }

    private fun displayResult(result: String) {
        cardResult.visibility = View.VISIBLE
        textResult.text = result

        when (result) {
            "STAR" -> imageResult.setImageResource(R.drawable.stars)
            "GALAXY" -> imageResult.setImageResource(R.drawable.galaxyy)
            "QSO" -> imageResult.setImageResource(R.drawable.quasar)
        }
    }

    private fun validateInputs(): Boolean {
        if (inputU.text.isNullOrEmpty() ||
            inputG.text.isNullOrEmpty() ||
            inputR.text.isNullOrEmpty() ||
            inputI.text.isNullOrEmpty() ||
            inputZ.text.isNullOrEmpty() ||
            inputRedshift.text.isNullOrEmpty()) {
            Toast.makeText(context, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tflite.isInitialized) {
            tflite.close()
        }
    }
}
