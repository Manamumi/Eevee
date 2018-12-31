# Coffee

Coffee is an RPC service providing key-value storage (most popularly configuration) as a service.

## Why use Coffee?

Coffee works off of raw TCP/IP and uses protobufs for messaging making it great for internal service configuration. Coffee powered configurations allow your application to retrieve configuration values lightning fast. All values are written to and read directly from memory and eventually persisted to disk.

## What about Redis?

Coffee is special in that all values are stored in a hierarchical format while Redis uses a flat-key system.