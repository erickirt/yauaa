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

package nl.basjes.parse.useragent.debug;

import nl.basjes.parse.useragent.AgentField;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgent.MutableUserAgent;
import nl.basjes.parse.useragent.analyze.Matcher;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugUserAgent extends MutableUserAgent { // NOSONAR: No need to override equals and hashcode

    private static final Logger LOG = LogManager.getLogger(DebugUserAgent.class);

    private final transient List<Pair<UserAgent, Matcher>> appliedMatcherResults = new ArrayList<>(32);

    DebugUserAgent(Collection<String> wantedFieldNames) {
        super(wantedFieldNames);
    }

    @Override
    public void set(MutableUserAgent newValuesUserAgent, Matcher appliedMatcher) {
        appliedMatcherResults.add(new ImmutablePair<>(new ImmutableUserAgent(newValuesUserAgent), appliedMatcher));
        super.set(newValuesUserAgent, appliedMatcher);
    }

    @Override
    public void reset() {
        appliedMatcherResults.clear();
        super.reset();
    }

    int getNumberOfAppliedMatches() {
        return appliedMatcherResults.size();
    }

    String toMatchTrace(List<String> highlightNames) {
        StringBuilder sb = new StringBuilder(4096);
        sb.append('\n');
        sb.append("+=========================================+\n");
        sb.append("| Matcher results that have been combined |\n");
        sb.append("+=========================================+\n");
        sb.append('\n');

        appliedMatcherResults.sort((o1, o2) -> {
            Matcher m1 = o1.getValue();
            Matcher m2 = o2.getValue();
            return m1.getMatcherSourceLocation().compareTo(m2.getMatcherSourceLocation());
        });

        for (Pair<UserAgent, Matcher> pair: appliedMatcherResults){
            sb.append('\n');
            sb.append("+================\n");
            sb.append("+ Applied matcher\n");
            sb.append("+----------------\n");
            UserAgent result = pair.getLeft();
            Matcher matcher = pair.getRight();
            sb.append(matcher.toString());
            sb.append("+----------------\n");
            sb.append("+ Results\n");
            sb.append("+----------------\n");
            for (String fieldName : result.getAvailableFieldNamesSorted()) {
                AgentField field = result.get(fieldName);

                if (field.getConfidence() >= 0) {
                    String marker = "";
                    if (highlightNames.contains(fieldName)) {
                        marker = " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
                    }
                    sb.append("| ").append(fieldName).append('(').append(field.getConfidence());
                    if (field.isDefaultValue()) {
                        sb.append(" => isDefaultValue");
                    }
                    sb.append(") = ").append(field.getValue()).append(marker).append('\n');
                }
            }
            sb.append("+================\n");
        }
        return sb.toString();
    }

    boolean analyzeMatchersResult() {
        boolean passed = true;
        for (String fieldName : getAvailableFieldNamesSorted()) {
            Map<Long, String> receivedValues = new HashMap<>(32);
            for (Pair<UserAgent, Matcher> pair: appliedMatcherResults) {
                UserAgent result = pair.getLeft();
                AgentField partialField = result.get(fieldName);
                if (partialField != null && partialField.getConfidence() > 0) {
                    String previousValue = receivedValues.get(partialField.getConfidence());
                    if (previousValue != null) {
                        if (!previousValue.equals(partialField.getValue())) {
                            if (passed) {
                                LOG.error("***********************************************************");
                                LOG.error("***        REALLY IMPORTANT ERRORS IN THE RULESET       ***");
                                LOG.error("*** YOU MUST CHANGE THE CONFIDENCE LEVELS OF YOUR RULES ***");
                                LOG.error("***********************************************************");
                            }
                            passed = false;
                            LOG.error("Found different value for \"{}\" with SAME confidence {}: \"{}\" and \"{}\"",
                                fieldName, partialField.getConfidence(), previousValue, partialField.getValue());
                        }
                    } else {
                        receivedValues.put(partialField.getConfidence(), partialField.getValue());
                    }
                }
            }
        }
        return passed;
    }


}
