package com.bignerdranch.android.webFragment.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.asLiveData
import com.bignerdranch.android.application_practica2.databinding.FragmentHomeBinding
import com.bignerdranch.android.webFragment.database.MyData
import com.bignerdranch.android.webFragment.database.MyDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var ivMyImage: ImageView
    private lateinit var imageUrl: Uri
    private lateinit var db: MyDataBase
    private lateinit var editText: EditText
    private lateinit var editText2: EditText
    private lateinit var editText3: EditText
    private lateinit var buttonSave: Button

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Доступ к камере отклонен", Toast.LENGTH_SHORT).show()
        }
    }

    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            ivMyImage.setImageURI(imageUrl)
        } else {
            Toast.makeText(requireContext(), "Не удалось сделать фото", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imageUrl = createImageUri()
        ivMyImage = binding.imageView
        editText = binding.Name
        editText2 = binding.Surname
        editText3 = binding.Group
        buttonSave = binding.buttonSave

        ivMyImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        db = MyDataBase.getInstance(requireContext())

        // Загружаем данные из базы данных и обновляем UI
        loadDataFromDatabase()

        buttonSave.setOnClickListener {
            saveDataToDatabase()
        }

        return root
    }

    private fun loadDataFromDatabase() {
        db.getDbDao().query().asLiveData().observe(viewLifecycleOwner) { dataList ->
            if (dataList.isNotEmpty()) {
                val data = dataList[0]
                Log.d("HomeFragment", "Data loaded: $data")
                updateUI(data)
            } else {
                Log.d("HomeFragment", "Data list is empty")
            }
        }
    }

    private fun updateUI(data: MyData) {
        editText.setText(data.name)
        editText2.setText(data.surname)
        editText3.setText(data.group)
        val bitmap = data.Image?.let { BitmapFactory.decodeByteArray(data.Image, 0, it.size) }
        ivMyImage.setImageBitmap(bitmap)
    }

    private fun saveDataToDatabase() {
        val name = editText.text.toString()
        val surname = editText2.text.toString()
        val group = editText3.text.toString()
        val imageBytes = convertImageToBytes(ivMyImage)

        if (name.isEmpty() || surname.isEmpty() || group.isEmpty() || imageBytes == null) {
            Log.e("HomeFragment", "All fields must be filled and image must be taken")
            return
        }

        Log.d("HomeFragment", "Image bytes length: ${imageBytes.size}")

        val data = MyData(
            PrimaryKey = 1, // Обратите внимание на значение PrimaryKey
            Image = imageBytes,
            name = name,
            surname = surname,
            group = group
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    db.getDbDao().insert(data) // Используйте insert вместо update
                    Log.d("HomeFragment", "Data saved successfully: $data")
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error saving data", e)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createImageUri(): Uri {
        val image = File(requireActivity().filesDir, "myPhoto.png")
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().applicationInfo.packageName}.fileprovider",
            image
        )
    }

    private fun convertImageToBytes(imageView: ImageView): ByteArray? {
        try {
            val drawable = imageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap

                // Сначала попробуем сжать изображение до приемлемого размера
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val compressedBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))

                // Теперь конвертируем сжатое изображение в массив байтов
                val compressedStream = ByteArrayOutputStream()
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, compressedStream)
                return compressedStream.toByteArray()
            }
        } catch (e: OutOfMemoryError) {
            Log.e("HomeFragment", "Out of memory error while converting image to bytes", e)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error converting image to bytes", e)
        }
        return null
    }


    private fun launchCamera() {
        contract.launch(imageUrl)
    }
}
