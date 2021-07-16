package com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sergeikuchin.pokemons.R
import com.sergeikuchin.pokemons.databinding.ItemPokemonBinding
import com.sergeikuchin.pokemons.domain.models.PokemonModel
import com.sergeikuchin.pokemons.view.utils.ViewEventContext
import com.sergeikuchin.pokemons.view.utils.ViewEventDelegate
import com.sergeikuchin.pokemons.view.utils.recycler_view.DiffCallback

class PokemonsListAdapter(
    private val viewEventDelegate: ViewEventDelegate
) : RecyclerView.Adapter<PokemonViewHolder>() {

    var items: List<PokemonModel> = emptyList()
        set(value) {
            val diffCallback = DiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder =
        ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let { binding ->
                val viewHolder = PokemonViewHolder(binding)
                setOnItemClickListener(binding, viewHolder)
                viewHolder
            }


    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = items[position].hashCode().toLong()

    private fun setOnItemClickListener(binding: ItemPokemonBinding, viewHolder: PokemonViewHolder) {
        binding.root.setOnClickListener {
            items[viewHolder.adapterPosition].let { item ->
                viewEventDelegate.onViewEvent(
                    PokemonItemEventData(
                        imageView = binding.pokemonAvatarImageView,
                        pokemonModel = item
                    )
                )
            }
        }
    }
}

class PokemonViewHolder(
    private val binding: ItemPokemonBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: PokemonModel) {
        binding.apply {
            item.image?.loadInto(
                pokemonAvatarImageView,
                R.drawable.ic_pokemon_empty
            )
            pokemonNameTextView.text = item.name
        }
    }
}

data class PokemonItemEventData(
    val imageView: AppCompatImageView,
    val pokemonModel: PokemonModel
) : ViewEventContext