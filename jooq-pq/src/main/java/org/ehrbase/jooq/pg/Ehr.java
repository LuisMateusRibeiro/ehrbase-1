/*
 * Copyright (c) 2022 vitasystems GmbH and Hannover Medical School.
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
package org.ehrbase.jooq.pg;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.ehrbase.jooq.pg.tables.Access;
import org.ehrbase.jooq.pg.tables.AdminDeleteAttestation;
import org.ehrbase.jooq.pg.tables.AdminDeleteAudit;
import org.ehrbase.jooq.pg.tables.AdminDeleteComposition;
import org.ehrbase.jooq.pg.tables.AdminDeleteCompositionHistory;
import org.ehrbase.jooq.pg.tables.AdminDeleteContribution;
import org.ehrbase.jooq.pg.tables.AdminDeleteEhr;
import org.ehrbase.jooq.pg.tables.AdminDeleteEhrFull;
import org.ehrbase.jooq.pg.tables.AdminDeleteEhrHistory;
import org.ehrbase.jooq.pg.tables.AdminDeleteEventContextForCompo;
import org.ehrbase.jooq.pg.tables.AdminDeleteFolder;
import org.ehrbase.jooq.pg.tables.AdminDeleteFolderHistory;
import org.ehrbase.jooq.pg.tables.AdminDeleteFolderObjRefHistory;
import org.ehrbase.jooq.pg.tables.AdminDeleteStatus;
import org.ehrbase.jooq.pg.tables.AdminDeleteStatusHistory;
import org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositions;
import org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositionsForContrib;
import org.ehrbase.jooq.pg.tables.AdminGetLinkedContributions;
import org.ehrbase.jooq.pg.tables.AdminGetLinkedStatusForContrib;
import org.ehrbase.jooq.pg.tables.AdminGetTemplateUsage;
import org.ehrbase.jooq.pg.tables.Attestation;
import org.ehrbase.jooq.pg.tables.AttestationRef;
import org.ehrbase.jooq.pg.tables.AttestedView;
import org.ehrbase.jooq.pg.tables.AuditDetails;
import org.ehrbase.jooq.pg.tables.CompoXref;
import org.ehrbase.jooq.pg.tables.Composition;
import org.ehrbase.jooq.pg.tables.CompositionHistory;
import org.ehrbase.jooq.pg.tables.Concept;
import org.ehrbase.jooq.pg.tables.Contribution;
import org.ehrbase.jooq.pg.tables.Entry;
import org.ehrbase.jooq.pg.tables.EntryHistory;
import org.ehrbase.jooq.pg.tables.EventContext;
import org.ehrbase.jooq.pg.tables.EventContextHistory;
import org.ehrbase.jooq.pg.tables.FlywaySchemaHistory;
import org.ehrbase.jooq.pg.tables.Folder;
import org.ehrbase.jooq.pg.tables.FolderHierarchy;
import org.ehrbase.jooq.pg.tables.FolderHierarchyHistory;
import org.ehrbase.jooq.pg.tables.FolderHistory;
import org.ehrbase.jooq.pg.tables.FolderItems;
import org.ehrbase.jooq.pg.tables.FolderItemsHistory;
import org.ehrbase.jooq.pg.tables.Heading;
import org.ehrbase.jooq.pg.tables.Identifier;
import org.ehrbase.jooq.pg.tables.JsonbArrayElements;
import org.ehrbase.jooq.pg.tables.Language;
import org.ehrbase.jooq.pg.tables.ObjectRef;
import org.ehrbase.jooq.pg.tables.ObjectRefHistory;
import org.ehrbase.jooq.pg.tables.Participation;
import org.ehrbase.jooq.pg.tables.ParticipationHistory;
import org.ehrbase.jooq.pg.tables.PartyIdentified;
import org.ehrbase.jooq.pg.tables.PartyUsageIdentification;
import org.ehrbase.jooq.pg.tables.Plugin;
import org.ehrbase.jooq.pg.tables.SessionLog;
import org.ehrbase.jooq.pg.tables.Status;
import org.ehrbase.jooq.pg.tables.StatusHistory;
import org.ehrbase.jooq.pg.tables.StoredQuery;
import org.ehrbase.jooq.pg.tables.System;
import org.ehrbase.jooq.pg.tables.TemplateStore;
import org.ehrbase.jooq.pg.tables.TerminologyProvider;
import org.ehrbase.jooq.pg.tables.Territory;
import org.ehrbase.jooq.pg.tables.XjsonbArrayElements;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteAttestationRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteAuditRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteCompositionHistoryRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteCompositionRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteContributionRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteEhrFullRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteEhrHistoryRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteEhrRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteEventContextForCompoRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteFolderHistoryRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteFolderObjRefHistoryRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteFolderRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteStatusHistoryRecord;
import org.ehrbase.jooq.pg.tables.records.AdminDeleteStatusRecord;
import org.ehrbase.jooq.pg.tables.records.AdminGetLinkedCompositionsForContribRecord;
import org.ehrbase.jooq.pg.tables.records.AdminGetLinkedCompositionsRecord;
import org.ehrbase.jooq.pg.tables.records.AdminGetLinkedContributionsRecord;
import org.ehrbase.jooq.pg.tables.records.AdminGetLinkedStatusForContribRecord;
import org.ehrbase.jooq.pg.tables.records.AdminGetTemplateUsageRecord;
import org.ehrbase.jooq.pg.tables.records.JsonbArrayElementsRecord;
import org.ehrbase.jooq.pg.tables.records.PartyUsageIdentificationRecord;
import org.ehrbase.jooq.pg.tables.records.XjsonbArrayElementsRecord;
import org.ehrbase.jooq.pg.udt.CodePhrase;
import org.ehrbase.jooq.pg.udt.DvCodedText;
import org.jooq.Catalog;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.UDT;
import org.jooq.impl.SchemaImpl;

/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Ehr extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>ehr</code>
     */
    public static final Ehr EHR = new Ehr();

    /**
     * defines the modality for accessing an com.ethercis.ehr (security strategy
     * implementation)
     */
    public final Access ACCESS = Access.ACCESS;

    /**
     * The table <code>ehr.admin_delete_attestation</code>.
     */
    public final AdminDeleteAttestation ADMIN_DELETE_ATTESTATION = AdminDeleteAttestation.ADMIN_DELETE_ATTESTATION;

    /**
     * Call <code>ehr.admin_delete_attestation</code>.
     */
    public static Result<AdminDeleteAttestationRecord> ADMIN_DELETE_ATTESTATION(
            Configuration configuration, UUID attestRefInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminDeleteAttestation.ADMIN_DELETE_ATTESTATION.call(attestRefInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_attestation</code> as a table.
     */
    public static AdminDeleteAttestation ADMIN_DELETE_ATTESTATION(UUID attestRefInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteAttestation.ADMIN_DELETE_ATTESTATION.call(attestRefInput);
    }

    /**
     * Get <code>ehr.admin_delete_attestation</code> as a table.
     */
    public static AdminDeleteAttestation ADMIN_DELETE_ATTESTATION(Field<UUID> attestRefInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteAttestation.ADMIN_DELETE_ATTESTATION.call(attestRefInput);
    }

    /**
     * The table <code>ehr.admin_delete_audit</code>.
     */
    public final AdminDeleteAudit ADMIN_DELETE_AUDIT = AdminDeleteAudit.ADMIN_DELETE_AUDIT;

    /**
     * Call <code>ehr.admin_delete_audit</code>.
     */
    public static Result<AdminDeleteAuditRecord> ADMIN_DELETE_AUDIT(Configuration configuration, UUID auditInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteAudit.ADMIN_DELETE_AUDIT.call(auditInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_audit</code> as a table.
     */
    public static AdminDeleteAudit ADMIN_DELETE_AUDIT(UUID auditInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteAudit.ADMIN_DELETE_AUDIT.call(auditInput);
    }

    /**
     * Get <code>ehr.admin_delete_audit</code> as a table.
     */
    public static AdminDeleteAudit ADMIN_DELETE_AUDIT(Field<UUID> auditInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteAudit.ADMIN_DELETE_AUDIT.call(auditInput);
    }

    /**
     * The table <code>ehr.admin_delete_composition</code>.
     */
    public final AdminDeleteComposition ADMIN_DELETE_COMPOSITION = AdminDeleteComposition.ADMIN_DELETE_COMPOSITION;

    /**
     * Call <code>ehr.admin_delete_composition</code>.
     */
    public static Result<AdminDeleteCompositionRecord> ADMIN_DELETE_COMPOSITION(
            Configuration configuration, UUID compoIdInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminDeleteComposition.ADMIN_DELETE_COMPOSITION.call(compoIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_composition</code> as a table.
     */
    public static AdminDeleteComposition ADMIN_DELETE_COMPOSITION(UUID compoIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteComposition.ADMIN_DELETE_COMPOSITION.call(compoIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_composition</code> as a table.
     */
    public static AdminDeleteComposition ADMIN_DELETE_COMPOSITION(Field<UUID> compoIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteComposition.ADMIN_DELETE_COMPOSITION.call(compoIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_composition_history</code>.
     */
    public final AdminDeleteCompositionHistory ADMIN_DELETE_COMPOSITION_HISTORY =
            AdminDeleteCompositionHistory.ADMIN_DELETE_COMPOSITION_HISTORY;

    /**
     * Call <code>ehr.admin_delete_composition_history</code>.
     */
    public static Result<AdminDeleteCompositionHistoryRecord> ADMIN_DELETE_COMPOSITION_HISTORY(
            Configuration configuration, UUID compoInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminDeleteCompositionHistory.ADMIN_DELETE_COMPOSITION_HISTORY.call(
                                compoInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_composition_history</code> as a table.
     */
    public static AdminDeleteCompositionHistory ADMIN_DELETE_COMPOSITION_HISTORY(UUID compoInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteCompositionHistory.ADMIN_DELETE_COMPOSITION_HISTORY.call(
                compoInput);
    }

    /**
     * Get <code>ehr.admin_delete_composition_history</code> as a table.
     */
    public static AdminDeleteCompositionHistory ADMIN_DELETE_COMPOSITION_HISTORY(Field<UUID> compoInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteCompositionHistory.ADMIN_DELETE_COMPOSITION_HISTORY.call(
                compoInput);
    }

    /**
     * The table <code>ehr.admin_delete_contribution</code>.
     */
    public final AdminDeleteContribution ADMIN_DELETE_CONTRIBUTION = AdminDeleteContribution.ADMIN_DELETE_CONTRIBUTION;

    /**
     * Call <code>ehr.admin_delete_contribution</code>.
     */
    public static Result<AdminDeleteContributionRecord> ADMIN_DELETE_CONTRIBUTION(
            Configuration configuration, UUID contribIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteContribution.ADMIN_DELETE_CONTRIBUTION.call(
                        contribIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_contribution</code> as a table.
     */
    public static AdminDeleteContribution ADMIN_DELETE_CONTRIBUTION(UUID contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteContribution.ADMIN_DELETE_CONTRIBUTION.call(contribIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_contribution</code> as a table.
     */
    public static AdminDeleteContribution ADMIN_DELETE_CONTRIBUTION(Field<UUID> contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteContribution.ADMIN_DELETE_CONTRIBUTION.call(contribIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_ehr</code>.
     */
    public final AdminDeleteEhr ADMIN_DELETE_EHR = AdminDeleteEhr.ADMIN_DELETE_EHR;

    /**
     * Call <code>ehr.admin_delete_ehr</code>.
     */
    public static Result<AdminDeleteEhrRecord> ADMIN_DELETE_EHR(Configuration configuration, UUID ehrIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteEhr.ADMIN_DELETE_EHR.call(ehrIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_ehr</code> as a table.
     */
    public static AdminDeleteEhr ADMIN_DELETE_EHR(UUID ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhr.ADMIN_DELETE_EHR.call(ehrIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_ehr</code> as a table.
     */
    public static AdminDeleteEhr ADMIN_DELETE_EHR(Field<UUID> ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhr.ADMIN_DELETE_EHR.call(ehrIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_ehr_full</code>.
     */
    public final AdminDeleteEhrFull ADMIN_DELETE_EHR_FULL = AdminDeleteEhrFull.ADMIN_DELETE_EHR_FULL;

    /**
     * Call <code>ehr.admin_delete_ehr_full</code>.
     */
    public static Result<AdminDeleteEhrFullRecord> ADMIN_DELETE_EHR_FULL(Configuration configuration, UUID ehrIdParam) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteEhrFull.ADMIN_DELETE_EHR_FULL.call(ehrIdParam))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_ehr_full</code> as a table.
     */
    public static AdminDeleteEhrFull ADMIN_DELETE_EHR_FULL(UUID ehrIdParam) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhrFull.ADMIN_DELETE_EHR_FULL.call(ehrIdParam);
    }

    /**
     * Get <code>ehr.admin_delete_ehr_full</code> as a table.
     */
    public static AdminDeleteEhrFull ADMIN_DELETE_EHR_FULL(Field<UUID> ehrIdParam) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhrFull.ADMIN_DELETE_EHR_FULL.call(ehrIdParam);
    }

    /**
     * The table <code>ehr.admin_delete_ehr_history</code>.
     */
    public final AdminDeleteEhrHistory ADMIN_DELETE_EHR_HISTORY = AdminDeleteEhrHistory.ADMIN_DELETE_EHR_HISTORY;

    /**
     * Call <code>ehr.admin_delete_ehr_history</code>.
     */
    public static Result<AdminDeleteEhrHistoryRecord> ADMIN_DELETE_EHR_HISTORY(
            Configuration configuration, UUID ehrIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteEhrHistory.ADMIN_DELETE_EHR_HISTORY.call(ehrIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_ehr_history</code> as a table.
     */
    public static AdminDeleteEhrHistory ADMIN_DELETE_EHR_HISTORY(UUID ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhrHistory.ADMIN_DELETE_EHR_HISTORY.call(ehrIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_ehr_history</code> as a table.
     */
    public static AdminDeleteEhrHistory ADMIN_DELETE_EHR_HISTORY(Field<UUID> ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEhrHistory.ADMIN_DELETE_EHR_HISTORY.call(ehrIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_event_context_for_compo</code>.
     */
    public final AdminDeleteEventContextForCompo ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO =
            AdminDeleteEventContextForCompo.ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO;

    /**
     * Call <code>ehr.admin_delete_event_context_for_compo</code>.
     */
    public static Result<AdminDeleteEventContextForCompoRecord> ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO(
            Configuration configuration, UUID compoIdInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminDeleteEventContextForCompo.ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO
                                .call(compoIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_event_context_for_compo</code> as a table.
     */
    public static AdminDeleteEventContextForCompo ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO(UUID compoIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEventContextForCompo.ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO.call(
                compoIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_event_context_for_compo</code> as a table.
     */
    public static AdminDeleteEventContextForCompo ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO(Field<UUID> compoIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteEventContextForCompo.ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO.call(
                compoIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_folder</code>.
     */
    public final AdminDeleteFolder ADMIN_DELETE_FOLDER = AdminDeleteFolder.ADMIN_DELETE_FOLDER;

    /**
     * Call <code>ehr.admin_delete_folder</code>.
     */
    public static Result<AdminDeleteFolderRecord> ADMIN_DELETE_FOLDER(Configuration configuration, UUID folderIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteFolder.ADMIN_DELETE_FOLDER.call(folderIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_folder</code> as a table.
     */
    public static AdminDeleteFolder ADMIN_DELETE_FOLDER(UUID folderIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolder.ADMIN_DELETE_FOLDER.call(folderIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_folder</code> as a table.
     */
    public static AdminDeleteFolder ADMIN_DELETE_FOLDER(Field<UUID> folderIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolder.ADMIN_DELETE_FOLDER.call(folderIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_folder_history</code>.
     */
    public final AdminDeleteFolderHistory ADMIN_DELETE_FOLDER_HISTORY =
            AdminDeleteFolderHistory.ADMIN_DELETE_FOLDER_HISTORY;

    /**
     * Call <code>ehr.admin_delete_folder_history</code>.
     */
    public static Result<AdminDeleteFolderHistoryRecord> ADMIN_DELETE_FOLDER_HISTORY(
            Configuration configuration, UUID folderIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteFolderHistory.ADMIN_DELETE_FOLDER_HISTORY.call(
                        folderIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_folder_history</code> as a table.
     */
    public static AdminDeleteFolderHistory ADMIN_DELETE_FOLDER_HISTORY(UUID folderIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolderHistory.ADMIN_DELETE_FOLDER_HISTORY.call(folderIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_folder_history</code> as a table.
     */
    public static AdminDeleteFolderHistory ADMIN_DELETE_FOLDER_HISTORY(Field<UUID> folderIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolderHistory.ADMIN_DELETE_FOLDER_HISTORY.call(folderIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_folder_obj_ref_history</code>.
     */
    public final AdminDeleteFolderObjRefHistory ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY =
            AdminDeleteFolderObjRefHistory.ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY;

    /**
     * Call <code>ehr.admin_delete_folder_obj_ref_history</code>.
     */
    public static Result<AdminDeleteFolderObjRefHistoryRecord> ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY(
            Configuration configuration, UUID contributionIdInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminDeleteFolderObjRefHistory.ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY
                                .call(contributionIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_folder_obj_ref_history</code> as a table.
     */
    public static AdminDeleteFolderObjRefHistory ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY(UUID contributionIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolderObjRefHistory.ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY.call(
                contributionIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_folder_obj_ref_history</code> as a table.
     */
    public static AdminDeleteFolderObjRefHistory ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY(Field<UUID> contributionIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteFolderObjRefHistory.ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY.call(
                contributionIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_status</code>.
     */
    public final AdminDeleteStatus ADMIN_DELETE_STATUS = AdminDeleteStatus.ADMIN_DELETE_STATUS;

    /**
     * Call <code>ehr.admin_delete_status</code>.
     */
    public static Result<AdminDeleteStatusRecord> ADMIN_DELETE_STATUS(Configuration configuration, UUID statusIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteStatus.ADMIN_DELETE_STATUS.call(statusIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_status</code> as a table.
     */
    public static AdminDeleteStatus ADMIN_DELETE_STATUS(UUID statusIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteStatus.ADMIN_DELETE_STATUS.call(statusIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_status</code> as a table.
     */
    public static AdminDeleteStatus ADMIN_DELETE_STATUS(Field<UUID> statusIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteStatus.ADMIN_DELETE_STATUS.call(statusIdInput);
    }

    /**
     * The table <code>ehr.admin_delete_status_history</code>.
     */
    public final AdminDeleteStatusHistory ADMIN_DELETE_STATUS_HISTORY =
            AdminDeleteStatusHistory.ADMIN_DELETE_STATUS_HISTORY;

    /**
     * Call <code>ehr.admin_delete_status_history</code>.
     */
    public static Result<AdminDeleteStatusHistoryRecord> ADMIN_DELETE_STATUS_HISTORY(
            Configuration configuration, UUID statusIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminDeleteStatusHistory.ADMIN_DELETE_STATUS_HISTORY.call(
                        statusIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_delete_status_history</code> as a table.
     */
    public static AdminDeleteStatusHistory ADMIN_DELETE_STATUS_HISTORY(UUID statusIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteStatusHistory.ADMIN_DELETE_STATUS_HISTORY.call(statusIdInput);
    }

    /**
     * Get <code>ehr.admin_delete_status_history</code> as a table.
     */
    public static AdminDeleteStatusHistory ADMIN_DELETE_STATUS_HISTORY(Field<UUID> statusIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminDeleteStatusHistory.ADMIN_DELETE_STATUS_HISTORY.call(statusIdInput);
    }

    /**
     * The table <code>ehr.admin_get_linked_compositions</code>.
     */
    public final AdminGetLinkedCompositions ADMIN_GET_LINKED_COMPOSITIONS =
            AdminGetLinkedCompositions.ADMIN_GET_LINKED_COMPOSITIONS;

    /**
     * Call <code>ehr.admin_get_linked_compositions</code>.
     */
    public static Result<AdminGetLinkedCompositionsRecord> ADMIN_GET_LINKED_COMPOSITIONS(
            Configuration configuration, UUID ehrIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositions.ADMIN_GET_LINKED_COMPOSITIONS.call(
                        ehrIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_get_linked_compositions</code> as a table.
     */
    public static AdminGetLinkedCompositions ADMIN_GET_LINKED_COMPOSITIONS(UUID ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositions.ADMIN_GET_LINKED_COMPOSITIONS.call(ehrIdInput);
    }

    /**
     * Get <code>ehr.admin_get_linked_compositions</code> as a table.
     */
    public static AdminGetLinkedCompositions ADMIN_GET_LINKED_COMPOSITIONS(Field<UUID> ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositions.ADMIN_GET_LINKED_COMPOSITIONS.call(ehrIdInput);
    }

    /**
     * The table <code>ehr.admin_get_linked_compositions_for_contrib</code>.
     */
    public final AdminGetLinkedCompositionsForContrib ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB =
            AdminGetLinkedCompositionsForContrib.ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB;

    /**
     * Call <code>ehr.admin_get_linked_compositions_for_contrib</code>.
     */
    public static Result<AdminGetLinkedCompositionsForContribRecord> ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB(
            Configuration configuration, UUID contribIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositionsForContrib
                        .ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB
                        .call(contribIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_get_linked_compositions_for_contrib</code> as a
     * table.
     */
    public static AdminGetLinkedCompositionsForContrib ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB(UUID contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositionsForContrib.ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB
                .call(contribIdInput);
    }

    /**
     * Get <code>ehr.admin_get_linked_compositions_for_contrib</code> as a
     * table.
     */
    public static AdminGetLinkedCompositionsForContrib ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB(
            Field<UUID> contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedCompositionsForContrib.ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB
                .call(contribIdInput);
    }

    /**
     * The table <code>ehr.admin_get_linked_contributions</code>.
     */
    public final AdminGetLinkedContributions ADMIN_GET_LINKED_CONTRIBUTIONS =
            AdminGetLinkedContributions.ADMIN_GET_LINKED_CONTRIBUTIONS;

    /**
     * Call <code>ehr.admin_get_linked_contributions</code>.
     */
    public static Result<AdminGetLinkedContributionsRecord> ADMIN_GET_LINKED_CONTRIBUTIONS(
            Configuration configuration, UUID ehrIdInput) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminGetLinkedContributions.ADMIN_GET_LINKED_CONTRIBUTIONS.call(
                        ehrIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_get_linked_contributions</code> as a table.
     */
    public static AdminGetLinkedContributions ADMIN_GET_LINKED_CONTRIBUTIONS(UUID ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedContributions.ADMIN_GET_LINKED_CONTRIBUTIONS.call(ehrIdInput);
    }

    /**
     * Get <code>ehr.admin_get_linked_contributions</code> as a table.
     */
    public static AdminGetLinkedContributions ADMIN_GET_LINKED_CONTRIBUTIONS(Field<UUID> ehrIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedContributions.ADMIN_GET_LINKED_CONTRIBUTIONS.call(ehrIdInput);
    }

    /**
     * The table <code>ehr.admin_get_linked_status_for_contrib</code>.
     */
    public final AdminGetLinkedStatusForContrib ADMIN_GET_LINKED_STATUS_FOR_CONTRIB =
            AdminGetLinkedStatusForContrib.ADMIN_GET_LINKED_STATUS_FOR_CONTRIB;

    /**
     * Call <code>ehr.admin_get_linked_status_for_contrib</code>.
     */
    public static Result<AdminGetLinkedStatusForContribRecord> ADMIN_GET_LINKED_STATUS_FOR_CONTRIB(
            Configuration configuration, UUID contribIdInput) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.AdminGetLinkedStatusForContrib.ADMIN_GET_LINKED_STATUS_FOR_CONTRIB
                                .call(contribIdInput))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_get_linked_status_for_contrib</code> as a table.
     */
    public static AdminGetLinkedStatusForContrib ADMIN_GET_LINKED_STATUS_FOR_CONTRIB(UUID contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedStatusForContrib.ADMIN_GET_LINKED_STATUS_FOR_CONTRIB.call(
                contribIdInput);
    }

    /**
     * Get <code>ehr.admin_get_linked_status_for_contrib</code> as a table.
     */
    public static AdminGetLinkedStatusForContrib ADMIN_GET_LINKED_STATUS_FOR_CONTRIB(Field<UUID> contribIdInput) {
        return org.ehrbase.jooq.pg.tables.AdminGetLinkedStatusForContrib.ADMIN_GET_LINKED_STATUS_FOR_CONTRIB.call(
                contribIdInput);
    }

    /**
     * The table <code>ehr.admin_get_template_usage</code>.
     */
    public final AdminGetTemplateUsage ADMIN_GET_TEMPLATE_USAGE = AdminGetTemplateUsage.ADMIN_GET_TEMPLATE_USAGE;

    /**
     * Call <code>ehr.admin_get_template_usage</code>.
     */
    public static Result<AdminGetTemplateUsageRecord> ADMIN_GET_TEMPLATE_USAGE(
            Configuration configuration, String targetId) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.AdminGetTemplateUsage.ADMIN_GET_TEMPLATE_USAGE.call(targetId))
                .fetch();
    }

    /**
     * Get <code>ehr.admin_get_template_usage</code> as a table.
     */
    public static AdminGetTemplateUsage ADMIN_GET_TEMPLATE_USAGE(String targetId) {
        return org.ehrbase.jooq.pg.tables.AdminGetTemplateUsage.ADMIN_GET_TEMPLATE_USAGE.call(targetId);
    }

    /**
     * Get <code>ehr.admin_get_template_usage</code> as a table.
     */
    public static AdminGetTemplateUsage ADMIN_GET_TEMPLATE_USAGE(Field<String> targetId) {
        return org.ehrbase.jooq.pg.tables.AdminGetTemplateUsage.ADMIN_GET_TEMPLATE_USAGE.call(targetId);
    }

    /**
     * The table <code>ehr.attestation</code>.
     */
    public final Attestation ATTESTATION = Attestation.ATTESTATION;

    /**
     * The table <code>ehr.attestation_ref</code>.
     */
    public final AttestationRef ATTESTATION_REF = AttestationRef.ATTESTATION_REF;

    /**
     * The table <code>ehr.attested_view</code>.
     */
    public final AttestedView ATTESTED_VIEW = AttestedView.ATTESTED_VIEW;

    /**
     * The table <code>ehr.audit_details</code>.
     */
    public final AuditDetails AUDIT_DETAILS = AuditDetails.AUDIT_DETAILS;

    /**
     * The table <code>ehr.compo_xref</code>.
     */
    public final CompoXref COMPO_XREF = CompoXref.COMPO_XREF;

    /**
     * Composition table
     */
    public final Composition COMPOSITION = Composition.COMPOSITION;

    /**
     * The table <code>ehr.composition_history</code>.
     */
    public final CompositionHistory COMPOSITION_HISTORY = CompositionHistory.COMPOSITION_HISTORY;

    /**
     * openEHR common concepts (e.g. terminology) used in the system
     */
    public final Concept CONCEPT = Concept.CONCEPT;

    /**
     * Contribution table, compositions reference this table
     */
    public final Contribution CONTRIBUTION = Contribution.CONTRIBUTION;

    /**
     * EHR itself
     */
    public final org.ehrbase.jooq.pg.tables.Ehr EHR_ = org.ehrbase.jooq.pg.tables.Ehr.EHR_;

    /**
     * this table hold the actual archetyped data values (fromBinder a template)
     */
    public final Entry ENTRY = Entry.ENTRY;

    /**
     * The table <code>ehr.entry_history</code>.
     */
    public final EntryHistory ENTRY_HISTORY = EntryHistory.ENTRY_HISTORY;

    /**
     * defines the context of an event (time, who, where... see openEHR IM 5.2
     */
    public final EventContext EVENT_CONTEXT = EventContext.EVENT_CONTEXT;

    /**
     * The table <code>ehr.event_context_history</code>.
     */
    public final EventContextHistory EVENT_CONTEXT_HISTORY = EventContextHistory.EVENT_CONTEXT_HISTORY;

    /**
     * The table <code>ehr.flyway_schema_history</code>.
     */
    public final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY = FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

    /**
     * The table <code>ehr.folder</code>.
     */
    public final Folder FOLDER = Folder.FOLDER;

    /**
     * The table <code>ehr.folder_hierarchy</code>.
     */
    public final FolderHierarchy FOLDER_HIERARCHY = FolderHierarchy.FOLDER_HIERARCHY;

    /**
     * The table <code>ehr.folder_hierarchy_history</code>.
     */
    public final FolderHierarchyHistory FOLDER_HIERARCHY_HISTORY = FolderHierarchyHistory.FOLDER_HIERARCHY_HISTORY;

    /**
     * The table <code>ehr.folder_history</code>.
     */
    public final FolderHistory FOLDER_HISTORY = FolderHistory.FOLDER_HISTORY;

    /**
     * The table <code>ehr.folder_items</code>.
     */
    public final FolderItems FOLDER_ITEMS = FolderItems.FOLDER_ITEMS;

    /**
     * The table <code>ehr.folder_items_history</code>.
     */
    public final FolderItemsHistory FOLDER_ITEMS_HISTORY = FolderItemsHistory.FOLDER_ITEMS_HISTORY;

    /**
     * The table <code>ehr.heading</code>.
     */
    public final Heading HEADING = Heading.HEADING;

    /**
     * specifies an identifier for a party identified, more than one identifier
     * is possible
     */
    public final Identifier IDENTIFIER = Identifier.IDENTIFIER;

    /**
     * The table <code>ehr.jsonb_array_elements</code>.
     */
    public final JsonbArrayElements JSONB_ARRAY_ELEMENTS = JsonbArrayElements.JSONB_ARRAY_ELEMENTS;

    /**
     * Call <code>ehr.jsonb_array_elements</code>.
     */
    public static Result<JsonbArrayElementsRecord> JSONB_ARRAY_ELEMENTS(Configuration configuration, JSONB jsonbVal) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.JsonbArrayElements.JSONB_ARRAY_ELEMENTS.call(jsonbVal))
                .fetch();
    }

    /**
     * Get <code>ehr.jsonb_array_elements</code> as a table.
     */
    public static JsonbArrayElements JSONB_ARRAY_ELEMENTS(JSONB jsonbVal) {
        return org.ehrbase.jooq.pg.tables.JsonbArrayElements.JSONB_ARRAY_ELEMENTS.call(jsonbVal);
    }

    /**
     * Get <code>ehr.jsonb_array_elements</code> as a table.
     */
    public static JsonbArrayElements JSONB_ARRAY_ELEMENTS(Field<JSONB> jsonbVal) {
        return org.ehrbase.jooq.pg.tables.JsonbArrayElements.JSONB_ARRAY_ELEMENTS.call(jsonbVal);
    }

    /**
     * ISO 639-1 language codeset
     */
    public final Language LANGUAGE = Language.LANGUAGE;

    /**
     * *implements
     * https://specifications.openehr.org/releases/RM/Release-1.0.3/support.html#_object_ref_class*id
     * implemented as native UID from postgres instead of a separate table.
     */
    public final ObjectRef OBJECT_REF = ObjectRef.OBJECT_REF;

    /**
     * *implements
     * https://specifications.openehr.org/releases/RM/Release-1.0.3/support.html#_object_ref_history_class*id
     * implemented as native UID from postgres instead of a separate table.
     */
    public final ObjectRefHistory OBJECT_REF_HISTORY = ObjectRefHistory.OBJECT_REF_HISTORY;

    /**
     * define a participating party for an event f.ex.
     */
    public final Participation PARTICIPATION = Participation.PARTICIPATION;

    /**
     * The table <code>ehr.participation_history</code>.
     */
    public final ParticipationHistory PARTICIPATION_HISTORY = ParticipationHistory.PARTICIPATION_HISTORY;

    /**
     * The table <code>ehr.party_identified</code>.
     */
    public final PartyIdentified PARTY_IDENTIFIED = PartyIdentified.PARTY_IDENTIFIED;

    /**
     * The table <code>ehr.party_usage_identification</code>.
     */
    public final PartyUsageIdentification PARTY_USAGE_IDENTIFICATION =
            PartyUsageIdentification.PARTY_USAGE_IDENTIFICATION;

    /**
     * Call <code>ehr.party_usage_identification</code>.
     */
    public static Result<PartyUsageIdentificationRecord> PARTY_USAGE_IDENTIFICATION(
            Configuration configuration, UUID partyUuid) {
        return configuration
                .dsl()
                .selectFrom(
                        org.ehrbase.jooq.pg.tables.PartyUsageIdentification.PARTY_USAGE_IDENTIFICATION.call(partyUuid))
                .fetch();
    }

    /**
     * Get <code>ehr.party_usage_identification</code> as a table.
     */
    public static PartyUsageIdentification PARTY_USAGE_IDENTIFICATION(UUID partyUuid) {
        return org.ehrbase.jooq.pg.tables.PartyUsageIdentification.PARTY_USAGE_IDENTIFICATION.call(partyUuid);
    }

    /**
     * Get <code>ehr.party_usage_identification</code> as a table.
     */
    public static PartyUsageIdentification PARTY_USAGE_IDENTIFICATION(Field<UUID> partyUuid) {
        return org.ehrbase.jooq.pg.tables.PartyUsageIdentification.PARTY_USAGE_IDENTIFICATION.call(partyUuid);
    }

    /**
     * key value store for plugin sub system
     */
    public final Plugin PLUGIN = Plugin.PLUGIN;

    /**
     * The table <code>ehr.session_log</code>.
     */
    public final SessionLog SESSION_LOG = SessionLog.SESSION_LOG;

    /**
     * specifies an ehr modality and ownership (patient)
     */
    public final Status STATUS = Status.STATUS;

    /**
     * The table <code>ehr.status_history</code>.
     */
    public final StatusHistory STATUS_HISTORY = StatusHistory.STATUS_HISTORY;

    /**
     * The table <code>ehr.stored_query</code>.
     */
    public final StoredQuery STORED_QUERY = StoredQuery.STORED_QUERY;

    /**
     * system table for reference
     */
    public final System SYSTEM = System.SYSTEM;

    /**
     * The table <code>ehr.template_store</code>.
     */
    public final TemplateStore TEMPLATE_STORE = TemplateStore.TEMPLATE_STORE;

    /**
     * openEHR identified terminology provider
     */
    public final TerminologyProvider TERMINOLOGY_PROVIDER = TerminologyProvider.TERMINOLOGY_PROVIDER;

    /**
     * ISO 3166-1 countries codeset
     */
    public final Territory TERRITORY = Territory.TERRITORY;

    /**
     * The table <code>ehr.xjsonb_array_elements</code>.
     */
    public final XjsonbArrayElements XJSONB_ARRAY_ELEMENTS = XjsonbArrayElements.XJSONB_ARRAY_ELEMENTS;

    /**
     * Call <code>ehr.xjsonb_array_elements</code>.
     */
    public static Result<XjsonbArrayElementsRecord> XJSONB_ARRAY_ELEMENTS(Configuration configuration, JSONB entry) {
        return configuration
                .dsl()
                .selectFrom(org.ehrbase.jooq.pg.tables.XjsonbArrayElements.XJSONB_ARRAY_ELEMENTS.call(entry))
                .fetch();
    }

    /**
     * Get <code>ehr.xjsonb_array_elements</code> as a table.
     */
    public static XjsonbArrayElements XJSONB_ARRAY_ELEMENTS(JSONB entry) {
        return org.ehrbase.jooq.pg.tables.XjsonbArrayElements.XJSONB_ARRAY_ELEMENTS.call(entry);
    }

    /**
     * Get <code>ehr.xjsonb_array_elements</code> as a table.
     */
    public static XjsonbArrayElements XJSONB_ARRAY_ELEMENTS(Field<JSONB> entry) {
        return org.ehrbase.jooq.pg.tables.XjsonbArrayElements.XJSONB_ARRAY_ELEMENTS.call(entry);
    }

    /**
     * No further instances allowed
     */
    private Ehr() {
        super("ehr", null);
    }

    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
                Access.ACCESS,
                AdminDeleteAttestation.ADMIN_DELETE_ATTESTATION,
                AdminDeleteAudit.ADMIN_DELETE_AUDIT,
                AdminDeleteComposition.ADMIN_DELETE_COMPOSITION,
                AdminDeleteCompositionHistory.ADMIN_DELETE_COMPOSITION_HISTORY,
                AdminDeleteContribution.ADMIN_DELETE_CONTRIBUTION,
                AdminDeleteEhr.ADMIN_DELETE_EHR,
                AdminDeleteEhrFull.ADMIN_DELETE_EHR_FULL,
                AdminDeleteEhrHistory.ADMIN_DELETE_EHR_HISTORY,
                AdminDeleteEventContextForCompo.ADMIN_DELETE_EVENT_CONTEXT_FOR_COMPO,
                AdminDeleteFolder.ADMIN_DELETE_FOLDER,
                AdminDeleteFolderHistory.ADMIN_DELETE_FOLDER_HISTORY,
                AdminDeleteFolderObjRefHistory.ADMIN_DELETE_FOLDER_OBJ_REF_HISTORY,
                AdminDeleteStatus.ADMIN_DELETE_STATUS,
                AdminDeleteStatusHistory.ADMIN_DELETE_STATUS_HISTORY,
                AdminGetLinkedCompositions.ADMIN_GET_LINKED_COMPOSITIONS,
                AdminGetLinkedCompositionsForContrib.ADMIN_GET_LINKED_COMPOSITIONS_FOR_CONTRIB,
                AdminGetLinkedContributions.ADMIN_GET_LINKED_CONTRIBUTIONS,
                AdminGetLinkedStatusForContrib.ADMIN_GET_LINKED_STATUS_FOR_CONTRIB,
                AdminGetTemplateUsage.ADMIN_GET_TEMPLATE_USAGE,
                Attestation.ATTESTATION,
                AttestationRef.ATTESTATION_REF,
                AttestedView.ATTESTED_VIEW,
                AuditDetails.AUDIT_DETAILS,
                CompoXref.COMPO_XREF,
                Composition.COMPOSITION,
                CompositionHistory.COMPOSITION_HISTORY,
                Concept.CONCEPT,
                Contribution.CONTRIBUTION,
                org.ehrbase.jooq.pg.tables.Ehr.EHR_,
                Entry.ENTRY,
                EntryHistory.ENTRY_HISTORY,
                EventContext.EVENT_CONTEXT,
                EventContextHistory.EVENT_CONTEXT_HISTORY,
                FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
                Folder.FOLDER,
                FolderHierarchy.FOLDER_HIERARCHY,
                FolderHierarchyHistory.FOLDER_HIERARCHY_HISTORY,
                FolderHistory.FOLDER_HISTORY,
                FolderItems.FOLDER_ITEMS,
                FolderItemsHistory.FOLDER_ITEMS_HISTORY,
                Heading.HEADING,
                Identifier.IDENTIFIER,
                JsonbArrayElements.JSONB_ARRAY_ELEMENTS,
                Language.LANGUAGE,
                ObjectRef.OBJECT_REF,
                ObjectRefHistory.OBJECT_REF_HISTORY,
                Participation.PARTICIPATION,
                ParticipationHistory.PARTICIPATION_HISTORY,
                PartyIdentified.PARTY_IDENTIFIED,
                PartyUsageIdentification.PARTY_USAGE_IDENTIFICATION,
                Plugin.PLUGIN,
                SessionLog.SESSION_LOG,
                Status.STATUS,
                StatusHistory.STATUS_HISTORY,
                StoredQuery.STORED_QUERY,
                System.SYSTEM,
                TemplateStore.TEMPLATE_STORE,
                TerminologyProvider.TERMINOLOGY_PROVIDER,
                Territory.TERRITORY,
                XjsonbArrayElements.XJSONB_ARRAY_ELEMENTS);
    }

    @Override
    public final List<UDT<?>> getUDTs() {
        return Arrays.asList(CodePhrase.CODE_PHRASE, DvCodedText.DV_CODED_TEXT);
    }
}
