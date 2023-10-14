package more.tech.app.feature_auth.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import more.tech.app.R
import more.tech.app.core.presentation.util.UiEvent
import more.tech.app.core.presentation.util.asString
import more.tech.app.core.presentation.util.ex.afterTextChanged
import more.tech.app.core.presentation.util.ex.onBackPressedDispatcher
import more.tech.app.core.presentation.util.ex.toast
import more.tech.app.databinding.FragmentLoginBinding
import more.tech.app.feature_auth.presentation.util.AuthError

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher(this)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        initUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun initUI() {
        binding.inputLogin.setText(viewModel.login)
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.inputLogin.afterTextChanged {
            viewModel.onEvent(LoginEvent.EnteredLogin(it))
        }
        binding.inputPassword.afterTextChanged {
            viewModel.onEvent(LoginEvent.EnteredPassword(it))
        }
        binding.btnNext.setOnClickListener {
            viewModel.onEvent(LoginEvent.Login)
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> toast(event.uiText.asString(requireContext()))
                    is UiEvent.NavigationResource -> findNavController().navigate(event.id)
                    else -> {}
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collectLatest { viewState ->
                when (viewState.error) {
                    is AuthError.FieldEmpty -> getString(R.string.error_field_empty)
                    else -> null
                }?.let {
                    binding.inputLogin.error = it
                    binding.inputLogin.requestFocus()
                }
                viewState.error = null
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.passwordState.collectLatest { viewState ->
                when (viewState.error) {
                    is AuthError.FieldEmpty -> getString(R.string.error_field_empty)
                    else -> null
                }?.let {
                    binding.inputPassword.error = it
                    binding.inputPassword.requestFocus()
                }
                viewState.error = null
            }
        }

    }

}