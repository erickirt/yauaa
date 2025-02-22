#!/bin/bash
# Yet Another UserAgent Analyzer
# Copyright (C) 2013-2025 Niels Basjes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
SCRIPTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
TARGETDIR=$(cd "${SCRIPTDIR}/../../../resources/UserAgents" || exit 1; pwd)

INPUT="${SCRIPTDIR}/WindowsDesktop.csv"
OUTPUT="${TARGETDIR}/WindowsDesktopLookups.yaml"

[ "$1" = "--force" ] && rm "${OUTPUT}"

if [ "Generate.sh" -ot "${OUTPUT}" ]; then
    if [ "${INPUT}" -ot "${OUTPUT}" ]; then
        echo "Up to date: ${OUTPUT}";
        exit;
    fi
fi

echo "Generating: ${OUTPUT}";

(
echo '# $schema: https://yauaa.basjes.nl/v1/YauaaConfig.json'
echo "# ============================================="
echo "# THIS FILE WAS GENERATED; DO NOT EDIT MANUALLY"
echo "# ============================================="
echo "#"
echo "# Yet Another UserAgent Analyzer"
echo "# Copyright (C) 2013-2025 Niels Basjes"
echo "#"
echo "# Licensed under the Apache License, Version 2.0 (the \"License\");"
echo "# you may not use this file except in compliance with the License."
echo "# You may obtain a copy of the License at"
echo "#"
echo "# https://www.apache.org/licenses/LICENSE-2.0"
echo "#"
echo "# Unless required by applicable law or agreed to in writing, software"
echo "# distributed under the License is distributed on an \"AS IS\" BASIS,"
echo "# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
echo "# See the License for the specific language governing permissions and"
echo "# limitations under the License."
echo "#"
echo "config:"

echo "- lookup:"
echo "    name: 'WindowsDesktopOSName'"
echo "    map:"
grep -F -v '#' "${INPUT}" | grep  . | while read -r line
do
    tag=$(        echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f1)
    osname=$(     echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f2)
    echo "      \"${tag}\" : \"${osname}\""
done

echo "- lookup:"
echo "    name: 'WindowsDesktopOSVersion'"
echo "    map:"
grep -F -v '#' "${INPUT}" | grep  . | while read -r line
do
    tag=$(        echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f1)
    osversion=$(  echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f3)
    echo "      \"${tag}\" : \"${osversion}\""
done

echo "- lookup:"
echo "    name: 'WindowsDesktopOSNameVersion'"
echo "    map:"
grep -F -v '#' "${INPUT}" | grep  . | while read -r line
do
    tag=$(        echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f1)
    osnameversion=$(  echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f4)
    echo "      \"${tag}\" : \"${osnameversion}\""
done

echo "- lookup:"
echo "    name: 'WindowsDesktopOSCpuBits'"
echo "    map:"
grep -F -v '#' "${INPUT}" | grep  . | while read -r line
do
    tag=$(        echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f1)
    cpubits=$(    echo "${line}" | sed 's@ *| *@|@g' | cut -d'|' -f5)
    if [ -n "${cpubits}" ];
    then
        echo "      \"${tag}\" : \"${cpubits}\""
    fi
done

) >"${OUTPUT}"
