package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adorastudios.androidbluetoothmessenger.R
import com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover.composables.DeviceList

@Composable
fun DiscoverScreen(
    navController: NavController,
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.isConnecting) {
            CircularProgressIndicator()
            Text(text = stringResource(id = R.string.connecting))
            Text(
                text = if (state.server) {
                    stringResource(id = R.string.waitingForClient)
                } else {
                    stringResource(id = R.string.waitingForServer)
                },
            )
            Button(onClick = { viewModel.onEvent(DiscoverEvent.StopConnection) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        } else {
            DeviceList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(MaterialTheme.shapes.medium),
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                connect = {
                    viewModel.onEvent(DiscoverEvent.ConnectToClient(it))
                },
                accept = {
                    viewModel.onEvent(DiscoverEvent.ConnectToServer(it))
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                Button(onClick = { viewModel.onEvent(DiscoverEvent.StartScan) }) {
                    Text(text = stringResource(id = R.string.startScan))
                }
                Button(onClick = { viewModel.onEvent(DiscoverEvent.StopScan) }) {
                    Text(text = stringResource(id = R.string.stopScan))
                }
            }
        }
    }
}
