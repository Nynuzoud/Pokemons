package com.sergeikuchin.pokemons.view.pokemons_screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.sergeikuchin.pokemons.R
import com.sergeikuchin.pokemons.databinding.ActivityPokemonsBinding
import com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemon.PokemonFragment
import com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list.PokemonsListFragment

class PokemonsActivity : AppCompatActivity() {

    private var binding: ActivityPokemonsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initPokemonsFragment(savedInstanceState)

    }

    private fun initPokemonsFragment(savedInstanceState: Bundle?) {
        val onOpenPokemonScreen: (sharedImageView: AppCompatImageView) -> Unit =
            { sharedImageView ->
                addPokemonFragment(sharedImageView)
            }

        when (savedInstanceState == null) {
            true -> createPokemonsListFragment(onOpenPokemonScreen)
            false -> restorePokemonsListFragment(onOpenPokemonScreen)
        }
    }

    private fun createPokemonsListFragment(openPokemonScreen: (sharedImageView: AppCompatImageView) -> Unit) {
        val fragment = PokemonsListFragment().apply {
            onOpenPokemonScreen = openPokemonScreen
        }

        supportFragmentManager.commit {
            add(R.id.fragmentContainerView, fragment, PokemonsListFragment::class.java.simpleName)
        }
    }

    private fun restorePokemonsListFragment(openPokemonScreen: (sharedImageView: AppCompatImageView) -> Unit) {
        supportFragmentManager.fragments.find { it is PokemonsListFragment }?.let {
            (it as PokemonsListFragment).onOpenPokemonScreen = openPokemonScreen
        }
    }

    private fun addPokemonFragment(sharedImageView: AppCompatImageView) {
        val fragment = PokemonFragment()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentContainerView, fragment, PokemonFragment::class.java.simpleName)
            addSharedElement(sharedImageView, sharedImageView.transitionName)
            addToBackStack(PokemonFragment::class.java.simpleName)
            hide(getCurrentFragment() ?: throw IllegalStateException("Current fragment is null!"))
        }
    }

    private fun getCurrentFragment(): Fragment? =
        supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
}
