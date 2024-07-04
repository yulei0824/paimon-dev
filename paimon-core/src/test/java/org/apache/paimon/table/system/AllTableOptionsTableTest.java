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

package org.apache.paimon.table.system;

import org.apache.paimon.catalog.AbstractCatalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.fs.Path;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.table.TableTestBase;
import org.apache.paimon.types.DataTypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.paimon.catalog.Catalog.SYSTEM_DATABASE_NAME;
import static org.apache.paimon.table.system.AllTableOptionsTable.ALL_TABLE_OPTIONS;
import static org.apache.paimon.table.system.AllTableOptionsTable.options;
import static org.apache.paimon.table.system.AllTableOptionsTable.toRow;
import static org.assertj.core.api.Assertions.assertThat;

/** Unit tests for {@link AllTableOptionsTable}. */
public class AllTableOptionsTableTest extends TableTestBase {

    private AllTableOptionsTable allTableOptionsTable;

    @BeforeEach
    public void before() throws Exception {
        Identifier identifier = identifier("T");
        Schema schema =
                Schema.newBuilder()
                        .column("product_id", DataTypes.INT())
                        .column("price", DataTypes.INT())
                        .column("sales", DataTypes.INT())
                        .primaryKey("product_id")
                        .option("merge-engine", "aggregation")
                        .option("fields.price.aggregate-function", "max")
                        .option("fields.sales.aggregate-function", "sum")
                        .build();
        catalog.createTable(identifier, schema, true);
        allTableOptionsTable =
                (AllTableOptionsTable)
                        catalog.getTable(new Identifier(SYSTEM_DATABASE_NAME, ALL_TABLE_OPTIONS));
    }

    @Test
    public void testSchemasTable() throws Exception {
        List<InternalRow> expectRow = getExceptedResult();
        List<InternalRow> result = read(allTableOptionsTable);
        assertThat(result).containsExactlyElementsOf(expectRow);
    }

    private List<InternalRow> getExceptedResult() {
        AbstractCatalog abstractCatalog = (AbstractCatalog) catalog;
        Map<String, Map<String, Path>> stringMapMap = abstractCatalog.allTablePaths();
        Iterator<InternalRow> rows =
                toRow(options(((AbstractCatalog) catalog).fileIO(), stringMapMap));
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(rows, Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }
}
