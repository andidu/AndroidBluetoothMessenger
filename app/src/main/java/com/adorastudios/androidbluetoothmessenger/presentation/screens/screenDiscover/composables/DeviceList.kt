@file:OptIn(ExperimentalFoundationApi::class)

package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adorastudios.androidbluetoothmessenger.R
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData

@Composable
fun DeviceList(
    modifier: Modifier = Modifier,
    pairedDevices: List<BluetoothDeviceData>,
    scannedDevices: List<BluetoothDeviceData>,
    connect: (BluetoothDeviceData) -> Unit,
    accept: (BluetoothDeviceData) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp),
    ) {
        stickyHeader {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 8.dp),
                text = stringResource(id = R.string.pairedDevices),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
        items(
            items = pairedDevices,
        ) {
            DeviceTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                deviceData = it,
                connect = {
                    connect(it)
                },
                accept = {
                    accept(it)
                },
            )
        }
        stickyHeader {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 8.dp),
                text = stringResource(id = R.string.scannedDevices),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
        items(
            items = scannedDevices,
        ) {
            DeviceTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                deviceData = it,
                connect = {
                    connect(it)
                },
                accept = {
                    accept(it)
                },
            )
        }
    }
}
