# Eye (Left)

I'm watching you all the time...

### How to build
The source code is available on https://github.com/kuwa0824/android/tree/master/eyeL.
* Unzip OpenCV android sdk in SDK_DIR(user defined).
* Start Android Studio and make new project with empty activity.
* File -> New -> Import Module
    - Set souce directory to "SDK_DIR/OpenCV-android-sdk/sdk" and modul name to ":opencv".
* File -> Project Structure -> Dependencies -> app -> + 3 Module Dependency
    - Select opencv at step1 and implementation at step2.
* Edit opencv/build.gradle.
    -  Comment out of "apply plugin: 'kotlin-android'
    -  Change compileSdkVersion to 33
    -  Change minSdkVersion to 23
    -  Change targetSdkVersion 33
* Delete project app directory.
* Replace source files.
* If necessary, change the folder name and package name in the files according to the application ID.
* Create a symbolic link from SDK_DIR/OpenCV-android-sdk/sdk to PROJECT_DIR/sdk.
* Reload Gradle Project and build.

