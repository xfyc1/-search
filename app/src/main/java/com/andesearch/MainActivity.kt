package com.andesearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.andesearch.ui.navigation.AppNavigation
import com.andesearch.ui.theme.AndeSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndeSearchTheme {
                AppNavigation()
            }
        }
    }
}
