## Forked from the yoctoproject version.
This [dylan-fix] branch contains a few patches to the upstream [dylan]
branch to fix compile issues with the latest Yocto [dylan].

Only tested building for a Gumstix Overo 3.5.7 kernel system.

## Integration Instructions
TI installers require this, if you are using a 64-bit version of ubuntu:

    $ sudo apt-get install ia32-libs

Gumstix uses a meta-ti layer with issues.  We have a fork which fixes them, so lets replace theirs with ours.

    $ cd ~/yocto/poky
    $ rm -rf ./meta-ti
    $ git clone https://github.com/chrisw957/meta-ti.git

Then, edit ~/yocto/poky/build/conf/bblayers.conf to add the meta-ti layer so that is looks something like this:

    BBLAYERS ?= " \
      /home/chris/yocto/poky/meta \
      /home/chris/yocto/poky/meta-yocto \
      /home/chris/yocto/poky/meta-openembedded/meta-gnome \
      /home/chris/yocto/poky/meta-openembedded/meta-oe \
      /home/chris/yocto/poky/meta-openembedded/meta-xfce \
      /home/chris/yocto/poky/meta-openembedded/meta-systemd \
      /home/chris/yocto/poky/meta-openembedded/meta-networking \
      /home/chris/yocto/poky/meta-openembedded/meta-efl \
      /home/chris/yocto/poky/meta-openembedded/meta-multimedia \
      /home/chris/yocto/poky/meta-gumstix \
      /home/chris/yocto/poky/meta-gumstix-extras \
      /home/chris/yocto/poky/meta-ti \
      "

Edit ~/yocto/poky/build/conf/local.conf and add the following line to ignore some of the meta-ti recipes:

    BBMASK ?= ".*/meta-ti/recipes-(misc|bsp/formfactor)/"

Fix the toolchain_path env variable by editing ~/yocto/poky/meta-gumstix/conf/machine/overo.conf and add the two lines shown below.  This fixes the problem finding the compiler when building dsplink.

    TOOLCHAIN_PATH ?= "${STAGING_DIR_NATIVE}${prefix_native}/bin/${TUNE_PKGARCH}${HOST_VENDOR}-${HOST_OS}"
    TOOLCHAIN_SYSPATH ?= "${TOOLCHAIN_PATH}/${TARGET_SYS}"

Now, before we get into the DSP, we need to download the TI code generation tools from https://www-a.ti.com/downloads/sds_support/TICodegenerationTools/download.htm

To install it:

    $ cp ti_cgt_c6000_7.2.7_setup_linux_x86.bin ~/yocto/build/downloads
    $ touch ~/yocto/build/downloads/ti_cgt_c6000_7.2.7_setup_linux_x86.bin.done

In your image recipe, for the gst-ti ddompe branch, add:

    gstreamer-ddompe

Once booted, you will generally still have to manually load the ti modules, depending on your startup script configuration.  Do this with:

    /etc/init.d/gstti-init start
    
## Sample Pipelines
Here is one which encodes vga testsource:

    gst-launch -v videotestsrc ! \
    'video/x-raw-yuv, format=(fourcc)UYVY, width=(int)640, height=(int)480, framerate=(fraction)20/1' ! \
    videorate ! \
    dmaiaccel ! \
    queue max-size-bytes=0 max-size-buffers=5 ! \
    dmaienc_h264 ! \
    queue max-size-bytes=0 max-size-buffers=5 ! \
    dmaiperf engine-name=codecServer ! \
    fakesink silent=true sync=true

Generating video with videotestsrc can actually take quite a bit of CPU.  One work around is to generate a frame and write it to a file, then use that pre-generated frame for your tests.  This works ok for mjpeg type encodings, but for h.264 encoding, you would need a series of frames so that you have "motion".  Here is how to generate a test frame of snow:

    gst-launch -v videotestsrc pattern=snow is-live=true num-buffers=1 ! \
    'video/x-raw-yuv, format=(fourcc)UYVY, width=(int)1280, height=(int)960, framerate=(fraction)12/1' ! \
    filesink location=snow-frame.yuv

Then, this is a test of encoding that snow using the dsp's jpeg codec.  For a 1280x960 frame of snow, at 10 fps, I measured the cpu at 44% idle with this pipeline:

    gst-launch -v multifilesrc location=snow-frame.yuv ! \
    'video/x-raw-yuv, format=(fourcc)UYVY, width=(int)1280, height=(int)960, framerate=(fraction)10/1' ! \
    videorate ! \
    dmaiaccel ! \
    queue max-size-bytes=0 max-size-buffers=5 ! \
    dmaienc_jpeg ! \
    queue max-size-bytes=0 max-size-buffers=5 ! \
    dmaiperf engine-name=codecServer ! \
    fakesink silent=true sync=true

## Original README
The official OpenEmbedded/Yocto BSP layer for Texas Instruments platforms.

It is hosted on http://git.yoctoproject.org/cgit/cgit.cgi/meta-ti/ with the
source repository at git://git.yoctoproject.org/meta-ti


This layer depends on:

URI: git://git.openembedded.org/openembedded-core
layers: meta
branch: master


The base BSP part of meta-ti should work with different OpenEmbedded/Yocto
distributions and layer stacks, such as:
distro-less (only with OE-Core), with Yocto/Poky, with Angstrom or Arago.

Please follow the recommended setup procedures of your OE distribution.


Send pull requests, patches, comments or questions to meta-ti@yoctoproject.org

Maintainers: Denys Dmytriyenko <denys@ti.com>
