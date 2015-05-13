DESCRIPTION = "TI Codecs and Server Combo for OMAP3530"
SECTION = "multimedia"
LICENSE = "TI"

require recipes-ti/includes/ti-paths.inc
require recipes-ti/includes/ti-staging.inc

INSANE_SKIP_${PN} = "installed-vs-shipped"

PR="${MACHINE_KERNEL_PR}"
PR_append = "b"

PV="4_02_00_00"

LIC_FILES_CHKSUM = "file://packages/ti/sdo/server/cs/docs/cs1omap3530_software_manifest.pdf;md5=84444ba7c7abe117c30f9c960d1930ec"

CODEC_SUITE_NAME="${WORKDIR}/${PN}_${PV}"

SRCREV = "8393c892b09e0ac42b19ff1531e232478c3b1a6c"

SRC_URI="file://codecs-omap3530_4_02_00_00.tar.gz \
    git://arago-project.org/git/projects/codec-servers.git;protocol=git \
    file://disable-c6accel.diff \
"

TI_BIN_UNPK_CMDS = "Y:workdir"

S = "${CODEC_SUITE_NAME}"

DEPENDS = "ti-cgt6x ti-xdctools ti-dspbios ti-codec-engine ti-linuxutils"

#generic codec
DSPSUFFIX_omap3530 = "x64P"

addtask prepsources after do_unpack before do_patch

do_prepsources () {

    mkdir -p ${CODEC_SUITE_NAME}/packages/ti/sdo/server/cs
    cp ${WORKDIR}/git/omap3530/cs1omap3530/rel-files/*  ${CODEC_SUITE_NAME}/  
    cp ${WORKDIR}/git/omap3530/cs1omap3530/source/*  ${CODEC_SUITE_NAME}/packages/ti/sdo/server/cs
    cp -a "${WORKDIR}/git/omap3530/cs1omap3530/docs"  ${CODEC_SUITE_NAME}/packages/ti/sdo/server/cs 

    mkdir -p ${CODEC_SUITE_NAME}/packages/ti/sdo/codecs
    cp -ar "${WORKDIR}/codecs-omap3530_4_02_00_00/packages" "${CODEC_SUITE_NAME}"
    chmod 755 -R ${CODEC_SUITE_NAME}
}

do_compile() {

    cd "${S}"

    make \
             CE_INSTALL_DIR=${CE_INSTALL_DIR} \
             FC_INSTALL_DIR=${FC_INSTALL_DIR} \
             LINK_INSTALL_DIR=${LINK_INSTALL_DIR} \
             CMEM_INSTALL_DIR=${CMEM_INSTALL_DIR} \
             LPM_INSTALL_DIR=${LPM_INSTALL_DIR} \
             BIOS_INSTALL_DIR=${BIOS_INSTALL_DIR} \
             CODEGEN_INSTALL_DIR=${CODEGEN_INSTALL_DIR} \
             XDC_INSTALL_DIR=${XDC_INSTALL_DIR} \
             CODEC_INSTALL_DIR="${S}" \
             XDCARGS="prod" \
             C6ACCEL_INSTALL_DIR=${C6ACCEL_INSTALL_DIR} \
             clean

    make \
             CE_INSTALL_DIR=${CE_INSTALL_DIR} \
             FC_INSTALL_DIR=${FC_INSTALL_DIR} \
             LINK_INSTALL_DIR=${LINK_INSTALL_DIR} \
             CMEM_INSTALL_DIR=${CMEM_INSTALL_DIR} \
             LPM_INSTALL_DIR=${LPM_INSTALL_DIR} \
             BIOS_INSTALL_DIR=${BIOS_INSTALL_DIR} \
             CODEGEN_INSTALL_DIR=${CODEGEN_INSTALL_DIR} \
             XDC_INSTALL_DIR=${XDC_INSTALL_DIR} \
             CODEC_INSTALL_DIR="${S}" \
             XDCARGS="prod" \
             C6ACCEL_INSTALL_DIR=${C6ACCEL_INSTALL_DIR} \
             all
}

do_install() {

    install -d ${D}/${installdir}/ti-codecs-server
    cd ${S}

    # Install the DSP Server Binary 
    for file in `find . -name *.${DSPSUFFIX}`; do
        cp ${file} ${D}/${installdir}/ti-codecs-server
    done

    # Install docs (codec qualiTI test reports, server config datasheet, etc)
    for file in `find . -name *.html`; do
        cp ${file} ${D}/${installdir}/ti-codecs-server
    done

    install -d ${D}${CODEC_INSTALL_DIR_RECIPE}
    cp -pPrf ${CODEC_SUITE_NAME}/* ${D}${CODEC_INSTALL_DIR_RECIPE}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

PACKAGES += "ti-codecs-omap3530-server"
FILES_ti-codecs-omap3530-server = "${installdir}/ti-codecs-server/*"


