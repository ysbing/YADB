yadb是一个根据原生yadb不支持的功能做的扩展，现在已支持两个功能，分别是中文输入和长按屏幕。

根目录已经编译出yadb，可以直接使用，如果是windows系统可以直接执行windows.bat看看效果。

## 中文输入
```
adb shell input text hello
```

使用adb的文本输入命令，我们可以将文本输入到手机的编辑框上
```
adb shell input text 你好
```

再看，是不是出现了崩溃，调研得出，是系统在转换字符编码的时候出错了

怎么办，后面看了下网上的其他方案，有一条，大概原理是这样，安装ADBKeyBoard的apk到手机上，再通过adb发送指定广播，apk将收到广播的文本再输入到编辑框上，这个方案需要安装apk在手机上，太不友好了点

后面我看了下adb shell app_process命令，可以将一个dex推到手机上，并且直接执行，我在想，我是不是可以顺便把我要输入的文本带上去，然后通过这个dex来输入文本，就搞定了

于是，开发出来了，具体的使用如下：
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Server -keyboard 你好，世界
```

执行完再看下手机编辑框，是不是出现了中文，这个方案不用手动安装apk在手机上，应该是方便了许多

## 长按屏幕
adb有一个类似的命令
```
adb shell input swipe <x1> <y1> <x2> <y2> [duration(ms)]
```
这个命令是在两点间移动，两个点设置一样的话就是长按

我尝试了一下，发现了一个问题，就是在编辑框长按没法弹出粘贴，后面查了一下，是因为长按的点不是同一个，没法触发出长按的效果

于是，开发出来了，具体的使用如下：
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Server -touch 500 500 500 500 2000
```

## 致谢
[scrcpy](https://github.com/Genymobile/scrcpy) 使用了复制文本到粘贴板的逻辑

[ADBKeyBoard](https://github.com/senzhk/ADBKeyBoard) 参考了apk安装在手机上来实现输入的思路