package com.kienct.chess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kienct.chess.ui.app.Game
import com.kienct.chess.ui.theme.KienCTsChessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KienCTsChessTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Game()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KienCTsChessTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Game()
        }
    }
}