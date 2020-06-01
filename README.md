## Summary

This is the Android SDK of Omilia™. You can read more about Omilia™ at [omilia.com].

## Table of contents

* [Basic integration](#basic-integration)
   * [Add SDK to Project](#add-sdk-to-project)
   * [Add permissions](#add-permissions)
   * [Basic setup](#basic-setup)
   * [Build your app](#build-your-app)
   * [R8 / Proguard](#r8-proguard)

## Basic integration
We will describe the steps to integrate the Omilia Android SDK into your Android project. We are going to assume that you are using Android Studio for your development.

### Add SDK to Project
To integrate Omilia's SDK into your project, copy `omilia-android-sdk.aar` into the `app/libs` folder of your app.
Then, in your modules `build.gradle` (the one under "app"), add the following dependencies:
```
implementation 'com.squareup.okhttp3:okhttp:4.2.2'
implementation 'com.google.code.gson:gson:2.8.6'
```

### Add permissions
Please add the following permissions, which Omilia's SDK needs, if they are not already present in your AndroidManifest.xml file:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### Basic setup
We recommend using an android [Application][android_application] class to initialize the SDK. If you already have one in your app already, follow these steps:

Firstly, in your `Application` class find or create the `onCreate` method and add the following code to initialize Omilia's SDK:

```java
import com.omilia.sdk.OmiliaClient;

public class AppMain extends Application {

    // overrides
    @Override
    public final void onCreate() {

        super.onCreate();

        OmiliaClient.launch(this);
    }
}
```

Secondly, in your `res/values` folder make sure to create a file `omilia.xml` with the following contents:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <string name="omilia_sdk_key">{OmiliaSdkKey}</string>
    <string name="omilia_base_url">{OmiliaBaseUrl}</string>
    <string name="omilia_bot_name">{OmiliaBotName}</string>

</resources>
```

**Note**: Initializing Omilia's SDK like this is `very important`. Replace `{OmiliaSdkKey}`, `{OmiliaBaseUrl}` and `{OmiliaBotName}` with your SDK key, your simple-proxy connector url and 
the desired bot name.

### Build your app

Build and run your app. If the build succeeds, you should carefully read the SDK logs in the console.


### R8 / ProGuard

If you are using R8 or ProGuard add the options from [build.gradle][build_gradle].


[omilia.com]:  http://www.omilia.com

[android_application]:   http://developer.android.com/reference/android/app/Application.html
[build_gradle]:          ./app/build.gradle
