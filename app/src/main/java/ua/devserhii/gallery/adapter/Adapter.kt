package ua.devserhii.gallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.example_image_item.view.*
import ua.devserhii.gallery.R

class Adapter(
    private val callback: (String) -> Unit
) : RecyclerView.Adapter<Adapter.CatViewHolder>() {
    private var images: List<String> = emptyList()

    override fun getItemCount() = images.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.example_image_item, parent, false)

        return CatViewHolder(itemView, callback)
    }

    fun update(newValues: List<String>) {
        images = newValues
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(images[position])
    }

    class CatViewHolder(
        itemView: View,
        private val callback: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String) {
            with(itemView) {
                Glide.with(this)
                    .load(item)
                    .into(ivPicture)

                tvPath?.text = item
                ivPicture.setOnClickListener { callback.invoke(item) }
            }
        }
    }
}