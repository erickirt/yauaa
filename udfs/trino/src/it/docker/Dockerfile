#
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
#
ARG TRINO_VERSION

FROM trinodb/trino:${TRINO_VERSION:-The_TRINO_VERSION_argument_is_mandatory}

ARG YAUAA_VERSION

RUN mkdir -p /usr/lib/trino/plugin/yauaa
COPY ./target/yauaa-trino-${YAUAA_VERSION}-udf.jar  /usr/lib/trino/plugin/yauaa
RUN ls -laFR /usr/lib/trino/plugin/yauaa
