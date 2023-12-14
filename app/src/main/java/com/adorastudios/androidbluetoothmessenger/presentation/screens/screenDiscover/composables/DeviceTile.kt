package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adorastudios.androidbluetoothmessenger.R
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData

@Composable
fun DeviceTile(
    modifier: Modifier = Modifier,
    deviceData: BluetoothDeviceData,
    connect: () -> Unit,
    accept: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = deviceData.name ?: stringResource(id = R.string.unnamedDevice),
            maxLines = 1,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            OutlinedButton(onClick = connect) {
                Text(
                    text = stringResource(id = R.string.connect),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            OutlinedButton(onClick = accept) {
                Text(
                    text = stringResource(id = R.string.accept),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
