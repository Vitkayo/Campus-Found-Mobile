package com.example.lostfound.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.lostfound.R
import com.example.lostfound.databinding.ActivityLoginBinding
import com.example.lostfound.databinding.DialogForgotPasswordBinding
import com.example.lostfound.databinding.DialogRegisterBinding
import com.example.lostfound.service.SessionManager
import com.example.lostfound.ui.main.MainActivity
import com.example.lostfound.util.SystemBars
import com.example.lostfound.util.ThemeToggleBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: LoginViewModel by viewModels()
    private var registerDialog: AlertDialog? = null
    private var forgotPasswordDialog: AlertDialog? = null
    private var forgotPasswordBinding: DialogForgotPasswordBinding? = null
    private var pendingRegisterEmail: String? = null
    private var pendingRegisterPassword: String? = null
    private var pendingResetEmail: String? = null
    private var pendingResetPassword: String? = null

    companion object {
        const val EXTRA_SHOW_LOGIN = "com.example.lostfound.SHOW_LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isLoggedIn() || !intent.getBooleanExtra(EXTRA_SHOW_LOGIN, false)) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SystemBars.apply(activity = this, root = binding.root)

        ThemeToggleBinding.bind(binding.darkModeButton, this)

        binding.signInTab.setOnClickListener { binding.emailInput.requestFocus() }
        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.registerTab.setOnClickListener { showRegisterDialog() }
        binding.browseGuestButton.setOnClickListener { navigateToMain() }
        binding.forgotPasswordText.setOnClickListener { showForgotPasswordDialog() }

        observeViewModel()
        binding.root.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            ThemeToggleBinding.refreshIcon(binding.darkModeButton, this)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.loginButton.isEnabled = !state.isLoading

                    if (state.loginSucceeded) {
                        viewModel.consumeLoginSuccess()
                        navigateToMain()
                        return@collect
                    }

                    binding.emailLayout.error = null
                    binding.passwordLayout.error = null

                    when (state.error) {
                        "identifier_required" ->
                            binding.emailLayout.error = getString(R.string.login_identifier_required)
                        "invalid_identifier" ->
                            binding.emailLayout.error = getString(R.string.invalid_login_identifier)
                        "password_too_short" -> {
                            if (forgotPasswordDialog?.isShowing == true) {
                                showForgotPasswordFieldError(
                                    passwordError = getString(R.string.password_min_length)
                                )
                            } else {
                                binding.passwordLayout.error = getString(R.string.password_min_length)
                            }
                        }
                        "forgot_invalid_email" ->
                            showForgotPasswordFieldError(
                                emailError = getString(R.string.invalid_login_identifier)
                            )
                        "forgot_password_mismatch" ->
                            showForgotPasswordFieldError(
                                confirmError = getString(R.string.edit_profile_password_mismatch)
                            )
                        "register_validation" ->
                            showRegisterError(getString(R.string.register_validation_error))
                        null -> Unit
                        else -> {
                            val message = when (state.error) {
                                "network_error" -> getString(R.string.error_loading)
                                else -> state.error
                            }
                            if (forgotPasswordDialog?.isShowing == true) {
                                showForgotPasswordError(message)
                            } else if (registerDialog?.isShowing == true) {
                                showRegisterError(message)
                            } else {
                                binding.errorText.text = message
                                binding.errorText.setTextColor(getColor(R.color.error))
                                binding.errorText.visibility = View.VISIBLE
                            }
                        }
                    }

                    if (state.successMessage == "registration_success") {
                        registerDialog?.dismiss()
                        registerDialog = null
                        pendingRegisterEmail?.let { binding.emailInput.setText(it) }
                        pendingRegisterPassword?.let { binding.passwordInput.setText(it) }
                        pendingRegisterEmail = null
                        pendingRegisterPassword = null
                        binding.errorText.text = getString(R.string.registration_success)
                        binding.errorText.setTextColor(getColor(R.color.secondary))
                        binding.errorText.visibility = View.VISIBLE
                        viewModel.clearMessages()
                    }

                    if (state.successMessage == "password_reset_success") {
                        forgotPasswordDialog?.dismiss()
                        forgotPasswordDialog = null
                        pendingResetEmail?.let { binding.emailInput.setText(it) }
                        pendingResetPassword?.let { binding.passwordInput.setText(it) }
                        pendingResetEmail = null
                        pendingResetPassword = null
                        binding.errorText.text = getString(R.string.forgot_password_success)
                        binding.errorText.setTextColor(getColor(R.color.secondary))
                        binding.errorText.visibility = View.VISIBLE
                        viewModel.clearMessages()
                    }
                }
            }
        }
    }

    private fun attemptLogin() {
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.errorText.visibility = View.GONE

        viewModel.login(
            identifier = binding.emailInput.text?.toString()?.trim().orEmpty(),
            password = binding.passwordInput.text?.toString().orEmpty(),
            rememberMe = binding.rememberCheckBox.isChecked
        )
    }

    private fun showRegisterError(message: String) {
        if (registerDialog?.isShowing == true) {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        } else {
            binding.errorText.text = message
            binding.errorText.setTextColor(getColor(R.color.error))
            binding.errorText.visibility = View.VISIBLE
        }
        viewModel.clearMessages()
    }

    private fun showForgotPasswordError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        viewModel.clearMessages()
    }

    private fun showForgotPasswordFieldError(
        emailError: String? = null,
        passwordError: String? = null,
        confirmError: String? = null
    ) {
        forgotPasswordBinding?.forgotEmailLayout?.error = emailError
        forgotPasswordBinding?.forgotPasswordLayout?.error = passwordError
        forgotPasswordBinding?.forgotConfirmPasswordLayout?.error = confirmError
        viewModel.clearMessages()
    }

    private fun showRegisterDialog() {
        val dialogBinding = DialogRegisterBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.register, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            dialogBinding.closeRegisterButton.setOnClickListener { dialog.dismiss() }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val username = dialogBinding.registerUsernameInput.text?.toString()?.trim().orEmpty()
                val email = dialogBinding.registerEmailInput.text?.toString()?.trim().orEmpty()
                val phone = dialogBinding.registerPhoneInput.text?.toString()?.trim().orEmpty()
                val password = dialogBinding.registerPasswordInput.text?.toString().orEmpty()

                pendingRegisterEmail = email
                pendingRegisterPassword = password
                viewModel.register(username, email, phone, password)
            }
        }
        dialog.setOnDismissListener {
            if (registerDialog === dialog) registerDialog = null
        }
        registerDialog = dialog
        dialog.show()
    }

    private fun showForgotPasswordDialog() {
        val dialogBinding = DialogForgotPasswordBinding.inflate(layoutInflater)
        forgotPasswordBinding = dialogBinding
        binding.emailInput.text?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let {
            dialogBinding.forgotEmailInput.setText(it)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.forgot_password_reset, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            dialogBinding.closeForgotPasswordButton.setOnClickListener { dialog.dismiss() }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogBinding.forgotEmailLayout.error = null
                dialogBinding.forgotPasswordLayout.error = null
                dialogBinding.forgotConfirmPasswordLayout.error = null

                val email = dialogBinding.forgotEmailInput.text?.toString()?.trim().orEmpty()
                val newPassword = dialogBinding.forgotPasswordInput.text?.toString().orEmpty()
                val confirmPassword = dialogBinding.forgotConfirmPasswordInput.text?.toString().orEmpty()

                pendingResetEmail = email
                pendingResetPassword = newPassword
                viewModel.resetPassword(email, newPassword, confirmPassword)
            }
        }
        dialog.setOnDismissListener {
            if (forgotPasswordDialog === dialog) {
                forgotPasswordDialog = null
                forgotPasswordBinding = null
            }
        }
        forgotPasswordDialog = dialog
        dialog.show()
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }
}
