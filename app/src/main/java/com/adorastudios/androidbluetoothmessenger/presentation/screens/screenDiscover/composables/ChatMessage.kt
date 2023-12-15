package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adorastudios.androidbluetoothmessenger.R
import com.adorastudios.androidbluetoothmessenger.domain.Message

@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: Message,
) {
    Column(
        modifier = modifier
            .clip(
                shape = if (message.isLocal) {
                    MaterialTheme.shapes.medium.copy(
                        bottomEnd = MaterialTheme.shapes.extraSmall.bottomEnd,
                    )
                } else {
                    MaterialTheme.shapes.medium.copy(
                        bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
                    )
                },
            )
            .background(
                color = if (message.isLocal) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = message.sender ?: stringResource(id = R.string.unnamedDevice),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = message.data.text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
