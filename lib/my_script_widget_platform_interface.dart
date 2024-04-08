import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'my_script_widget_method_channel.dart';

abstract class MyScriptWidgetPlatform extends PlatformInterface {
  /// Constructs a MyscriptwidgetPlatform.
  MyScriptWidgetPlatform() : super(token: _token);

  static final Object _token = Object();

  static MyScriptWidgetPlatform _instance = MethodChannelMyScriptWidget();

  /// The default instance of [MyscriptwidgetPlatform] to use.
  ///
  /// Defaults to [MethodChannelMyscriptwidget].
  static MyScriptWidgetPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MyscriptwidgetPlatform] when
  /// they register themselves.
  static set instance(MyScriptWidgetPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> clear() {
    throw UnimplementedError('clear() has not been implemented.');
  }

  ///myCertificate 证书
  ///configPath 配置文件夹
  Future<String?> initMyScriptWidget(List<int> myCertificate, String configPath) {
    throw UnimplementedError('initMyScriptWidget() has not been implemented.');
  }

  Future<String?> getChannel() {
    throw UnimplementedError('initMyScriptWidget() has not been implemented.');
  }
}
