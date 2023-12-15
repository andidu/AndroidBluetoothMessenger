package com.adorastudios.androidbluetoothmessenger.data

import android.bluetooth.BluetoothSocket
import com.adorastudios.androidbluetoothmessenger.domain.ConnectionResult
import com.adorastudios.androidbluetoothmessenger.domain.Message
import com.adorastudios.androidbluetoothmessenger.domain.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class BluetoothMessageTransferService(
    private val socket: BluetoothSocket,
    private val senderName: String?,
) {
    fun listenForIncomingMessages(): Flow<ConnectionResult> {
        return flow {
            if (!socket.isConnected) return@flow
            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: Exception) {
                    emit(ConnectionResult.Error("Error"))
                    return@flow
                }

                emit(
                    ConnectionResult.Success(
                        Message(
                            data = Json.decodeFromString(buffer.decodeToString(endIndex = byteCount)),
                            sender = senderName,
                            isLocal = false,
                        ),
                    ),
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(data: MessageData): Boolean = withContext(Dispatchers.IO) {
        try {
            socket.outputStream.write(Json.encodeToString(data).toByteArray())
            true
        } catch (e: Exception) {
            false
        }
    }
}
