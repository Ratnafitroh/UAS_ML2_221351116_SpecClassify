package com.example.specclassify.ui.tentang

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.specclassify.R

class TentangFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tentang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linkKaggle: TextView = view.findViewById(R.id.link_kaggle)
        linkKaggle.setOnClickListener {
            val url = "https://www.kaggle.com/datasets/fedesoriano/stellar-classification-dataset-sdss17"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            try {
                startActivity(intent)
            } catch (e: Exception) {
            }
        }
        try {
            val versionText: TextView = view.findViewById(R.id.text_app_version)
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            versionText.text = "Versi $version"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
