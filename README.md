[![official project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
# [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) web application project template

This template uses the HTML Compose library.

- `./gradlew jsBrowserRun` - run application in a browser
- `./gradlew jsBrowserProductionWebpack` - produce the output in `build/dist`


Enable system firewall (linux) to access the project

Install
- sudo apt update
- sudo apt install iptables ufw -y

- sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT

Open ports forever
- sudo ufw allow 8080/tcp
- sudo systemctl restart ufw


