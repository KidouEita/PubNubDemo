package tw.com.pubnubdemo.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import tw.com.pubnubdemo.ui.theme.Purple700

@Composable
fun ChatScreen(
    navController: NavController,
    userName: String?,
    channelId: String?,
    chatViewModel: ChatViewModel = hiltViewModel(),

    ) {
    val msgList by chatViewModel.msgList.collectAsState()

    LaunchedEffect(true) {
        chatViewModel.setUserId(userName)
        chatViewModel.setChannel(listOf(channelId ?: ""))
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        // Create references for the composables to constrain
        val (titleRef, listRef, inputRef) = createRefs()

        // When list update, scroll to the last message
        val listState = rememberLazyListState()
        LaunchedEffect(msgList) {
            if (msgList.isNotEmpty())
                listState.animateScrollToItem(msgList.size - 1)
        }

        Text(
            text = "ChannelId: $channelId",
            fontSize = 20.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .background(Purple700)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    height = Dimension.wrapContent
                    width = Dimension.matchParent
                }
        )
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .constrainAs(listRef) {
                    linkTo(
                        start = parent.start,
                        top = titleRef.bottom,
                        end = parent.end,
                        bottom = inputRef.top
                    )
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            content = {
                items(msgList) { msg ->
                    Text(text = msg)
                }
            }
        )
        InputPanel(
            modifier = Modifier
                .constrainAs(inputRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.wrapContent
                    width = Dimension.fillToConstraints
                }
        ) { message ->
            chatViewModel.publish(channelId ?: "", message)
        }
    }

}

@Composable
fun InputPanel(modifier: Modifier, onClickButton: (String) -> Unit) {
    ConstraintLayout(
        modifier = modifier
    ) {

        val (editRef, buttonRef) = createRefs()

        var inputText by remember { mutableStateOf(TextFieldValue("")) }

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (inputText.text.isNotBlank()) {
                        onClickButton(inputText.text)
                        inputText = TextFieldValue("")
                    }
                }
            ),
            modifier = Modifier
                .constrainAs(editRef) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = buttonRef.start,
                        bottom = parent.bottom
                    )
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
        )
        Button(
            enabled = inputText.text.isNotBlank(),
            onClick = {
                onClickButton.invoke(inputText.text)
                inputText = TextFieldValue("")
            },
            modifier = Modifier
                .constrainAs(buttonRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    height = Dimension.wrapContent
                    width = Dimension.wrapContent
                }
        ) {
            Text("Send")
        }
    }
}