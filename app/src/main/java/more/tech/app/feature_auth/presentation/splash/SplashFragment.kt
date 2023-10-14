package more.tech.app.feature_auth.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import more.tech.app.core.presentation.util.UiEvent
import more.tech.app.core.presentation.util.asString
import more.tech.app.core.presentation.util.ex.toast
import more.tech.app.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        findNavController().popBackStack()
        initUI()
        return binding.root
    }

    private fun initUI() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is UiEvent.ShowToast -> toast(event.uiText.asString(requireContext()))
                    is UiEvent.NavigationResource -> findNavController().navigate(event.id)
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.viewState.collect { viewState ->
                binding.progressBar.isVisible = viewState.isLoading
            }
        }
    }

}