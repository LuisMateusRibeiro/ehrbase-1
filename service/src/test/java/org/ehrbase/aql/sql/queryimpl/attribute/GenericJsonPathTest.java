package org.ehrbase.aql.sql.queryimpl.attribute;

import static org.junit.Assert.*;

import org.junit.Test;

public class GenericJsonPathTest {

    @Test
    public void jqueryPath() {

        assertEquals(
                "'{context,health_care_facility,external_ref,id}'",
                new GenericJsonPath("context/health_care_facility/external_ref/id").jqueryPath());
        assertEquals("'{setting,defining_code}'", new GenericJsonPath("setting/defining_code").jqueryPath());
        assertEquals(
                "'{context,other_context,/items[openEHR-EHR-CLUSTER.composition_context_detail.v1],$AQL_NODE_ITERATIVE$}'",
                new GenericJsonPath("context/other_context/items[openEHR-EHR-CLUSTER.composition_context_detail.v1]")
                        .jqueryPath());
        assertEquals(
                "'{context,other_context,/items[openEHR-EHR-CLUSTER.composition_context_detail.v1],$AQL_NODE_ITERATIVE$,/items[at0001],$AQL_NODE_ITERATIVE$}'",
                new GenericJsonPath(
                                "context/other_context/items[openEHR-EHR-CLUSTER.composition_context_detail.v1]/items[at0001]")
                        .jqueryPath());
        assertEquals(
                "'{context,other_context,/items[openEHR-EHR-CLUSTER.composition_context_detail.v1],$AQL_NODE_ITERATIVE$,/items[at0001],$AQL_NODE_ITERATIVE$,/value,value}'",
                new GenericJsonPath(
                                "context/other_context/items[openEHR-EHR-CLUSTER.composition_context_detail.v1]/items[at0001]/value/value")
                        .jqueryPath());
        assertEquals("'{context,name,0,value}'", new GenericJsonPath("context/name/value").jqueryPath());
    }

    @Test
    public void jqueryPathOtherDetails() {

        assertEquals(
                "'{other_details,/items[at0111],$AQL_NODE_ITERATIVE$,/value}'",
                new GenericJsonPath("other_details/items[at0111]/value").jqueryPath());
        assertEquals(
                "'{other_details,/items[at0111],$AQL_NODE_ITERATIVE$,/name,0}'",
                new GenericJsonPath("other_details/items[at0111]/name").jqueryPath());
        assertEquals(
                "'{other_details,/items[at0111],$AQL_NODE_ITERATIVE$,/value,value}'",
                new GenericJsonPath("other_details/items[at0111]/value/value").jqueryPath());
        assertEquals(
                "'{other_details,/items[at0111],$AQL_NODE_ITERATIVE$,/name,0,value}'",
                new GenericJsonPath("other_details/items[at0111]/name/value").jqueryPath());
    }
}
