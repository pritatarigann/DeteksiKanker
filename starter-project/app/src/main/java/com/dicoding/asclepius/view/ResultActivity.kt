package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.utils.formatToString
import com.dicoding.asclepius.view.viewModel.ResultViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel by lazy {
        ViewModelProvider(this)[ResultViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(IMAGE_URI).orEmpty()
        val confidenceScore = intent.getFloatExtra(CONFIDENCE_SCORE, 0.0f) * 100
        val result = intent.getStringExtra(RESULT).orEmpty()
        val detail = intent.getBooleanExtra(DETAIL_PAGE, false)

        // Check if the record has already been saved
        val recordSaved = savedInstanceState?.getBoolean(RECORD_SAVED_KEY) ?: false

        if (!recordSaved && !detail) {
            viewModel.saveRecord(imageUri, confidenceScore / 100, result)
        }

        with(binding) {
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                finish()
            }

            articlesButton.setOnClickListener {
                startActivity(Intent(this@ResultActivity, ArticlesActivity::class.java))
            }

            resultImage.setImageURI(imageUri.toUri())
            resultText.text = getString(R.string.result, result)
            score.text = confidenceScore.formatToString().plus("%")
            progressScore.progress = confidenceScore.toInt()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the flag indicating whether the record has been saved
        outState.putBoolean(RECORD_SAVED_KEY, true)
    }

    companion object {
        const val IMAGE_URI = "image_uri"
        const val CONFIDENCE_SCORE = "confidence_score"
        const val RESULT = "result"
        const val INFERENCE_TIME = "inference_time"
        const val DETAIL_PAGE = "detail_page"
        const val RECORD_SAVED_KEY = "record_saved"
    }

}
