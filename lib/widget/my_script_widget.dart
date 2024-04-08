part of myscript;

class MyScriptWidget extends StatefulWidget {
  final int width;
  final int height;
  final String bgColor;
  final String penColor;

  const MyScriptWidget(
      {required this.width,
      required this.height,
      super.key,
      this.bgColor = "#000000",
      this.penColor = "#FFFFFF"});

  @override
  State<MyScriptWidget> createState() => _MyScriptWidgetState();
}

const String viewType = "com.example.myscriptwidget.view";

class _MyScriptWidgetState extends State<MyScriptWidget> {
  @override
  Widget build(BuildContext context) {
    return getScriptWidget();
  }

  Widget getScriptWidget() {
    Widget myScriptWidget = const Placeholder();
    ViewParams viewParams = ViewParams(
        width: widget.width,
        height: widget.height,
        bgColor: widget.bgColor,
        penColor: widget.penColor);

    if (Platform.isAndroid) {
      myScriptWidget = AndroidView(
        viewType: viewType,
        creationParams: viewParams.toJson(),
        hitTestBehavior: PlatformViewHitTestBehavior.opaque,
        creationParamsCodec: const StandardMessageCodec(),
      );
    } else if (Platform.isIOS) {
      myScriptWidget = UiKitView(
          viewType: viewType,
          creationParams: viewParams.toJson(),
          hitTestBehavior: PlatformViewHitTestBehavior.opaque,
          creationParamsCodec: const StandardMessageCodec());
    }

    return Container(
        width: widget.width.toDouble(),
        height: widget.height.toDouble(),
        child: myScriptWidget);
  }
}

class ViewParams {
  int width;
  int height;
  String bgColor;
  String penColor;

  ViewParams(
      {required this.width,
      required this.height,
      required this.bgColor,
      required this.penColor});

  Map<String, dynamic> toJson() => <String, dynamic>{
        "width": width,
        "height": height,
        "bgColor": bgColor,
        "penColor": penColor
      };
}
