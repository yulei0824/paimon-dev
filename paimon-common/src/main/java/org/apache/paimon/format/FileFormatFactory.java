/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.format;

import org.apache.paimon.options.Options;

/** Factory to create {@link FileFormat}. */
public interface FileFormatFactory {

    String identifier();

    FileFormat create(FormatContext formatContext);

    /** the format context. */
    class FormatContext {
        private final Options formatOptions;
        private final int readBatchSize;

        public FormatContext(Options formatOptions, int readBatchSize) {
            this.formatOptions = formatOptions;
            this.readBatchSize = readBatchSize;
        }

        public Options formatOptions() {
            return formatOptions;
        }

        public int readBatchSize() {
            return readBatchSize;
        }
    }
}
