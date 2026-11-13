# Keep kotlinx.serialization models and Room entities intact
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.grammaradventure.app.domain.model.**$$serializer { *; }
-keepclassmembers class com.grammaradventure.app.domain.model.** {
    *** Companion;
}
