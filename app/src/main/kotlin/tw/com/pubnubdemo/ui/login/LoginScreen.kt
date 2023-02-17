package tw.com.pubnubdemo.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import tw.com.pubnubdemo.ui.Screen
import tw.com.pubnubdemo.ui.theme.PubNubDemoTheme

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        var userName by remember { mutableStateOf(TextFieldValue("")) }
        val userId by viewModel.userId.collectAsState(initial = null)

        val localFocusManager = LocalFocusManager.current

        LaunchedEffect(userId) {
            userId?.let { navController.navigate(Screen.HomeScreen.route) }
        }

        TextField(
            value = userName,
            onValueChange = { userName = it },
            singleLine = true,
            placeholder = { Text(text = "Your Name here") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { localFocusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier
                .size(0.dp, 5.dp)
                .fillMaxWidth()
        )
        Button(
            enabled = userName.text.isNotBlank(),
            onClick = { viewModel.login(userName.text) }
        )
        {
            Text(text = "Enter Next Page")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    PubNubDemoTheme {
        LoginScreen(navController = rememberNavController())
    }
}