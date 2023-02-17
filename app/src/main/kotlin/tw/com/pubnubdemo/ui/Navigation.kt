package tw.com.pubnubdemo.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tw.com.pubnubdemo.ui.chat.ChatScreen
import tw.com.pubnubdemo.ui.home.HomeScreen
import tw.com.pubnubdemo.ui.login.LoginScreen

@Composable
fun Navigation(
    isLoggedIn: Boolean = false
) {

    val navController = rememberNavController()
    val initialDestination = if (isLoggedIn) Screen.HomeScreen.route else Screen.LoginScreen.route

    NavHost(navController = navController, startDestination = initialDestination) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.ChatScreen.withArgs("{userName}", "{channelId}")) {
            ChatScreen(
                navController,
                it.arguments?.getString("userName"),
                it.arguments?.getString("channelId")
            )
        }
    }
}