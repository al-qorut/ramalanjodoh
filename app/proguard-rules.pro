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
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
<init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
**[] $VALUES;
public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
*** rewind();
}

-keep class dagger.** { *; }
-keep interface dagger.** { *; }

-keep class **$$ModuleAdapter { *; }
-keepnames class **$$InjectAdapter { *; }

-keepclassmembers class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <init>(...);
}

#courotine
-keepnames class kotlinx.** { *; }
-dontwarn org.jetbrains.kotlinx.**
-keepnames class androidx.lifecycle.** { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
# will keep line numbers and file name obfuscation
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Firebase Firestore - Menjaga semua kelas Firestore tetap utuh
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes *Annotation*
-keep class * extends java.io.Serializable { *; }
-keep class * implements android.os.Parcelable { *; }


# Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class * implements com.google.gson.*

# Keep all classes related to Kotlin and lifecycle
-keepnames class kotlinx.** { *; }
-dontwarn org.jetbrains.kotlinx.**
-keepnames class androidx.lifecycle.** { *; }
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}


-keep class smk.adzikro.ramalanjodoh.data.models.Ramalx { *; }
-keep class smk.adzikro.ramalanjodoh.data.models.Userx { *; }
-keep class smk.adzikro.ramalanjodoh.data.models.Comment { *; }
-dontwarn smk.adzikro.ramalanjodoh.Hilt_MyApp
-dontwarn smk.adzikro.ramalanjodoh.ui.activities.Hilt_BaseActivity
-dontwarn smk.adzikro.ramalanjodoh.ui.activities.Hilt_CommentActivity
-dontwarn smk.adzikro.ramalanjodoh.ui.activities.Hilt_MainActivity
-dontwarn smk.adzikro.ramalanjodoh.ui.activities.Hilt_SettingsActivity
-dontwarn smk.adzikro.ramalanjodoh.ui.fragments.online.Hilt_OnlineFragment

