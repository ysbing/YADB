chcp 65001
F:\Users\DaoMai\Desktop\scrcpy\adb push yadb /data/local/tmp
F:\Users\DaoMai\Desktop\scrcpy\adb shell app_process -Djava.class.path=/data/local/tmp/yadb /data/local/tmp com.ysbing.yadb.Main -layout
F:\Users\DaoMai\Desktop\scrcpy\adb shell cat /data/local/tmp/yadb_layout_dump.xml
pause