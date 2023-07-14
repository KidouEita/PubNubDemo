package tw.com.pubnubdemo.ui.home

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import tw.com.pubnubdemo.R
import tw.com.pubnubdemo.ui.Screen
import tw.com.pubnubdemo.ui.theme.PubNubDemoTheme

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var shouldShowDialog by remember { mutableStateOf(false) }
    var shouldShowCameraDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf(TextFieldValue("")) }
    val channelList by viewModel.channelList.collectAsState()
    val username by viewModel.username.collectAsState()

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                viewModel.setDisplayFromGallery(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        })

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            if (isSuccess) {
                Log.d("PhotoPicker", "Selected URI: ${viewModel.uriFromCamera}")
                viewModel.setDisplayFromCamera()
            } else {
                Log.e("PhotoPicker", "Not success")
            }
        })

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = {
                items(channelList ?: listOf()) { channelName ->
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        onClick = {
                            shouldShowCameraDialog = true
//                            username?.let { usernameNonNull ->
//                                navController.navigate(
//                                    Screen.ChatScreen.withArgs(
//                                        usernameNonNull,
//                                        channelName
//                                    )
//                                ) {
//                                    launchSingleTop = true
//                                }
//                            }
                        }
                    ) {
                        Text(
                            text = channelName,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            })

        val uri by viewModel.uriState.collectAsState()
        Image(painter = rememberAsyncImagePainter(
            model = uri,
            onSuccess = {
                viewModel.deleteCameraTempFile()
            }
        ), contentDescription = "")

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(0.dp, 0.dp, 8.dp, 8.dp),
            onClick = { shouldShowDialog = true }
        ) {
            Icon(
                painter = painterResource(
                    id = R.drawable.ic_baseline_add_24
                ),
                contentDescription = "Add New Channel"
            )
        }

        if (shouldShowDialog)
            AlertDialog(
                title = { Text(text = "Create a new channel") },
                text = {
                    TextField(
                        value = dialogInput,
                        placeholder = { Text(text = "New Channel Name") },
                        onValueChange = { dialogInput = it })
                },
                onDismissRequest = { shouldShowDialog = false },
                confirmButton = {
                    Button(
                        enabled = dialogInput.text.isNotBlank(),
                        onClick = {
                            viewModel.addNewChannel(dialogInput.text)
                            dialogInput = TextFieldValue("")
                            shouldShowDialog = false
                        }
                    ) {
                        Text(text = "Create")
                    }
                })

        if (shouldShowCameraDialog)
            AlertDialog(onDismissRequest = { shouldShowCameraDialog = false }, buttons = {
                Button(
                    onClick = {
                        takePicture.launch(viewModel.uriFromCamera)
                        shouldShowCameraDialog = false
                    }
                ) {
                    Text(text = "Camera")
                }
                Button(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        shouldShowCameraDialog = false
                    }
                ) {
                    Text(text = "Gallery")
                }
            })
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    PubNubDemoTheme {
        HomeScreen(navController = rememberNavController())
    }
}