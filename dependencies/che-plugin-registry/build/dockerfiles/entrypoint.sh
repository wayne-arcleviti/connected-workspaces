#!/bin/bash
#
# Copyright (c) 2012-2018 Red Hat, Inc.
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Updates plugin runner images to point a registry defined by environment
# variables
#     CHE_SIDECAR_CONTAINERS_REGISTRY_URL
#     CHE_SIDECAR_CONTAINERS_REGISTRY_ORGANIZATION
#     CHE_SIDECAR_CONTAINERS_REGISTRY_TAG
#
# By default, this script will operate on the `/var/www/html/v3` directory.
# This can be overridden by the environment variable $METAS_DIR
#
# Will execute any arguments on completion (`exec $@`)
# For testing, the images used for the che-theia, theia runtime, and machine-exec
# plugins can be overridden via the environment variables
#     CHE_PLUGIN_REGISTRY_THEIA_IMAGE
#     CHE_PLUGIN_REGISTRY_THEIA_ENDPOINT_RUNTIME_BINARY_IMAGE
#     CHE_PLUGIN_REGISTRY_MACHINE_EXEC_IMAGE
# If set, these env vars override all modifications
# (e.g. via CHE_DEVFILE_IMAGES_REGISTRY_URL)
#

set -e

REGISTRY=${CHE_SIDECAR_CONTAINERS_REGISTRY_URL}
ORGANIZATION=${CHE_SIDECAR_CONTAINERS_REGISTRY_ORGANIZATION}
TAG=${CHE_SIDECAR_CONTAINERS_REGISTRY_TAG}

DEFAULT_METAS_DIR="/var/www/html/v3"
METAS_DIR="${METAS_DIR:-${DEFAULT_METAS_DIR}}"

# Regex used to break an image reference into groups:
#   \1 - Whitespace and (optional) quotation preceding image reference
#   \2 - Registry portion of image, e.g. (quay.io)/eclipse/che-theia:tag
#   \3 - Organization portion of image, e.g. quay.io/(eclipse)/che-theia:tag
#   \4 - Image name portion of image, e.g. quay.io/eclipse/(che-theia):tag
#   \5 - Tag of image, e.g. quay.io/eclipse/che-theia:(tag)
#   \6 - Optional quotation following image reference
IMAGE_REGEX='([[:space:]]*"?)([._:a-zA-Z0-9-]*)/([._a-zA-Z0-9-]*)/([._a-zA-Z0-9-]*):([._a-zA-Z0-9-]*)("?)'

# We can't use the `-d` option for readarray because
# registry.centos.org/centos/httpd-24-centos7 ships with Bash 4.2
# The below command will fail if any path contains whitespace
readarray -t metas < <(find "${METAS_DIR}" -name 'meta.yaml')
for meta in "${metas[@]}"; do
  echo "Checking meta $meta"
  # Need to update each field separately in case they are not defined.
  # Defaults don't work because registry and tags may be different.
  if [ -n "$REGISTRY" ]; then
    echo "    Updating image registry to $REGISTRY"
    sed -i -E "s|image:$IMAGE_REGEX|image:\1${REGISTRY}/\3/\4:\5\6|" "$meta"
  fi
  if [ -n "$ORGANIZATION" ]; then
    echo "    Updating image organization to $ORGANIZATION"
    sed -i -E "s|image:$IMAGE_REGEX|image:\1\2/${ORGANIZATION}/\4:\5\6|" "$meta"
  fi
  if [ -n "$TAG" ]; then
    echo "    Updating image tag to $TAG"
    sed -i -E "s|image:$IMAGE_REGEX|image:\1\2/\3/\4:${TAG}\6|" "$meta"
  fi
done

if [ -n "$CHE_PLUGIN_REGISTRY_THEIA_IMAGE" ]; then
  echo "Overriding Che Theia image with $CHE_PLUGIN_REGISTRY_THEIA_IMAGE"
  sed -i -E "s|image:.*theia-rhel8.*|image: $CHE_PLUGIN_REGISTRY_THEIA_IMAGE|" "${metas[@]}"
fi

if [ -n "$CHE_PLUGIN_REGISTRY_THEIA_ENDPOINT_RUNTIME_BINARY_IMAGE" ]; then
  echo "Overriding Theia remote runtime image with $CHE_PLUGIN_REGISTRY_THEIA_ENDPOINT_RUNTIME_BINARY_IMAGE"
  sed -i -E "s|image:.*theia-endpoint-rhel8.*|image: $CHE_PLUGIN_REGISTRY_THEIA_ENDPOINT_RUNTIME_BINARY_IMAGE|" "${metas[@]}"
fi

if [ -n "$CHE_PLUGIN_REGISTRY_MACHINE_EXEC_IMAGE" ]; then
  echo "Overriding Theia remote runtime image with $CHE_PLUGIN_REGISTRY_MACHINE_EXEC_IMAGE"
  sed -i -E "s|image:.*machineexec-rhel8.*|image: $CHE_PLUGIN_REGISTRY_MACHINE_EXEC_IMAGE|" "${metas[@]}"
fi

exec "${@}"
