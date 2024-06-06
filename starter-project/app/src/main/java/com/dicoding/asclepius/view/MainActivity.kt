package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    private val galleryResult = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            val filename = UUID.randomUUID().toString().plus(".jpg")
            UCrop.of(it, Uri.fromFile(File(cacheDir, filename))).start(this, CROP_REQUEST)
        } else Log.d("Photo Picker", "No media selected")
    }

    private val imageClassifierHelper by lazy {
        ImageClassifierHelper(contextValue = this, onError = {
            showToast(it)
            setLoading(false)
        }, onResult = { results, inferenceTime ->
            setLoading(false)
            if (!results.isNullOrEmpty()) {
                val result = results.first()
                val data = result.categories.first()
                moveToResult(data.label, data.score, inferenceTime)
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    private fun startGallery() {
        galleryResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            // Disable the analyze button to prevent multiple clicks
//            binding.analyzeButton.isEnabled = false

            setLoading(true)
            lifecycleScope.launch(Dispatchers.Default) {
                imageClassifierHelper.classifyImage(currentImageUri!!)
            }
        } else {
            showToast(getString(R.string.empty_image))
        }
    }

    private fun moveToResult(result: String, confidenceScore: Float, inferenceTime: Long) {
        val intent = Intent(this, ResultActivity::class.java)
        currentImageUri?.let { uri ->
            intent.putExtra(ResultActivity.RESULT, result)
            intent.putExtra(ResultActivity.CONFIDENCE_SCORE, confidenceScore)
            intent.putExtra(ResultActivity.INFERENCE_TIME, inferenceTime)
            intent.putExtra(ResultActivity.IMAGE_URI, currentImageUri.toString())
            intent.putExtra(ResultActivity.DETAIL_PAGE, false)
            startActivity(intent)
        } ?: showToast(getString(R.string.image_classifier_failed))

    }

    private fun setLoading(value: Boolean) {
        runOnUiThread {
            binding.apply {
                if (value) {
                    progressIndicator.visibility = View.VISIBLE
                    analyzeButton.visibility = View.INVISIBLE
                } else {
                    progressIndicator.visibility = View.INVISIBLE
                    analyzeButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == CROP_REQUEST) {
            currentImageUri = UCrop.getOutput(data!!)
            showImage()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val CROP_REQUEST = 101
    }
}