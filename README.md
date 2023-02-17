# PubNubDemo

An simple app to demonstrate how [PubNub](https://www.pubnub.com/docs "PubNub Docs") is used in message chat and push notification.

## Libraries

+  Jetpack
   +  Compose
   +  Datastore
   +  Lifecycle
   +  Navigation
+  Dagger-Hilt
+  PubNub
+  Firebase Cloud Messaging

## Notes

Remember to check your keys in local.properties
```properties
## This file is automatically generated by Android Studio.
# Do not modify this file -- YOUR CHANGES WILL BE ERASED!
#
# This file should *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
sdk.dir={Your sdk dir}
pubKey="{Your PubNub Publish Key}"
subKey="{Your PubNub Subscribe Key}"
secKey="{Your PubNub Secret Key}"
```