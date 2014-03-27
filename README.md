Pervasive-Computing-Shared-Surface
==================================

Pervasive computing : Exchanging images on a shared surface

* Detect different cell phones placed on the surface.
* Distinctly visualize the different cell phones placed on the surface.
* Allow ‘pinning‘ the cell phone to the surface, after which it can be removed, but the visualization remains, allowing for interactions to continue.
* Build a basic Android photo browser that visualizes images on the local devices.
* Display images from the cell phone on the surface by sending it from the Andoid App over a network connection. Make sure to visually link the images on the surface to the cell phone they belong to.
* Provide rich ways in which to browse through the images visualized on the surface. (E.g. ScatterView)
* Allow transferring images between the cell phones placed on the surface by using touch gestures to drag and drop images on the visualisation of other phones.

Two main components :

### Surface app
* Detect and register phones.
* Receive image from phones.
* Send image to phones.
* Display images.

### Android app
* Show image gallery.
* Send image to surface.
* Receive image from surface.
* Handshake with surface.