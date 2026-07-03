package com.cloutgrid.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cloutgrid.androidapp.ui.shared.AppCoordinator
import com.cloutgrid.androidapp.ui.theme.CloutgridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = 0,
                darkScrim = 0
            )
        )

        setContent {
            CloutgridTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AppCoordinator()
                }
            }
        }
    }
}