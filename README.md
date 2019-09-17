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

This library is available under the MIT license, though there is no need to include a license and copyright notice
