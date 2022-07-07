package com.example.sberqrscanner.presentation

import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val getProfileStorage = MyApp.instance!!.getProfileStorage
    private val validateProfile = MyApp.instance!!.validateProfile

    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )

        setContentView(R.layout.activity_main)
        val content: FragmentContainerView = findViewById(R.id.fragmentContainerView)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val profileJob = checkProfile()
                    return if (isFinished) {
                        // The content is ready; start drawing.
                        profileJob.cancel()
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            })
    }

    private fun checkProfile(): Job {
        return lifecycleScope.launch {
                getProfileStorage().collect { reaction ->
                    when (reaction) {
                        is Reaction.Error -> {
                            isFinished = true
                        }
                        is Reaction.Success -> {
                            when (validateProfile(reaction.data)) {
                                is Reaction.Error -> {
                                    isFinished = true
                                }
                                is Reaction.Success -> {
                                    if (!isFinished){
                                        findNavController(R.id.fragmentContainerView)
                                            .navigate(R.id.action_global_scannerFragment)
                                        isFinished = true
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val view: FragmentContainerView = findViewById(R.id.fragmentContainerView)
        savedInstanceState.putBundle("nav_state", view.findNavController()
            .saveState()
        )
        super.onSaveInstanceState(savedInstanceState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val view: FragmentContainerView = findViewById(R.id.fragmentContainerView)
        view.findNavController()
            .restoreState(savedInstanceState.getBundle("nav_state"))
        super.onRestoreInstanceState(savedInstanceState)
    }

}