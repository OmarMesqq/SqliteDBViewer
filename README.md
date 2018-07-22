# SQLiteViewer

Requires Kitkat and above (SDK 19+)

<a href="" target="_blank">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="100"/></a>
<a href="https://play.google.com/store/apps/details?id=com.orpheusdroid.sqliteviewer" target="_blank">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="100"/></a>

## User Support
Telegram: [https://goo.gl/iP2Lpz](https://goo.gl/iP2Lpz)

[![Telegram Support QR](https://goo.gl/iP2Lpz.qr "Telegram Support QR")](https://goo.gl/iP2Lpz.qr)

Microsoft Teams:
URL:    [https://goo.gl/FCwfGG](https://goo.gl/FCwfGG)
Team code: va4ek1d

## Donation/Payments
#### Bitcoin:     1Cbf61y8XNx3BLWvoZB71x4XgBKB7r8BuB
#### PayPal:      [![Paypal Donate](https://www.paypalobjects.com/webstatic/en_US/i/btn/png/gold-pill-paypal-26px.png)](https://paypal.me/vijaichander/5)
#### Flattr:      [![Flattr this git repo](https://button.flattr.com/flattr-badge-large.png)](https://flattr.com/submit/auto?fid=66ngyo&url=https%3A%2F%2Fgithub.com%2Fvijai1996%2Fscreenrecorder)

## Building the app

### Make a copy of the repository

Make sure to have Git installed and clone the repo using

```
git clone https://gitlab.com/vijai/SqliteDBViewer
```

### Building the apk
Building apk is possible in 3 ways
* 1.a. [Building debug apk using commandline](https://gitlab.com/vijai/SqliteDBViewer#1a-building-debug-apk-using-commandline)
* 1.b. [Building release apk using commandline](https://gitlab.com/vijai/SqliteDBViewer#1b-building-release-apk-using-commandline)
* 2.   [Building using AndroidStudio](https://gitlab.com/vijai/SqliteDBViewer#2-building-using-androidstudio)

### 1.a. Building debug apk using commandline
Switch to project root directory and run

#### Build Fdroid flavour
```
gradlew.bat assembleFdroidDebug
```

#### Build Play flavour
```
gradlew.bat assemblePlayDebug
```

### 1.b. Building release apk using commandline
Switch to project root directory and make sure to edit `app` module's build.gradle to include signing key information and run

#### Build Fdroid flavour
```
gradlew.bat assembleFdroidRelease
```

#### Build Play flavour
```
gradlew.bat assemblePlayRelease
```

#### 2. Building using AndroidStudio
Open Android Studio -> File -> Import Project -> Choose the cloned project folder and continue with the on-screen instructions

## Contributions
Any contribution to the app is welcome in the form of pull requests.
I'm available on Microsoft Team for developer's discussion on the "Developers support" channel. Please feel free to join the team for discussion

## License and copyright infringements
I will consider any kind of license or copyright infringements seriously and will send copyright claim notice or license infringement notice to anyone who is not adhering to them.

If you notice any content which seem to be infringing, please fill the below google forms to help me indentify and take them down.

[Google Form](https://goo.gl/forms/ntFKRXflFh2NH1dx1)

## Authors

* **Vijai Chander** - *Initial work* - [vijai1996](https://gitlab.com/vijai)

## License

This project is licensed under the GNU AGPLv3 - see the [LICENSE](LICENSE) file for details
