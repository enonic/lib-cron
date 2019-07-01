# Cron Library

## Getting the source

```sh
$ git clone git@github.com:enonic/lib-cron.git && cd lib-cron
```

or

```sh
$ git clone https://github.com/enonic/lib-cron.git && cd lib-cron
```

## Buildling

```sh
$ ./gradlew clean build
```

## Publishing

```sh
$ ./gradlew publish
```

## Include in an app

```build.gradle
dependencies {
	include 'com.enonic.lib:lib-cron:1.0.0-SNAPSHOT'
}
```
