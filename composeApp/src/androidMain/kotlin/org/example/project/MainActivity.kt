package org.example.project

import App
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ui.page.PageEvent
import ui.page.PageViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: PageViewModel = viewModel()

            App(
                pageViewModel = viewModel
            )

            LaunchedEffect(Unit) {
                lifecycle.repeatOnLifecycle(
                    state = Lifecycle.State.STARTED
                ) {
                    viewModel.eventFlow.collect { event ->
                        handleEvent(event = event)
                    }
                }
            }
        }
    }

    private fun handleEvent(event: PageEvent) {
        when (event) {
            is PageEvent.TriggerUrl -> {
                triggerUrl(url = event.url)
            }
        }
    }

    private fun triggerUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}