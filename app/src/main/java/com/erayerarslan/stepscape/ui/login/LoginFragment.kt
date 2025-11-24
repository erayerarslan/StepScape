package com.erayerarslan.stepscape.ui.login

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.erayerarslan.stepscape.R
import com.erayerarslan.stepscape.core.Response
import com.erayerarslan.stepscape.core.Response.*
import com.erayerarslan.stepscape.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class LoginFragment() : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    Log.d("SignInFragment", "ID Token received: ${idToken != null}")
                    if (idToken != null) {
                        Log.d("SignInFragment", "Calling viewModel.loginWithGoogle")
                        viewModel.loginWithGoogle(idToken)
                    } else {
                        Log.e("SignInFragment", "ID Token is null")
                        Toast.makeText(requireContext(), "Google girişi başarısız: ID Token alınamadı", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SignInFragment", "Unexpected error in Google Sign-In", e)
                }
            } else {
                if (result.data != null) {
                    try {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        val account = task.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        Log.e("SignInFragment", "Error in cancelled result: ${e.statusCode}", e)
                    }
                } else {
                    Toast.makeText(requireContext(), "Google girişi iptal edildi veya yapılandırma hatası olabilir.", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { response ->
                    Log.d("SignInFragment", "State received: ${response::class.simpleName}")
                    when (response) {
                        is Loading -> {
                            Log.d("SignInFragment", "Loading state")
                        }

                        is Success<*> -> {
                            Log.d("SignInFragment", "Success state - navigating to home")
                            try {
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            } catch (e: Exception) {
                                Log.e("SignInFragment", "Navigation error", e)
                            }
                        }

                        is Error -> {
                            Log.d("SignInFragment", "Error state: ${response.message}")
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                        }

                        is Init -> {
                            Log.d("SignInFragment", "Init state")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}