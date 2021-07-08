# human-android-sdk
The Biodigital Human Android SDK

The current version is 2.1.1, released 8 July 2021

Please visit http://developer.biodigital.com for documentation and setup instructions, and http://human.biodigital.com to see the 3D Human

To test the Sample Apps, download the ZIP and open HumanKotlinApp or HumanJava app in Android Studio<br><br>

NOTE: You'll need to generate an API key in our developer site and put your credentials and bundle ID into the Sample App to connect to our servers.

<hr>

To install human-android-sdk into your own app, add our maven repository to your top level build.gradle file:

    repositories {
    	...
        maven {
            url = uri("https://maven.pkg.github.com/biodigital-inc/human-android-sdk")
            credentials {
                username = "public"
                password = "<-- insert our public key here -->"
            }
        }
    }

And then add the following dependency to your module's build.gradle file:

    dependencies {
		...
		implementation 'com.biodigital-inc:human-android-sdk:2.1.1'
    }

