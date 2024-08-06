package me.iceice666.clipboardguard.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import me.iceice666.clipboardguard.LsposedServiceManager
import me.iceice666.clipboardguard.R
import me.iceice666.clipboardguard.ui.theme.ClipboardGuardTheme

@Serializable
object LoggingComponent : IDestinationComponent {

    @Composable
    override fun Show(modifier: Modifier) {
        val service = LsposedServiceManager.service
        val logs = mapOf("Oops!" to "It seems like the Lsposed service is down.")


        if (logs.isEmpty()) NothingToShow()
        else LogContainer(logs = logs, modifier = modifier)
    }

    override val label: String = "logging"
    override val icon: ImageVector = Icons.AutoMirrored.Filled.Assignment
  //  override val route: String = this::class.java.name
}


@Composable
fun LogContainer(
    logs: Map<String, Any?>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = logs.toList().sortedByDescending { it.first }.reversed()) { (time, log) ->
            LogCard(time = time, log = log.toString())
        }
    }
}

@Composable
fun LogCard(time: String, log: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        LogContent(time = time, log = log)
    }
}

@Composable
fun LogContent(time: String, log: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        val (level, packageName, message, cause) = log.split("$$", limit = 4)

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
                Text(text = level)
                Text(text = time.replace("_", " "))
                Text(text = packageName)
                Text(text = message)
                if (expanded) {
                    Text(text = cause)
                }

            }

            if (cause.isNotBlank()) {
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

@Preview(
    showBackground = true,
    //uiMode = UI_MODE_NIGHT_YES,
)
@Composable
fun Preview() {
    ClipboardGuardTheme {
        LogContainer(
            modifier = Modifier.fillMaxSize(),
            logs = mapOf(
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.100" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.200" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.300" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.400" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.500" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.600" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.700" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.800" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.900" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:82.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:83.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:84.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1970-01-01_00:00:00.000" to
                        "Debug$\$com.discord$$(Read+Text) Empty rule sets. Ignored.$$" +
                        "Caused by:\n" + // I just search and paste this form a random github issue
                        "java.lang.NoSuchFieldError: currentScreen\n" +
                        "\tat net.minecraft.class_425.handler\$zmk000\$rrls\$init(class_425.java:558)\n" +
                        "\tat net.minecraft.class_425.<init>(class_425.java:75)\n" +
                        "\tat net.minecraft.class_310.<init>(class_310.java:704)\n" +
                        "\tat net.minecraft.client.main.Main.main(Main.java:223)\n" +
                        "\tat net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider.launch(MinecraftGameProvider.java:470)\n" +
                        "\tat net.fabricmc.loader.impl.launch.knot.Knot.launch(Knot.java:74)\n" +
                        "\tat net.fabricmc.loader.impl.launch.knot.KnotClient.main(KnotClient.java:23)",
                "1125-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1165-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1445-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1245-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_15:19:81.090" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-07_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_19:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
                "1145-01-04_18:19:81.000" to "Info$\$com.homo$\$yiyiyokoyiyo$$",
            )
        )
    }
}