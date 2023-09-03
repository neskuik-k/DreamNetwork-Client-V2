-allowaccessmodification
-dontoptimize
-dontshrink
#-dontnote *
#-dontwarn *
-keepattributes Signature,InnerClasses,SourceFile,LineNumberTable,*Annotations*,LocalVariable*Table
-keep class be.alexandre01.dreamnetwork.core.Launcher
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.Launcher {
    <methods>;
    }


    -keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }

-keep class be.alexandre01.dreamnetwork.api.**
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.api.** {
    <methods>;
}

-keep class be.alexandre01.dreamnetwork.api.connection.request.RequestType
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.api.connection.request.RequestType {
    <methods>;
    <fields>;
}

-keep class be.alexandre01.dreamnetwork.core.console.language.Emoji
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.console.language.Emoji {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.service.bundle.BundleInfo {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class be.alexandre01.dreamnetwork.api.config.GlobalSettings {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.service.JVMProfiles {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class
    be.alexandre01.dreamnetwork.api.service.tasks.TaskData {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class
    be.alexandre01.dreamnetwork.api.service.tasks.GlobalTasks {
    <methods>;
    <fields>;
    }

-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.service.JVMConfig {
    <methods>;
    <fields>;
    }

-keep class be.alexandre01.dreamnetwork.core.console.**
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.console.** {
    <methods>;
    }

-keep class be.alexandre01.dreamnetwork.utils.**
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.utils.** {
    <methods>;
    }

-keep class be.alexandre01.dreamnetwork.core.utils.**
-keepclasseswithmembernames class be.alexandre01.dreamnetwork.core.utils.** {
    <methods>;
}

-keep public class javax.xml.*