package ua.devserhii.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ua.devserhii.gallery.adapter.Adapter
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private const val CAMERA_CAPTURE = 0
    }

    private var adapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessPermission()

        camera.setOnClickListener { openCamera() }
    }

    private fun accessPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, REQUEST_EXTERNAL_STORAGE)
        } else {
            if (isExternalStorageWritable()) {
                ensurePackageCreated()
                getActualPhotoList()
                initRecyclerView()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            accessPermission()
        }

    }

    private fun ensurePackageCreated() {
        val dir = File(
            Environment.getExternalStorageDirectory(),
            File.separator + "myGalleryPhoto"
        )
        if (!dir.exists())
            dir.mkdirs()

    }

    private fun initRecyclerView() {
        rv_view.layoutManager = GridLayoutManager(this, 2)
        adapter = Adapter {
            openFullScreenImage(it)
        }
        rv_view.adapter = adapter
        adapter?.update(getActualPhotoList())
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    }

    private fun getActualPhotoList(): List<String> {
        val folder = File(
            Environment.getExternalStorageDirectory(),
            File.separator + "myGalleryPhoto"
        )
        return processFilesFromFolder(folder, mutableSetOf()).toList()
    }

    private fun openCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(captureIntent, CAMERA_CAPTURE)
    }

    private fun openFullScreenImage(path: String) {
        val intent = Intent(this, FullscreenImage::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap ?: return
            try {
                val fos =
                    FileOutputStream(
                        Environment.getExternalStorageDirectory()
                            .toString()
                                + File.separator + "myGalleryPhoto" + File.separator
                                + "image" + (0..Int.MAX_VALUE).random() + ".jpg"
                    )
                imageBitmap.compress(CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()

                getActualPhotoList()
                adapter?.update(getActualPhotoList())
            } catch (e: Exception) {
                Log.e("MyLog", e.toString())
            }
        }
    }

    private fun processFilesFromFolder(folder: File, result: MutableSet<String>): Set<String> {
        val folderEntries = folder.listFiles()
        if (folderEntries.isNullOrEmpty())
            return result
        for (entry in folderEntries) {
            if (entry.isDirectory) {
                result.addAll(processFilesFromFolder(entry, result))
                continue
            }
            if (!result.contains(entry.absolutePath))
                result.add(entry.absolutePath)
        }
        return result
    }
}