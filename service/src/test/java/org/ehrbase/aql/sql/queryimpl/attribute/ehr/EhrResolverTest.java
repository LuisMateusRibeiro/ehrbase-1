package org.ehrbase.aql.sql.queryimpl.attribute.ehr;

import static org.junit.Assert.*;

import org.junit.Test;

public class EhrResolverTest {

    @Test
    public void testIsEhrAttribute() {

        assertTrue(EhrResolver.isEhrAttribute("ehr_id"));
        assertTrue(EhrResolver.isEhrAttribute("ehr_status"));
        assertTrue(EhrResolver.isEhrAttribute("system_id"));
        assertTrue(EhrResolver.isEhrAttribute("time_created/value"));

        assertFalse(EhrResolver.isEhrAttribute("time_created/time"));
    }
}
