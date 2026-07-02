package com.turkcell.rencar_pair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.turkcell.rencar_pair.navigation.RenCarNavHost
import com.turkcell.rencar_pair.ui.theme.RenCarPairTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RenCarPairTheme {
                RenCarNavHost()
            }
        }
    }
}
