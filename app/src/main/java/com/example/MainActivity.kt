package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainScreenContainer
import com.example.ui.theme.ShonenStudyOSTheme
import com.example.ui.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    
    private val studyViewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Supports full edge-to-edge drawing
        enableEdgeToEdge()
        
        setContent {
            ShonenStudyOSTheme {
                // Surface base using our themed cockpit background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MainScreenContainer(viewModel = studyViewModel)
                }
            }
        }
    }
}
