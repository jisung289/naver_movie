# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn

-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
-keepattributes InlinedApi
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class **.*$MyJavascriptInterface {
    *;
}
-keepclassmembers class **.*$JavaScriptInterface {
    *;
}

-keep public class **.*$MyJavascriptInteface
-keep public class **.*$JavaScriptInterface



-keep public class YOURPACKAGENAMEHERE.JavascriptCallback
-keep public class * implements YOURPACKAGENAMEHERE.JavascriptCallback
-keepclassmembers class * implements com.s_code.roezone.JavascriptCallback { <methods>; }
