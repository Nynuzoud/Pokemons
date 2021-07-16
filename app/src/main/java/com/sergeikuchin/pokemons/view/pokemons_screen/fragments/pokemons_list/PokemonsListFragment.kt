package com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sergeikuchin.pokemons.R
import com.sergeikuchin.pokemons.databinding.FragmentPokemonsListBinding
import com.sergeikuchin.pokemons.domain.ResponseError
import com.sergeikuchin.pokemons.domain.models.PokemonModel
import com.sergeikuchin.pokemons.view.pokemons_screen.PokemonsViewModel
import com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list.adapter.PokemonItemEventData
import com.sergeikuchin.pokemons.view.pokemons_screen.fragments.pokemons_list.adapter.PokemonsListAdapter
import com.sergeikuchin.pokemons.view.utils.ViewEventContext
import com.sergeikuchin.pokemons.view.utils.ViewEventDelegate
import com.sergeikuchin.pokemons.view.utils.recycler_view.DefaultVerticalItemDecoration
import com.sergeikuchin.pokemons.view.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonsListFragment : Fragment(), ViewEventDelegate {

    private val adapter: PokemonsListAdapter by lazy {
        PokemonsListAdapter(this)
    }

    private val viewModel: PokemonsListViewModel by viewModel()
    private val sharedViewModel: PokemonsViewModel by activityViewModels()

    var onOpenPokemonScreen: ((sharedImageView: AppCompatImageView) -> Unit)? = null

    private var binding: FragmentPokemonsListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPokemonsListBinding.inflate(inflater, container, false).let {
        binding = it
        binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupSubscriptions()
    }

    private fun setupSwipeRefreshLayout() {
        binding?.pokemonsSwipeRefreshLayout?.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupRecyclerView() {
        binding?.pokemonsRecyclerView?.apply {
            adapter = this@PokemonsListFragment.adapter.apply {
                if (!hasStableIds()) {
                    setHasStableIds(true)
                }
            }
            addItemDecoration(
                DefaultVerticalItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.pokemon_item_margin)
                )
            )
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    startPostponedEnterTransition()
                    return true
                }
            })
        }
    }

    private fun setupSubscriptions() {
        viewModel.subscribeOnPokemons()
            .observe(viewLifecycleOwner, {
                updateRecyclerView(it)
            })

        viewModel.subscribeOnErrors()
            .observe(viewLifecycleOwner, {
                showErrorMessage(it.getContentIfNotHandled())
            })
        viewModel.subscribeOnSwipeToRefreshState()
            .observe(viewLifecycleOwner, {
                updateSwipeRefreshProgress(it)
            })
    }

    private fun showErrorMessage(error: ResponseError?) {
        error ?: return
        when (error) {
            is ResponseError.TimeoutException -> showTimeoutErrorMessage()
            is ResponseError.HttpNotFoundError -> showServerErrorMessage()
            is ResponseError.HttpServerError -> showServerErrorMessage()
            is ResponseError.UnknownError -> showUnknownErrorMessage()
        }
    }

    private fun showServerErrorMessage() {
        showToastMessage(R.string.server_error)
    }

    private fun showUnknownErrorMessage() {
        showToastMessage(R.string.unknown_error)
    }

    private fun showTimeoutErrorMessage() {
        showToastMessage(R.string.timeout_error)
    }

    private fun showToastMessage(@StringRes messageRes: Int) {
        context?.let { it.showToast(it.getString(messageRes)) }
    }


    private fun updateSwipeRefreshProgress(isRefreshing: Boolean) {
        binding?.pokemonsSwipeRefreshLayout?.isRefreshing = isRefreshing
    }

    private fun updateRecyclerView(pokemons: List<PokemonModel>) {
        adapter.items = pokemons
    }

    override fun onViewEvent(eventContext: ViewEventContext) {
        when (eventContext) {
            is PokemonItemEventData -> openPokemonScreen(eventContext)
        }
    }

    private fun openPokemonScreen(eventContext: PokemonItemEventData) {
        with(eventContext.pokemonModel) {
            eventContext.imageView.transitionName = image?.url
            sharedViewModel.setTransitionName(image?.url ?: "")
            sharedViewModel.setPokemonModel(this)
        }
        onOpenPokemonScreen?.invoke(eventContext.imageView)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}