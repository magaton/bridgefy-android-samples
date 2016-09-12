**Bridgefy**


**Android SDK Documentation**


Version 2.0


September 2016

# Introduction

The Bridgefy 

## System Requirements

### Hardware Interfaces

...

### Software supported

...

### Performance requirements

...

# Getting Started

## **Registration**

Follow these steps to enable the Bridgefy SDK for Android inside your app and get an API key:

1. Go to the [Bridgefy Developer Console](http://bridgefy.me).

2. Click **Register New App**.

3. Enter your app package details, for example:


```
#!python

com.myapp.android.main
```


4. The resulting API Key will have a UUID format like this:


```
#!python

xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```


## **Add the Bridgefy SDK to your project**


```
#!xml

dependencies {
    compile 'me.bridgefy.android:bridgefy-sdk:1.0.0'
}

<manifest xmlns:android="http://schemas.android.com/apk/res/android"

    package="com.bridgefy.sample.app" >

    <application ...>

        <meta-data

            android:name="me.bridgefy.android.sdk.API_KEY"

            android:value="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" />

        <activity>

        ...

        </activity>

    </application>

</manifest>

```

## **Initialize Bridgefy**

First, initialize Bridgefy:


```
#!java

Bridgefy.initialize();
```




## **Start Operations**

The start method starts all framework operations including discovery and advertising.



```
#!java

Bridgefy.start(bridgefyClient, messageListener,deviceListener);

```



**Discovering Devices**

After the start method has been invoked, all discovered devices will be asynchronously received in your deviceListener in the onDeviceConnected method.  



```
#!java

public void onDeviceConnected(Device device, ConnectionType deviceType) {
….
}

```

**Send Messages**

To send a message, use:


```
#!java

Bridgefy.sendMessage(device, message);

```



or:


```
#!java

Bridgefy.sendMessage(userId, message);

```



To send a broadcast  message, use:



```
#!java

Bridgefy.sendMessage(message);
```