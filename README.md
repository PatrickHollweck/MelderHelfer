# MelderHelfer

Android app to help German volunteer emergency service workers with dispatch

| Overview Screen | Alarm Screen |
| :-------------: | :----------: |
<img  src="https://raw.githubusercontent.com/PatrickHollweck/MelderHelfer/master/docs/images/App-Alarm-Screen.jpeg?raw=true" /> | <img src="https://raw.githubusercontent.com/PatrickHollweck/MelderHelfer/master/docs/images/App-Overview-Screen.jpeg?raw=true" />

## The What's and Why's

In Germany it is common practice that volunteer firefighters and EMS workers
get a SMS that contains the information for the call that the personell was called for. This app makes it easy to work with these alarms.

### Features
- Automatically start into the "Alarm" Screen when SMS is received.
- Ability to parse the SMS to automatically start google maps navigation to the address specified in the alarm.
- Ability to start Whatsapp to a specific Group. (We use this locally to organize)
- Text-To-Speech to read out the alarm.
- Alarm noise when a alarm is received.

## Install

This app will most likely never be release to google play.
Which is simply because it is too specific to be usefull for a wieder audience.

There are many things in the app that just dont work for people which are not in my very specific scenario.
Things like the alarm sms parsing are specifically tailored to my regions dispatch center. Meaning that it just
wont work with any other format.

If you do however still want to get the app, [download the latest release here](https://github.com/PatrickHollweck/MelderHelfer/releases/latest) and install it. Be aware that you will need to enable the "Install from unknown sources" option
