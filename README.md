# human-android-sdk
The Biodigital Human Android SDK

Please visit http://developer.biodigital.com for documentation and setup instructions, and http://human.biodigital.com to see the 3D Human

To test the Sample Apps, download the ZIP and open HumanKotlinApp or HumanJava app in Android Studio<br>
NOTE: You'll need to generate an API key in our developer site and put your credentials into the Sample App to connect to our servers.

<hr>

To install human-android-sdk into your own app, just add the following to your top level build.gradle file:

    repositories {
    	...
        maven {
            url 'https://biodigital.bintray.com/human-android-sdk/'
        }
    }

And then add the following dependency to your module's build.gradle file:

    dependencies {
		...
    	implementation 'com.biodigital:human-android-sdk:1.2.0'
	}


