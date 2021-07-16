package com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.TransitionInflater
import com.sergeikuchin.pokemons.R
import com.sergeikuchin.pokemons.databinding.FragmentPokemonBinding
import com.sergeikuchin.pokemons.domain.models.PokemonModel
import com.sergeikuchin.pokemons.view.pokemons_screen.PokemonsViewModel

class PokemonFragment : Fragment() {

    private val sharedViewModel: PokemonsViewModel by activityViewModels()

    private var binding: FragmentPokemonBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPokemonBinding.inflate(inflater, container, false).let {
        binding = it
        binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSubscriptions()
    }

    private fun setupSubscriptions() {
        sharedViewModel.transitionName.observe(viewLifecycleOwner, {
            binding?.pokemonAvatarImageView?.transitionName = it
        })
        sharedViewModel.pokemonModel.observe(viewLifecycleOwner, { model ->
            showImageAndText(model)
        })
    }

    private fun showImageAndText(model: PokemonModel) {
        binding?.pokemonAvatarImageView?.let {
            model.image?.loadInto(
                imageView = it,
                placeHolder = R.drawable.ic_pokemon_empty,
                originalSize = true,
                loadFromCacheOnly = true,
                finally = {
                    binding?.pokemonNameTextView?.text = model.name
                    startPostponedEnterTransition()
                }
            )
        }
    }

}