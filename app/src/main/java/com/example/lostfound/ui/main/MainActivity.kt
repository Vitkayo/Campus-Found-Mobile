package com.example.lostfound.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.example.lostfound.R
import com.example.lostfound.databinding.ActivityMainBinding
import com.example.lostfound.service.SessionManager
import com.example.lostfound.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            binding.root.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
            )
            binding.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            binding.bottomNavigation.visibility =
                if (imeVisible) View.GONE else View.VISIBLE

            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(com.example.lostfound.R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (!sessionManager.isLoggedIn() && item.itemId != R.id.homeFragment) {
                showLoginRequiredDialog()
                return@setOnItemSelectedListener false
            }
            item.onNavDestinationSelected(navController)
        }
    }

    private fun showLoginRequiredDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.login_required_title)
            .setMessage(R.string.login_required_message)
            .setPositiveButton(R.string.login) { _, _ -> openLogin() }
            .setNegativeButton(R.string.continue_browsing, null)
            .show()
    }

    private fun openLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).putExtra(LoginActivity.EXTRA_SHOW_LOGIN, true)
        )
    }
}
