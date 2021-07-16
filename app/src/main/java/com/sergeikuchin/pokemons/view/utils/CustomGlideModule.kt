package com.sergeikuchin.pokemons.view.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class CustomGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSize = 1024L * 1024L * 100L // 100 MB
        builder.apply {
            setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSize))
            setDefaultRequestOptions(getDefaultOptions())
        }
    }

    private fun getDefaultOptions() =
        RequestOptions().apply {
            dontTransform()
        }
}