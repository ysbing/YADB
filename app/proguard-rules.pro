# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontwarn android.app.IUiAutomationConnection
-dontwarn android.app.UiAutomationConnection
-dontwarn android.content.IClipboard$Stub
-dontwarn android.content.IClipboard
-dontwarn android.hardware.display.IDisplayManager$Stub
-dontwarn android.hardware.display.IDisplayManager
-dontwarn android.hardware.input.IInputManager$Stub
-dontwarn android.hardware.input.IInputManager
-dontwarn android.os.ServiceManager
-dontwarn android.view.DisplayInfo

-keep class com.daomai.stub.Main{*;}