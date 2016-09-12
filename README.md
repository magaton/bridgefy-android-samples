                                                                                                                                                                                                                                                                                                                      

![image alt text](image_0.png)

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

com.myapp.android.main

4. The resulting API Key will have a UUID format like this:

xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

## **Add the Bridgefy SDK to your project**

dependencies {    compile 'me.bridgefy.android:bridgefy-sdk:1.0.0'}

<manifest xmlns:android="http://schemas.android.com/apk/res/android"

    package="com.bridgefy.sample.app" >

    <application ...>

        <meta-data

            android:name="me.bridgefy.android.sdk.API_KEY"

            android:value="YOUR_UUID" />

        <activity>

        ...

        </activity>

    </application>

</manifest>

## **Initialize Bridgefy**

First, initialize Bridgefy:

<table>
  <tr>
    <td>Bridgefy.initialize();
</td>
  </tr>
</table>


## **Start Operations**

The start method starts all framework operations including discovery and advertising.

<table>
  <tr>
    <td>Bridgefy.start(bridgefyClient, messageListener,deviceListener);</td>
  </tr>
</table>


**Discovering Devices**

After the start method has been invoked, all discovered devices will be asynchronously received in your deviceListener in the onDeviceConnected method.  

<table>
  <tr>
    <td>@Override
public void onDeviceConnected(Device device, ConnectionType deviceType) {
….
}</td>
  </tr>
</table>


**Send Messages**

To send a message, use:

<table>
  <tr>
    <td>Bridgefy.sendMessage(device, message);</td>
  </tr>
</table>


or:

<table>
  <tr>
    <td>Bridgefy.sendMessage(userId, message);</td>
  </tr>
</table>


To send a broadcast  message, use:

<table>
  <tr>
    <td>Bridgefy.sendMessage(message);</td>
  </tr>
</table>


# Listeners

**MessageListener**

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onMessageDelivered(Session session, Message message)
Called when a Message was succesfully delivered.</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onMessageDeliveryStatus(Session session, int messageStatus, java.lang.String statusContent)
Receives a report of a message, status and content</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onMessageFailed(Message message, MessageException e)
Called when messages aren't able to be sent.</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onMessageReceived(Session session, Message message)
Called when messages are received.</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onMessageSent(Session session, Message message)
Called when a Message is ready to be sent.</td>
  </tr>
</table>


**Device****Listener**

<table>
  <tr>
    <td>Modifier and Type</td>
    <td>Method and Description</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onDeviceFound(Device device, ConnectionType deviceType)
Notify when a Device is discovered</td>
  </tr>
  <tr>
    <td>void</td>
    <td>onDeviceLost(Device device, ConnectionType deviceType)
Notify when a Device is lost</td>
  </tr>
</table>


# Configuration

<table>
  <tr>
    <td>Key</td>
    <td>Type</td>
    <td>Description</td>
  </tr>
  <tr>
    <td>antennaType</td>
    <td>Enum</td>
    <td>Decide if use a antenna type between Wifi and Bluetooth, can be ALL for using antenna signal available.</td>
  </tr>
  <tr>
    <td>discoveryInterval</td>
    <td>long</td>
    <td>Time to live in milliseconds for the device discovery interval</td>
  </tr>
  <tr>
    <td>timeToLiveDevice</td>
    <td>long</td>
    <td></td>
  </tr>
  <tr>
    <td>timeToLiveConnection</td>
    <td>long</td>
    <td></td>
  </tr>
  <tr>
    <td>timeToLiveMessage</td>
    <td>long</td>
    <td>Time to life in milliseconds at along the forwarding of the message mesh</td>
  </tr>
  <tr>
    <td>connectionRetries</td>
    <td>int</td>
    <td>Maximum number of connection retries</td>
  </tr>
  <tr>
    <td>messageRetry</td>
    <td>int</td>
    <td>Maximum number of attempts to send mesh messages</td>
  </tr>
  <tr>
    <td>tryingToMesh</td>
    <td>boolean</td>
    <td>When sending a direct message failed, this feature allows to send direct messages failed by mesh</td>
  </tr>
</table>


<table>
  <tr>
    <td>Time to live in milliseconds for the nearby device discovered, the devices was be removing from queue after end time to live.</td>
  </tr>
  <tr>
    <td>Time to wait in milliseconds for before finalize connectivity for inactivity.</td>
  </tr>
</table>


