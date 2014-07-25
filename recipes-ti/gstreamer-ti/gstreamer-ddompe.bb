require gstreamer-ti.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=c622b61fcc2fcd3b46a93ff72ac59688"

PV = "svnr${SRCREV}"

S = "${WORKDIR}/gstreamer_ti/ti_build/ticodecplugin"

PLATFORM = "omap35x"

SRCREV = "1160"

SRC_URI = "svn://gforge.ti.com/svn/gstreamer_ti/branches/BRANCH_DDOMPE;module=gstreamer_ti;protocol=https;user=anonymous;pswd='' \
           file://gstreamer-ti-rc.sh \
           file://gstti-init.service \
           file://jpeg-fix.patch;patch=1 \
"

# use local loadmodules.sh for these platform
# TODO: must be removed onces these loadmodules goes in gstreamer.ti.com
SRC_URI_append_dm365 = " file://loadmodules.sh"
SRC_URI_append_omapl137 = " file://loadmodules.sh"
SRC_URI_append_omapl138 = " file://loadmodules.sh "
SRC_URI_append_omap3 = " file://loadmodules.sh "

do_configure_prepend() {
    for makefile in $(find ${S} -name "Makefile.am") ; do
        sed -i -e 's:-Wl,@XDC_CONFIG_BASENAME@/linker.cmd:-Wl,-T,@XDC_CONFIG_BASENAME@/linker.cmd:g' $makefile
    done

    # XDCPATH fixups
    sed -i -e 's:(LINK_INSTALL_DIR)/packages:(LINK_XDC_ROOT):g' ${S}/src/Makefile.am
    sed -i -e 's:(XDC_USER_PATH);:(XDC_USER_PATH);$(EDMA3_LLD_INSTALL_DIR)/packages;$(C6ACCEL_INSTALL_DIR)/soc/c6accelw;$(C6ACCEL_INSTALL_DIR)/soc/packages:g' ${S}/src/Makefile.am
}

do_install_prepend() {
    # keep gstreamer-ti.inc's do_install_prepend happy
    mkdir -p ${WORKDIR}/gstreamer_ti/gstreamer_demo/shared
}

EXTRA_OECONF_omap3_append = " --with-preset=omap35x"
CPPFLAGS_append = " -I${BIOS_INSTALL_DIR}/packages"

