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

package nl.basjes.parse.useragent.servlet.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.servlet.ParseService;
import nl.basjes.parse.useragent.servlet.exceptions.MissingUserAgentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static nl.basjes.parse.useragent.servlet.api.Utils.splitPerFilledLine;
import static nl.basjes.parse.useragent.servlet.utils.Constants.EXAMPLE_JSON;
import static nl.basjes.parse.useragent.servlet.utils.Constants.EXAMPLE_TWO_USERAGENTS;
import static nl.basjes.parse.useragent.servlet.utils.Constants.EXAMPLE_USERAGENT;
import static nl.basjes.parse.useragent.servlet.utils.Constants.EXAMPLE_XML;
import static nl.basjes.parse.useragent.servlet.utils.Constants.EXAMPLE_YAML;
import static nl.basjes.parse.useragent.servlet.utils.Constants.TEXT_XYAML_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Tag(name = "Analyze", description = "Analyzing the useragents")
@RequestMapping(value = "/yauaa/v1")
@RestController
public class ApiYamlOutput {

    private final ParseService parseService;

    @Autowired
    public ApiYamlOutput(ParseService parseService) {
        this.parseService = parseService;
    }

    // -------------------------------------------------
    // GET /analyze + accept --> Yaml

    @Operation(
        summary = "Analyze the provided User-Agent",
        description = "<b>Trying this in swagger does not work in Chrome as Chrome does not allow setting " +
            "a different User-Agent: <a href=\"https://github.com/swagger-api/swagger-ui/issues/5035\">swagger-ui issue 5035</a></b>"
    )
    @ApiResponse(
        responseCode = "200", // HttpStatus.OK
        description = "The agent was successfully analyzed",
        content = {
            @Content(mediaType = APPLICATION_JSON_VALUE,    examples = @ExampleObject(EXAMPLE_JSON)),
            @Content(mediaType = APPLICATION_XML_VALUE,     examples = @ExampleObject(EXAMPLE_XML)),
            @Content(mediaType = TEXT_XYAML_VALUE,          examples = @ExampleObject(EXAMPLE_YAML)),
            @Content(mediaType = TEXT_PLAIN_VALUE,          examples = @ExampleObject(EXAMPLE_YAML))
        }
    )
    @GetMapping(
        value = "/analyze",
        produces = {TEXT_XYAML_VALUE, TEXT_PLAIN_VALUE}
    )
    public String handleGETAnalyze(
        @Parameter(
            name = "User-Agent",
            description = "The standard browser request header User-Agent is used as the input that is to be analyzed.",
            example = EXAMPLE_USERAGENT
        )
        @RequestHeader("User-Agent") String userAgentString
    ) {
        return createOutput(userAgentString);
    }

    // -------------------------------------------------
    // GET /analyze/yaml --> Yaml

    @Operation(
        summary = "Analyze the provided User-Agent",
        description = "<b>Trying this in swagger does not work in Chrome as Chrome does not allow setting " +
            "a different User-Agent: <a href=\"https://github.com/swagger-api/swagger-ui/issues/5035\">swagger-ui issue 5035</a></b>"
    )
    @ApiResponse(
        responseCode = "200", // HttpStatus.OK
        description = "The agent was successfully analyzed",
        content = {
            @Content(mediaType = TEXT_XYAML_VALUE,          examples = @ExampleObject(EXAMPLE_YAML)),
            @Content(mediaType = TEXT_PLAIN_VALUE,          examples = @ExampleObject(EXAMPLE_YAML))
        }
    )
    @GetMapping(
        value = "/analyze/yaml",
        produces = {TEXT_XYAML_VALUE, TEXT_PLAIN_VALUE}
    )
    public String handleGETAnalyzeYaml(
        @Parameter(
            name = "User-Agent",
            description = "The standard browser request header User-Agent is used as the input that is to be analyzed.",
            example = EXAMPLE_USERAGENT
        )
        @RequestHeader("User-Agent") String userAgentString
    ) {
        return createOutput(userAgentString);
    }

    // -------------------------------------------------
    // POST /analyze + accept --> Yaml

    @Operation(
        summary = "Analyze the provided User-Agent"
    )
    @PostMapping(
        value ="/analyze",
        consumes = TEXT_PLAIN_VALUE,
        produces = {TEXT_XYAML_VALUE, TEXT_PLAIN_VALUE}
    )
    @ApiResponse(
        responseCode = "200", // HttpStatus.OK
        description = "The agent was successfully analyzed",
        content = {
            @Content(mediaType = APPLICATION_JSON_VALUE,    examples = @ExampleObject(EXAMPLE_JSON)),
            @Content(mediaType = APPLICATION_XML_VALUE,     examples = @ExampleObject(EXAMPLE_XML)),
            @Content(mediaType = TEXT_XYAML_VALUE,          examples = @ExampleObject(EXAMPLE_YAML)),
            @Content(mediaType = TEXT_PLAIN_VALUE,          examples = @ExampleObject(EXAMPLE_YAML))
        }
    )
    public String handlePOSTAnalyze(
        @Parameter(
            name = "Request body",
            schema = @Schema(
                type = "A newline separated list of useragent strings",
                description = "The entire POSTed value is used as the input that is to be analyzed.",
                example = EXAMPLE_TWO_USERAGENTS)
        )
        @RequestBody String userAgentString
    ) {
        return createOutput(userAgentString);
    }

    // -------------------------------------------------
    // POST /analyze/yaml --> Yaml
    @Operation(
        summary = "Analyze the provided User-Agent"
    )
    @PostMapping(
        value = "/analyze/yaml",
        consumes = TEXT_PLAIN_VALUE,
        produces = {TEXT_XYAML_VALUE, TEXT_PLAIN_VALUE}
    )
    @ApiResponse(
        responseCode = "200", // HttpStatus.OK
        description = "The agent was successfully analyzed",
        content = {
            @Content(mediaType = TEXT_XYAML_VALUE,          examples = @ExampleObject(EXAMPLE_YAML)),
            @Content(mediaType = TEXT_PLAIN_VALUE,          examples = @ExampleObject(EXAMPLE_YAML))
        }
    )
    public String handlePOSTAnalyzeYaml(
        @Parameter(
            name = "Request body",
            schema = @Schema(
                type = "A newline separated list of useragent strings",
                description = "The entire POSTed value is used as the input that is to be analyzed.",
                example = EXAMPLE_TWO_USERAGENTS)
        )
        @RequestBody String userAgentString
    ) {
        return createOutput(userAgentString);
    }

    // -------------------------------------------------

    private String createOutput(String userAgentString) {
        if (userAgentString == null) {
            throw new MissingUserAgentException();
        }
        parseService.ensureStartedForApis(OutputType.JSON);
        if (parseService.isUserAgentAnalyzerAvailable()) {
            UserAgentAnalyzer userAgentAnalyzer = parseService.getUserAgentAnalyzer();
            List<String> result = new ArrayList<>(2048);
            for (String input : splitPerFilledLine(userAgentString)) {
                if (input.startsWith("#")) {
                    result.add(input);
                } else {
                    UserAgent userAgent = userAgentAnalyzer.parse(input);
                    result.add(userAgent.toYamlTestCase(userAgent.getCleanedAvailableFieldNamesSorted()));
                }
            }
            return String.join("\n", result);
        }
        return "";
    }

}
