#!/bin/bash

sed -i -e "s|ENTER-CORRECT-VALUE|$(base64 -w 0 target/keystore.jks)|" target/test-classes/secret.yml
