package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.adorastudios.androidbluetoothmessenger.R
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData

@Composable
fun DeviceTile(
    modifier: Modifier = Modifier,
    deviceData: BluetoothDeviceData,
) {
    Box(
        modifier = modifier,
    ) {
        Text(
            text = deviceData.name ?: stringResource(id = R.string.unnamedDevice),
            maxLines = 1,
        )
    }
}
