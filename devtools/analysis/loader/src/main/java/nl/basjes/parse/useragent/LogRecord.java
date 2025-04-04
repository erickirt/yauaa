/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2025 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.basjes.parse.core.Field;

import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode
public final class LogRecord implements Comparable<LogRecord> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Getter @Setter                                                                                 private long count = 1;

    @Getter @Setter(onMethod=@__(@Field("TIME.EPOCH:request.receive.time.epoch")))                  private String epoch                 = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.YEAR:request.receive.time.year_utc")))                private String yearUtc               = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.MONTH:request.receive.time.month_utc")))              private String monthUtc              = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.DAY:request.receive.time.day_utc")))                  private String dayUtc                = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.HOUR:request.receive.time.hour_utc")))                private String hourUtc               = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.MINUTE:request.receive.time.minute_utc")))            private String minuteUtc             = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.SECOND:request.receive.time.second_utc")))            private String secondUtc             = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.MILLISECOND:request.receive.time.millisecond_utc")))  private String millisecondUtc        = null;
    @Getter @Setter(onMethod=@__(@Field("TIME.MICROSECOND:request.receive.time.microsecond_utc")))  private String microsecondUtc        = null;

    @Getter @Setter(onMethod=@__(@Field("HTTP.USERAGENT:request.user-agent")))                      private String userAgent              = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua")))                   private String secChUa                = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-arch")))              private String secChUaArch            = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-bitness")))           private String secChUaBitness         = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-full-version")))      private String secChUaFullVersion     = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-full-version-list"))) private String secChUaFullVersionList = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-mobile")))            private String secChUaMobile          = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-model")))             private String secChUaModel           = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-platform")))          private String secChUaPlatform        = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-platform-version")))  private String secChUaPlatformVersion = null;
    @Getter @Setter(onMethod=@__(@Field("HTTP.HEADER:request.header.sec-ch-ua-wow64")))             private String secChUaWow64           = null;

    private void putIfNotNull(Map<String, String> result, String header, String value) {
        if (value != null) {
            result.put(header, value);
        }
    }

    public Map<String, String> asHeadersMap() {
        Map<String, String> result = new LinkedHashMap<>(); // LinkedHashMap to fix the key ordering when creating the toJson.
        putIfNotNull(result, "User-Agent",                  userAgent);
        putIfNotNull(result, "Sec-Ch-Ua",                   secChUa);
        putIfNotNull(result, "Sec-Ch-Ua-Arch",              secChUaArch);
        putIfNotNull(result, "Sec-Ch-Ua-Bitness",           secChUaBitness);
        putIfNotNull(result, "Sec-Ch-Ua-Full-Version",      secChUaFullVersion);
        putIfNotNull(result, "Sec-Ch-Ua-Full-Version-List", secChUaFullVersionList);
        putIfNotNull(result, "Sec-Ch-Ua-Mobile",            secChUaMobile);
        putIfNotNull(result, "Sec-Ch-Ua-Model",             secChUaModel);
        putIfNotNull(result, "Sec-Ch-Ua-Platform",          secChUaPlatform);
        putIfNotNull(result, "Sec-Ch-Ua-Platform-Version",  secChUaPlatformVersion);
        putIfNotNull(result, "Sec-Ch-Ua-Wow64",             secChUaWow64);
        return result;
    }

    // Cache this value
    private String toJson = null;

    public String toJson() {
        if (toJson == null) {
            try {
                toJson = OBJECT_MAPPER.writeValueAsString(asHeadersMap());
            } catch (JsonProcessingException e) {
                toJson = e.toString();
            }
        }
        return toJson;
    }

    private String escapeYaml(String input) {
        return input.replace("'", "''");
    }

    private void appendIfNotNull(StringBuilder sb, String header, String value) {
        if (value != null) {
            sb.append("      ").append(header).append(": '").append(escapeYaml(value)).append("'\n");
        }
    }

    // Cache this value
    private String toString = null;

    @Override
    public String toString() {
        if (toString == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("- test:\n");
            sb.append("    input:\n");
            appendIfNotNull(sb, "User-Agent                   ", userAgent);
            appendIfNotNull(sb, "Sec-Ch-Ua                    ", secChUa);
            appendIfNotNull(sb, "Sec-Ch-Ua-Arch               ", secChUaArch);
            appendIfNotNull(sb, "Sec-Ch-Ua-Bitness            ", secChUaBitness);
            appendIfNotNull(sb, "Sec-Ch-Ua-Full-Version       ", secChUaFullVersion);
            appendIfNotNull(sb, "Sec-Ch-Ua-Full-Version-List  ", secChUaFullVersionList);
            appendIfNotNull(sb, "Sec-Ch-Ua-Mobile             ", secChUaMobile);
            appendIfNotNull(sb, "Sec-Ch-Ua-Model              ", secChUaModel);
            appendIfNotNull(sb, "Sec-Ch-Ua-Platform           ", secChUaPlatform);
            appendIfNotNull(sb, "Sec-Ch-Ua-Platform-Version   ", secChUaPlatformVersion);
            appendIfNotNull(sb, "Sec-Ch-Ua-Wow64              ", secChUaWow64);
            toString = sb.toString();
        }
        return toString;
    }

    private Integer useragentCacheKey = null;

    public Integer getUseragentCacheKey() {
        if (useragentCacheKey == null) {
            useragentCacheKey = toString().hashCode();
        }
        return useragentCacheKey;
    }

    public int compareFields(String ours, String other) {
        if (ours == null){
            if (other == null) {
                return 0;
            } else {
                return -1;
            }
        }
        if (other == null) {
            return 1;
        }
        return ours.compareTo(other);
    }

    @Override
    public int compareTo(LogRecord other) {
        int compare;

        compare = compareFields(userAgent, other.userAgent);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUa, other.secChUa);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaArch, other.secChUaArch);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaBitness, other.secChUaBitness);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaFullVersion, other.secChUaFullVersion);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaFullVersionList, other.secChUaFullVersionList);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaMobile, other.secChUaMobile);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaModel, other.secChUaModel);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaPlatform, other.secChUaPlatform);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaPlatformVersion, other.secChUaPlatformVersion);
        if (compare != 0) {
            return compare;
        }

        compare = compareFields(secChUaWow64, other.secChUaWow64);
        return compare;
    }
}
