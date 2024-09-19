package com.geekstudio.sentrytest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.geekstudio.sentrytest.ui.theme.SentryTestTheme
import io.sentry.Sentry
import io.sentry.compose.withSentryObservableEffect
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController().withSentryObservableEffect(
                enableNavigationBreadcrumbs = true, // enabled by default
                enableNavigationTracing = true  // enabled by default
            )
            SentryTestTheme {
                SampleNavigation(navController)
            }
        }
    }
}

@Composable
fun SampleNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "first"
    ) {
        composable("first") {
            Column {
                Text("first")
                ErrorButton {
                    executeException()
                    navController.navigate("second")
                }
            }
        }
        composable("second") {
            Column {
                Text("second")
                ErrorButton {
                    executeException()
                    navController.navigate("first")
                }
            }
        }
    }
}

private fun executeException() {
    val randomSeed = Random.nextInt(0, 5)
    val msg = "recordException $randomSeed"
    val exception = when (randomSeed) {
        0 -> NullPointerException(msg)
        1 -> ArrayIndexOutOfBoundsException(msg)
        2 -> SecurityException(msg)
        3 -> RuntimeException(msg)
        else -> ClassNotFoundException(msg)
    }
    Sentry.captureException(exception)
//    throw exception
}

@Composable
fun ErrorButton(event: () -> Unit) {
    val context = LocalContext.current
    Button(
        modifier = Modifier,
        onClick = {
            Toast.makeText(context, "ErrorButton Action", Toast.LENGTH_SHORT).show()
/*            runCatching {
                throw RuntimeException("This app uses Sentry! :)")
            }.onFailure { e ->
                Sentry.captureException(e)
            }*/
            event.invoke()
        }) {
        Text(
            text = "Error 발생",
            modifier = Modifier
        )
    }
}
