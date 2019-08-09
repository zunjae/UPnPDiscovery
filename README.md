# UPnPDiscovery

I legit don't know what I am doing but this works. Based off https://github.com/berndverst/android-ssdp/blob/master/app/src/main/java/ly/readon/android_ssdp/UPnPDiscovery.java

## Usage

```kotlin
val detector = UPnPDiscovery()

when (val result = detector.detect()) {
  is UPnPDetectorResult.Success -> {
    result.addresses
  }
  is UPnPDetectorResult.NoResults -> {
    print("No results found")
  }
  is UPnPDetectorResult.FatalError -> {
    print("Exception: ${result.exception.message}")
  }
}
```

## License

There's no license associated with this project. You may copy the code and modify it to your heart's content. You may not send me questions regarding this project. You're on your own.
