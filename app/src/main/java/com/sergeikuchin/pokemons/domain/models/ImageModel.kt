package com.sergeikuchin.pokemons.domain.models

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

data class ImageModel(
    val url: String? = null
) {

    fun loadInto(
        imageView: AppCompatImageView,
        @DrawableRes placeHolder: Int,
        loadFromCacheOnly: Boolean = false,
        originalSize: Boolean = false,
        onLoadSuccess: (() -> Unit)? = null,
        onLoadFailed: (() -> Unit)? = null,
        finally: (() -> Unit)? = null,
    ) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeHolder)
            .apply {
                if (originalSize) {
                    override(Target.SIZE_ORIGINAL)
                }
            }
            .onlyRetrieveFromCache(loadFromCacheOnly)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadFailed?.invoke()
                    finally?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadSuccess?.invoke()
                    finally?.invoke()
                    return false
                }
            })
            .into(imageView)
    }
}