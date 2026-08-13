package com.example.milk_homecalci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.milk_homecalci.ui.navigation.MilkNavGraph
import com.example.milk_homecalci.ui.theme.MilkHomeCalciTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MilkHomeCalciTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MilkNavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
