yadb是一个根据原生adb不支持的功能做的扩展，现在已支持四个功能，分别是中文输入、截屏、布局抓取，长按屏幕

根目录已经编译出yadb，可以直接使用，如果是windows系统可以直接执行windows.bat看看效果。

## 中文输入
解决了使用adb shell input text输入乱码的问题
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -keyboard 你好，世界
```

## 截屏
可以无视Activity的禁止截屏
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -screenshot
```

## 布局抓取
比adb shell uiautomator dump好用，uiautomator有些界面获取不到
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
```

## 长按屏幕
做自动化点击或许可以用到
```
adb push yadb /data/local/tmp & adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -touch 500 500 2000
```


## 致谢
[scrcpy](https://github.com/Genymobile/scrcpy) 使用了复制文本到粘贴板的逻辑

[YRouter](https://github.com/ysbing/yrouter) 本人的另外一个库，一种无损耗的Android路由库，这里使用了免反射调用系统类的插件


## License

This package is released under [LGPLv3](https://opensource.org/licenses/LGPL-3.0)
