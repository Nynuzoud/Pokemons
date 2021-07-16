package com.sergeikuchin.pokemons.view.utils.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.sergeikuchin.pokemons.domain.models.AdapterModel

class DiffCallback(
    private val oldList: List<AdapterModel>,
    private val newList: List<AdapterModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean =
        oldList[oldPosition].areContentsTheSame(newList[newPosition])

}