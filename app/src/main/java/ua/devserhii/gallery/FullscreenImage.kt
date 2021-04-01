package ua.devserhii.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fullscreen_image.*
import java.io.File


class FullscreenImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_image)

        ivDetail.setImageURI(Uri.parse(intent.getStringExtra("path")))
        bDelete.setOnClickListener { deleteImage() }
    }

    private fun deleteImage() {
        intent.getStringExtra("path")?.let { File(it).delete() }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}