import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'my_script_widget_platform_interface.dart';

/// An implementation of [MyscriptwidgetPlatform] that uses method channels.
class MethodChannelMyScriptWidget extends MyScriptWidgetPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(
      'com.example.myScriptWidget_example.myScriptWidgetMethodChannel');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> clear() {
    return methodChannel.invokeMethod("clear");
  }

  @override
  Future<String?> initMyScriptWidget(
      List<int> myCertificate, String configPath) async {
    final res = await methodChannel.invokeMethod(
        "initMyScriptWidget", <String, dynamic>{
      'myCertificate': myCertificate,
      "configPath": configPath
    });
    return res;
  }

  @override
  Future<String?> getChannel() async {
    final res = await methodChannel.invokeMethod("getChannel");
    return res;
  }
}
