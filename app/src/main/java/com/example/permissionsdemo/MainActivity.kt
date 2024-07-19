package com.example.permissionsdemo

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.permissionsdemo.ui.theme.PermissionsDemoTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionsDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Preview
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainContent() {
    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS
    )

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val readContactsPermissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

    val cameraPermissionText = rememberPermissionText(multiplePermissionsState, Manifest.permission.CAMERA)
    val locationPermissionText = rememberPermissionText(multiplePermissionsState, Manifest.permission.ACCESS_FINE_LOCATION)
    val readContactsPermissionText = rememberPermissionText(multiplePermissionsState, Manifest.permission.READ_CONTACTS)
    val allPermissionsText = rememberAllPermissionsText(multiplePermissionsState)

    Column(
        modifier = Modifier.padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PermissionButton(cameraPermissionText) { cameraPermissionState.launchPermissionRequest() }
        Spacer(modifier = Modifier.height(10.dp))
        PermissionButton(locationPermissionText) { locationPermissionState.launchPermissionRequest() }
        Spacer(modifier = Modifier.height(10.dp))
        PermissionButton(readContactsPermissionText) { readContactsPermissionState.launchPermissionRequest() }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
            Text(allPermissionsText.value)
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberPermissionText(
    permissionsState: MultiplePermissionsState,
    permission: String
): State<String> {
    val statusText = remember { mutableStateOf(permission.split('.').last().capitalize()) }

    val permissionStatus = permissionsState.permissions.find { it.permission == permission }?.status
    permissionStatus?.let {
        statusText.value = when {
            it.isGranted -> "$permission permission granted!"
            it.shouldShowRationale -> "The $permission is important for this app. Please grant the permission."
            else -> permission.split('.').last().capitalize()
        }
    }

    return statusText
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberAllPermissionsText(permissionsState: MultiplePermissionsState): State<String> {
    val statusText = remember { mutableStateOf("Request All Permissions") }

    val allPermissionsGranted = permissionsState.permissions.all { it.status.isGranted }
    val shouldShowRationale = permissionsState.permissions.any { it.status.shouldShowRationale }

    statusText.value = when {
        allPermissionsGranted -> "All permissions granted!"
        shouldShowRationale -> "Some permissions are important for this app. Please grant the permissions."
        else -> "Request All Permissions"
    }

    return statusText
}

@Composable
fun PermissionButton(textState: State<String>, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(textState.value)
    }
}