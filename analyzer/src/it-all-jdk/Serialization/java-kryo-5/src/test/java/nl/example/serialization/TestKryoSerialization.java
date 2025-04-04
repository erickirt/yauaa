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

package nl.example.serialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestKryoSerialization {

    @Test
    void cycleWithTestBefore() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
            () -> new SerializeWithKryo().cycleWithTestBefore());
        assertTrue(illegalArgumentException.getMessage().contains("Class is not registered"));
    }

    @Test
    void cycleWithoutTestBefore() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
            () -> new SerializeWithKryo().cycleWithoutTestBefore());
        assertTrue(illegalArgumentException.getMessage().contains("Class is not registered"));
    }
}
