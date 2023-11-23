package com.example.first_flutter_example

import `in`.lending.finbox_lending_plugin.FinBoxLendingPlugin
import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugins.firebase.messaging.FlutterFirebaseMessagingBackgroundService

// ...


class CustomerApplication : FlutterApplication(), PluginRegistrantCallback {
    // ...
    override fun onCreate() {
        super.onCreate()
        FinBoxLendingPlugin.initLibrary(this);
        // FlutterFirebaseMessagingBackgroundService.setPluginRegistrant(this)
    }

    override fun registerWith(registry: PluginRegistry) {
        GeneratedPluginRegistrant.registerWith(registry as FlutterEngine);
    }
}
