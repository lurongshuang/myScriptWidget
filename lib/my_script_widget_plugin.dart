part of myscript;

class MyScriptWidgetPlugin {
  static const EventChannel eventChannel = EventChannel(
      "com.example.myScriptWidget_example.myScriptWidgetEventChannel");

  static Future<String?> getPlatformVersion() {
    return MyScriptWidgetPlatform.instance.getPlatformVersion();
  }

  static Stream<dynamic> getBroadcastStream() {
    return eventChannel
        .receiveBroadcastStream()
        .map((event) => event.toString())
        .asBroadcastStream();
  }

  static void clear() {
    MyScriptWidgetPlatform.instance.clear();
  }

  static Future<String?> initMyScriptWidget(
      {required List<int> myCertificate, required String configPath}) {
    return MyScriptWidgetPlatform.instance
        .initMyScriptWidget(myCertificate, configPath);
  }

  static Future<String?> getChannel() {
    return MyScriptWidgetPlatform.instance.getChannel();
  }
}
