/*
 * Copyright (c) 2020 vitasystems GmbH and Hannover Medical School.
 *
 * This file is part of project EHRbase
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.aql.sql.queryimpl.translator.testcase.pg10.jsquery;

import org.ehrbase.aql.sql.queryimpl.translator.testcase.UC7;

public class TestUC7 extends UC7 {

    public TestUC7() {
        super();
        this.expectedSqlExpression = "select (ehr.xjsonb_array_elements((\"ehr\".\"entry\".\"entry\" #>>\n"
                + "                                   '{/composition[openEHR-EHR-COMPOSITION.health_summary.v1],/content[openEHR-EHR-ACTION.immunisation_procedure.v1]}')::jsonb) #>>\n"
                + "        '{/description[at0001],/items[at0002],0,/value,value}') as \"/description[at0001]/items[at0002]/value/value\"\n"
                + "from \"ehr\".\"entry\",\n"
                + "     lateral (\n"
                + "         select (ehr.xjsonb_array_elements((\"ehr\".\"entry\".\"entry\" #>>\n"
                + "                                            '{/composition[openEHR-EHR-COMPOSITION.health_summary.v1],/content[openEHR-EHR-ACTION.immunisation_procedure.v1]}')::jsonb) #>>\n"
                + "                 '{/description[at0001],/items[at0002],0,/value,value}')\n"
                + "                    AS COLUMN) as \"ARRAY\"\n"
                + "where (\"ehr\".\"entry\".\"template_id\" = ? and (ARRAY.COLUMN = 'Hepatitis A'))";
        testDomainAccess.getServerConfig().setUseJsQuery(true);
    }
}