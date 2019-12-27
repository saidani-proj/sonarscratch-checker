# How install SONARSCRATCH

You can follow theses steps to install *SONARSCRATCH* checker :

1. Ensure **Java** installed, if not install it from <https://openjdk.java.net>. The minimum version is **1.11**.

2. Install [**SONARSCRATCH docker**](https://github.com/tcdorg/sonarscratch-docker). Actually available only for Linux.

3. Install *SONARSCRATCH* checker. Actually available only for Linux as [debian package](../dist/debian/packages/sn-scratch-ch_1.0.0_all.deb). Download the debian package (**sn-scratch-ch_1.0.0_all.deb**) and execute this command in same directory ``sudo dpkg -i sn-scratch-ch_1.0.0_all.deb``.

4. To check your installation, run this command ``sn-scratch-ch --version``, you should see the version, like this ``1.0.0``.
