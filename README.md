# human-android-sdk
The Biodigital Human Android SDK

The current version is 139.1, released December 18, 2024

Please visit http://developer.biodigital.com for documentation and setup instructions, and http://human.biodigital.com to see the 3D Human

To test the Sample Apps, download the ZIP and open HumanKotlinApp or HumanJava app in Android Studio<br><br>

xsNOTE: You'll need to generate an API key in our developer site and put your credentials and bundle ID into the Sample App to connect to our servers.

<hr>

To install human-android-sdk into your own app, add our maven repository to your top level build.gradle file.  You should define BD_PASSWORD in your local.properties file:

```
BD_PASSWORD=<-- insert the github access key here -->
```

and add the following to the top-level build.gradle:

```
Properties properties = new Properties()
properties.load(project.rootProject.file("local.properties").newDataInputStream())

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/biodigital-inc/human-android-sdk")
            credentials {
                username = "public"
                password = properties.getProperty("BD_PASSWORD")
            }
        }
    }
}
```

And then add the following dependency to your module's build.gradle file:

```
    dependencies {
		...
		implementation 'com.biodigital-inc:human-android-sdk:139.1'
    }
```
