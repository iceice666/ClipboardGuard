package me.iceice666.clipboardguard.app.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.iceice666.clipboardguard.app.R
import me.iceice666.clipboardguard.common.datakind.MessagePacket
import java.text.SimpleDateFormat
import java.util.Locale


@Serializable
object LoggingComponent : IDestinationComponent {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Show(modifier: Modifier) {
        var logs: List<MessagePacket> by rememberSaveable {
            mutableStateOf(mutableListOf())
        }

        var isRefreshing by remember { mutableStateOf(false) }
        val state = rememberPullToRefreshState()
        val coroutineScope = rememberCoroutineScope()
        val onRefresh: () -> Unit = {
            isRefreshing = true
            coroutineScope.launch {
                TODO("Refresh logs")


                isRefreshing = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Logs") },
                    // Provide an accessible alternative to trigger refresh.
                    actions = {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Filled.Refresh, "Trigger Refresh")
                        }

                    }
                )
            }
        ) {
            PullToRefreshBox(
                modifier = modifier.padding(it),
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = state,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }

            ) {
                LogContainer(logs = logs, modifier = modifier)
            }
        }


    }

    override val label: String = "logging"
    override val icon: ImageVector = Icons.AutoMirrored.Filled.Assignment
}


@Composable
fun LogContainer(
    logs: List<MessagePacket>,
    modifier: Modifier = Modifier,
) {
    if (logs.isEmpty()) NothingToShow()
    else {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = logs) { msg ->
                LogCard(msg)
            }
        }
    }


}

@Composable
fun LogCard(msg: MessagePacket, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        LogContent(msg)
    }
}

@Composable
fun LogContent(msg: MessagePacket, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {

        var expanded by rememberSaveable { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                val time = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(msg.time.time)


                Text(text = msg.level.toString())
                Text(text = time)
                Text(text = msg.identifier)
                Text(text = msg.message)
                if (expanded) {
                    val c = msg.cause?.let {
                        (it.stackTraceToString()
                            .apply { if (endsWith("\n")) substring(0, length - 1) })
                            .run { if (isNotBlank()) "\nCaused by:\n$this" else "" }
                    } ?: ""

                    Text(text = c)
                }
            }

            if (msg.cause != null) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) {
                            Icons.Filled.ExpandLess
                        } else {
                            Icons.Filled.ExpandMore
                        },
                        contentDescription = if (expanded) {
                            stringResource(R.string.text_show_less)
                        } else {
                            stringResource(R.string.text_show_more)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NothingToShow() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Woo! Such empty.")
    }
}


@Preview
@Composable
fun LogPreview() {

    LogContainer(
        logs = listOf(
            MessagePacket("test", MessagePacket.Level.DEBUG, "test"),
            MessagePacket("test", MessagePacket.Level.INFO, "test"),
            MessagePacket("test", MessagePacket.Level.WARN, "test"),
            MessagePacket("test", MessagePacket.Level.ERROR, "test"),
            MessagePacket("test", MessagePacket.Level.FATAL, "test")
        )
    )

}

