/*
 * Modifications copyright (C) 2019 Christian Chevalley, Vitasystems GmbH and Hannover Medical School

 * This file is part of Project EHRbase

 * Copyright (c) 2015 Christian Chevalley
 * This file is part of Project Ethercis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ehrbase.aql.sql.binding;

import org.apache.commons.lang3.StringUtils;
import org.ehrbase.aql.definition.I_VariableDefinition;
import org.ehrbase.aql.definition.LateralJoinDefinition;
import org.ehrbase.aql.definition.LateralVariable;
import org.ehrbase.aql.definition.VariableDefinition;
import org.ehrbase.aql.sql.PathResolver;
import org.ehrbase.aql.sql.queryimpl.*;
import org.ehrbase.aql.sql.queryimpl.attribute.JoinSetup;
import org.ehrbase.aql.sql.queryimpl.value_field.ISODateTime;
import org.ehrbase.dao.access.interfaces.I_DomainAccess;
import org.ehrbase.serialisation.dbencoding.CompositionSerializer;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;

import static org.ehrbase.aql.sql.queryimpl.IterativeNodeConstants.ENV_AQL_USE_JSQUERY;
import static org.ehrbase.rest.openehr.OpenehrQueryController.EHR_ID_VALUE;

/**
 * Bind the abstract WHERE clause parameters into a SQL expression
 * Created by christian on 5/20/2016.
 */
//ignore cognitive complexity flag as this code as evolved historically and adjusted depending on use cases
@SuppressWarnings({"java:S3776","java:S135"})
public class WhereBinder {

    private static final String VALUETYPE_EXPR_VALUE = "/value,value";
    public static final String EXISTS = "EXISTS";
    public static final String MATCHES = "MATCHES";
    public static final String NOT = "NOT";
    public static final String IS = "IS";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    public static final String NULL = "NULL";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String DISTINCT = "DISTINCT";
    public static final String FROM = "FROM";
    public static final String BETWEEN = "BETWEEN";

    //from AQL grammar
    private static final Set<String> sqloperators = new HashSet<>(Arrays.asList(
            "=", "!=", ">", ">=", "<", "<=", MATCHES, EXISTS, NOT, IS, TRUE, FALSE, NULL, UNKNOWN, DISTINCT, FROM, BETWEEN, "(", ")", "{", "}"));
    public static final String COMPOSITION = "COMPOSITION";
    public static final String CONTENT = "content";
    public static final String EHR = "EHR";
    public static final String OR = "OR";
    public static final String XOR = "XOR";
    public static final String AND = "AND";
    public static final String IN = "IN";
    public static final String ANY = "ANY";
    public static final String SOME = "SOME";
    public static final String ALL = "ALL";
    private final I_DomainAccess domainAccess;

    private CompositionAttributeQuery compositionAttributeQuery;
    private final List<Object> whereClause;
    private PathResolver pathResolver;
    private boolean isWholeComposition = false;
    private String compositionName = null;
    private String sqlConditionalFunctionalOperatorRegexp = "(?i)(like|ilike|substr|in|not in)"; //list of subquery and operators
    private boolean requiresJSQueryClosure = false;
    private boolean isFollowedBySQLConditionalOperator = false;
    private EhrIdCondition explicitEhrIdCondition = null;

    public WhereBinder(I_DomainAccess domainAccess, CompositionAttributeQuery compositionAttributeQuery, List<Object> whereClause, PathResolver pathResolver) {
        this.compositionAttributeQuery = compositionAttributeQuery;
        this.whereClause = whereClause;
        this.pathResolver = pathResolver;
        this.domainAccess = domainAccess;
    }

    private TaggedStringBuilder encodeWhereVariable(int whereCursor, MultiFieldsMap multiFieldsMap, I_VariableDefinition variableDefinition, boolean forceSQL, String compositionName) throws UnknownVariableException {
        String identifier = variableDefinition.getIdentifier();
        String className = pathResolver.classNameOf(identifier);
        if (className == null)
            throw new IllegalArgumentException("Could not bind identifier in WHERE clause:'" + identifier + "'");

        Field<?> field = multiFieldsMap.get(variableDefinition.getIdentifier(), variableDefinition.getPath()).getQualifiedFieldOrLast(whereCursor).getSQLField();

        //EHR-327: if force SQL is set to true via environment, jsquery extension is not required
        //this allows to deploy on AWS since jsquery is not supported by this provider
        Boolean usePgExtensions;
        if (System.getenv(ENV_AQL_USE_JSQUERY) != null)
            usePgExtensions = Boolean.parseBoolean(System.getenv(ENV_AQL_USE_JSQUERY));
        else if (domainAccess.getServerConfig().getUseJsQuery() != null)
            usePgExtensions = domainAccess.getServerConfig().getUseJsQuery();
        else
            usePgExtensions = false;

        if (forceSQL || Boolean.FALSE.equals(usePgExtensions)) {

            if (field == null)
                return null;
            return new TaggedStringBuilder(field.toString(), I_TaggedStringBuilder.TagField.SQLQUERY);

        } else {
            switch (className) {
                case COMPOSITION:
                    if (variableDefinition.getPath().startsWith(CONTENT)) {
                        TaggedStringBuilder taggedStringBuilder = new TaggedStringBuilder(field.toString(), I_TaggedStringBuilder.TagField.JSQUERY);
                        if (compositionName != null && taggedStringBuilder.startWith(CompositionSerializer.TAG_COMPOSITION)) {
                            //add the composition name into the composition predicate
                            taggedStringBuilder.replace("]", " and name/value='" + compositionName + "']");
                        }
                        return taggedStringBuilder;
                    }
                    break;
                case EHR:
                    if (field == null)
                        return null;
                    isFollowedBySQLConditionalOperator = true;
                    return new TaggedStringBuilder(field.toString(), I_TaggedStringBuilder.TagField.SQLQUERY);

                default:
                    if (compositionAttributeQuery.isCompositionAttributeItemStructure(multiFieldsMap.get(variableDefinition.getIdentifier(), variableDefinition.getPath()).getTemplateId(), identifier)){
                        return new TaggedStringBuilder(field.toString(), I_TaggedStringBuilder.TagField.SQLQUERY);
                    }
                    else {
                        return new TaggedStringBuilder(field.toString(), I_TaggedStringBuilder.TagField.JSQUERY);
                    }
            }
            throw new IllegalStateException("Unhandled class name:"+className);
        }
    }

    private TaggedStringBuilder buildWhereCondition(int whereCursor, MultiFieldsMap multiFieldsMap, TaggedStringBuilder taggedBuffer, List<Object> item) throws UnknownVariableException {
        for (Object part : item) {
            if (part instanceof String)
                taggedBuffer.append((String) part);
            else if (part instanceof VariableDefinition) {
                //substitute the identifier
                TaggedStringBuilder taggedStringBuilder = encodeWhereVariable(whereCursor, multiFieldsMap, (VariableDefinition) part, false, null);
                if (taggedStringBuilder != null) {
                    taggedBuffer.append(taggedStringBuilder.toString());
                    taggedBuffer.setTagField(taggedStringBuilder.getTagField());
                }
            } else if (part instanceof List) {
                TaggedStringBuilder taggedStringBuilder = buildWhereCondition(whereCursor, multiFieldsMap, taggedBuffer, (List) part);
                taggedBuffer.append(taggedStringBuilder.toString());
                taggedBuffer.setTagField(taggedStringBuilder.getTagField());
            }
        }
        return taggedBuffer;
    }

    public Condition bind(JoinSetup joinSetup, String templateId, int whereCursor, MultiFieldsMap multiWhereFieldsMap, MultiFieldsMap multiSelectFieldsMap) throws UnknownVariableException {

        boolean unresolvedVariable = false;

        if (whereClause.isEmpty())
            return null;

        TaggedStringBuilder taggedBuffer = new TaggedStringBuilder();

        //work on a copy since Exist is destructive
        List<Object> whereItems = new ArrayList<>(whereClause);
        boolean notExists = false;
        boolean inSubqueryOperator = false;

        for (int cursor = 0; cursor < whereItems.size(); cursor++) {
            Object item = whereItems.get(cursor);
            if (item instanceof String) {
                switch (((String) item).trim().toUpperCase()) {
                    case OR:
                    case XOR:
                    case AND:
                        taggedBuffer = new WhereJsQueryExpression(taggedBuffer, requiresJSQueryClosure, isFollowedBySQLConditionalOperator).closure();
                        taggedBuffer.append(" " + item + " ");
                        break;

                    case NOT:
                        //check if precedes 'EXISTS'
                        if (whereItems.get(cursor + 1).toString().equalsIgnoreCase(EXISTS))
                            notExists = true;
                        else {
                            taggedBuffer = new WhereJsQueryExpression(taggedBuffer, requiresJSQueryClosure, isFollowedBySQLConditionalOperator).closure();
                            taggedBuffer.append(" " + item + " ");
                        }
                        break;

                    case IN: case ANY: case SOME: case ALL:
                        if (((String) item).trim().toUpperCase().matches("ANY|SOME|ALL"))
                            inSubqueryOperator = true;
                        taggedBuffer.append((String) item);
                        break;

                    case EXISTS:
                        //add the comparison to null after the variable expression
                        whereItems.add(cursor + 2, notExists ? "IS " : "IS NOT ");
                        whereItems.add(cursor + 3, "NULL");
                        notExists = false;
                        break;

                    default:
                        ISODateTime isoDateTime = new ISODateTime(((String) item).replace("'", ""));
                        if (isoDateTime.isValidDateTimeExpression()) {
                            long timestamp = isoDateTime.toTimeStamp()/1000; //this to align with epoch_offset in the DB, ignore ms
                            int lastValuePos = taggedBuffer.lastIndexOf(VALUETYPE_EXPR_VALUE);
                            if (lastValuePos > 0) {
                                taggedBuffer.replaceLast(VALUETYPE_EXPR_VALUE, "/value,epoch_offset");
                            }
                            isFollowedBySQLConditionalOperator = true;
                            item = hackItem(taggedBuffer, Long.toString(timestamp), "numeric");
                            taggedBuffer.append((String) item);
                        } else {
                            item = hackItem(taggedBuffer, (String) item, null);
                            taggedBuffer.append((String) item);
                        }
                        break;

                }
            } else if (item instanceof Long) {
                item = hackItem(taggedBuffer, item.toString(), null);
                taggedBuffer.append(item.toString());
            } else if (item instanceof I_VariableDefinition) {
                //look ahead and check if followed by a sql operator
                TaggedStringBuilder taggedStringBuilder = new TaggedStringBuilder();
                if (isFollowedBySQLConditionalOperator(cursor)) {
                    TaggedStringBuilder encodedVar = encodeWhereVariable(whereCursor, multiWhereFieldsMap, (I_VariableDefinition) item, true, null);
                    String expanded = expandForLateral(templateId, encodedVar, (I_VariableDefinition)item, multiSelectFieldsMap );
                    if (StringUtils.isNotBlank(expanded))
                        taggedStringBuilder.append(expanded);
                    else {
                        unresolvedVariable = true;
                        break;
                    }
                } else {
                    if (((I_VariableDefinition) item).getPath() != null && isWholeComposition) {
                        //assume a composition
                        //look ahead for name/value condition (used to produce the composition root)
                        if (compositionName == null)
                            compositionName = compositionNameValue(((I_VariableDefinition) item).getIdentifier());

                        if (compositionName != null) {
                            taggedStringBuilder = encodeWhereVariable(whereCursor, multiWhereFieldsMap, (I_VariableDefinition) item, false, compositionName);
                        } else
                            throw new IllegalArgumentException("A composition name/value is required to resolve where statement when querying for a whole composition");
                    } else {
                        //if the path contains node predicate expression uses a SQL syntax instead of jsquery
                        if (new VariablePath(((I_VariableDefinition) item).getPath()).hasPredicate()) {
                            String expanded = expandForLateral(templateId, encodeWhereVariable(whereCursor, multiWhereFieldsMap, (I_VariableDefinition) item, true, null), (I_VariableDefinition)item, multiSelectFieldsMap );
                            if (StringUtils.isNotBlank(expanded)) {
                                taggedStringBuilder.append(encodeForSubquery(expanded, inSubqueryOperator));
                                inSubqueryOperator = false;
                            }
                            else {
                                unresolvedVariable = true;
                                break;
                            }
                            isFollowedBySQLConditionalOperator = true;
                            requiresJSQueryClosure = false;
                        } else {
                            //prefix the ehr_id/value translation with matching filters for the current joins
                            DistributedFilter distributedFilter = new DistributedFilter(joinSetup, whereItems);
                            if (((I_VariableDefinition) item).getPath().equals(EHR_ID_VALUE)) {
                                distributedFilter.addToSql(taggedStringBuilder, whereItems, cursor);
                                explicitEhrIdCondition = distributedFilter.getEhrIdCondition();
                            }
                            String expanded = expandForLateral(templateId, encodeWhereVariable(whereCursor, multiWhereFieldsMap, (I_VariableDefinition) item, true, null), (I_VariableDefinition)item, multiSelectFieldsMap );
                            if (StringUtils.isNotBlank(expanded)) {
                                taggedStringBuilder.append(encodeForSubquery(expanded, inSubqueryOperator));
                                inSubqueryOperator = false;
                            }
                            else {
                                unresolvedVariable = true;
                                break;
                            }
                        }
                    }
                }

                if (taggedStringBuilder != null) {
                    taggedBuffer.append(taggedStringBuilder.toString());
                    taggedBuffer.setTagField(taggedStringBuilder.getTagField());
                }
            } else if (item instanceof List) {
                TaggedStringBuilder taggedStringBuilder = buildWhereCondition(whereCursor, multiWhereFieldsMap, taggedBuffer, (List) item);
                taggedBuffer.append(taggedStringBuilder.toString());
                taggedBuffer.setTagField(taggedStringBuilder.getTagField());
            }

        }

        if (!unresolvedVariable) {
            taggedBuffer = new WhereJsQueryExpression(taggedBuffer, requiresJSQueryClosure, isFollowedBySQLConditionalOperator).closure(); //termination

            return DSL.condition(taggedBuffer.toString());
        }
        else
            return DSL.falseCondition();
    }



    private String encodeForSubquery(String sqlExpression, boolean inSubqueryOperator){
        if (inSubqueryOperator)
            return "(SELECT " + sqlExpression+")";
        else
            return sqlExpression;
    }

    //look ahead for a SQL operator
    private boolean isFollowedBySQLConditionalOperator(int cursor) {
        if (cursor < whereClause.size() - 1) {
            Object nextToken = whereClause.get(cursor + 1);
            if (nextToken instanceof String && ((String) nextToken).trim().matches(sqlConditionalFunctionalOperatorRegexp)) {
                isFollowedBySQLConditionalOperator = true;
                return true;
            }
        }
        isFollowedBySQLConditionalOperator = false;
        return false;
    }

    //look ahead for a SQL operator
    private String compositionNameValue(String symbol) {

        String token = null;
        int lcursor; //skip the current variable

        for (lcursor = 0; lcursor < whereClause.size() - 1; lcursor++) {
            if (whereClause.get(lcursor) instanceof VariableDefinition
                    && (((VariableDefinition) whereClause.get(lcursor)).getIdentifier().equals(symbol))
                    && (((VariableDefinition) whereClause.get(lcursor)).getPath().equals("name/value"))
            ) {
                Object nextToken = whereClause.get(lcursor + 1); //check operator
                if (nextToken instanceof String && !nextToken.equals("="))
                    throw new IllegalArgumentException("name/value for CompositionAttribute must be an equality");
                nextToken = whereClause.get(lcursor + 2);
                if (nextToken instanceof String) {
                    token = (String) nextToken;
                    break;
                }
            }
        }

        return token;
    }


    //do some temporary hacking for unsupported features
    private Object hackItem(TaggedStringBuilder taggedBuffer, String item, String castAs) {
        //this deals with post type casting required f.e. for date comparisons with epoch_offset
        if (castAs != null){
            if (isFollowedBySQLConditionalOperator) { //at the moment, only for epoch_offset
                int variableClosure = taggedBuffer.lastIndexOf("}'");
                if (variableClosure > 0){
                    int variableInitial = taggedBuffer.lastIndexOf("\"ehr\".\"entry\".\"entry\" #>>");
                    if (variableInitial >= 0 && variableInitial < variableClosure){
                        taggedBuffer.insert(variableClosure+"}'".length(), ")::"+castAs);
                        taggedBuffer.insert(variableInitial, "(");
                    }
                }
            }
            return item;
        }

        if (sqloperators.contains(item.toUpperCase()))
            return " "+item+" ";
        if (taggedBuffer.toString().contains(JoinBinder.COMPOSITION_JOIN) && item.contains("::"))
            return item.split("::")[0] + "'";
        if (requiresJSQueryClosure && !isFollowedBySQLConditionalOperator && taggedBuffer.indexOf("#") > 0 && item.contains("'")) { //conventionally double quote for jsquery
            return item.replace("'", "\"");
        }
        return item;
    }

    private String expandForCondition(TaggedStringBuilder taggedStringBuilder) {

        if (taggedStringBuilder == null)
            return null;

        //perform the condition query wrapping depending on the dialect jsquery or sql
        String wrapped;

        switch (taggedStringBuilder.getTagField()) {
            case JSQUERY:
                wrapped = JsonbEntryQuery.JSQUERY_COMPOSITION_OPEN + taggedStringBuilder.toString();
                requiresJSQueryClosure = true;
                break;
            case SQLQUERY:
                wrapped = taggedStringBuilder.toString();
                break;

            default:
                throw new IllegalArgumentException("Uninitialized tag passed in query expression");
        }

        return wrapped;
    }

    private String expandForLateral(String templateId, TaggedStringBuilder encodedVar, I_VariableDefinition variableDefinition, MultiFieldsMap multiSelectFieldsMap ){
        String expanded = expandForCondition(encodedVar);
        if (new SetReturningFunction(expanded).isUsed()){

            //check if this variable is already defined as a lateral join from the projection (SELECT)
            MultiFields selectFields = multiSelectFieldsMap.get(variableDefinition.getIdentifier(), variableDefinition.getPath());

            if (selectFields != null && selectFields.getVariableDefinition().getLateralJoinDefinitions(templateId) != null) {
                //TODO: get the matching lateral join... not the LAST!
                LateralJoinDefinition lateralJoinDefinition = reconciliateWithAliasedTable(expanded, selectFields.getVariableDefinition(), templateId);
                if (lateralJoinDefinition == null)
                   return null;
                variableDefinition.setLateralJoinTable(templateId, lateralJoinDefinition);
                //NB: white space at the end is required since the clause is built with a string builder and space(s) is important!
                variableDefinition.setAlias(new LateralVariable(lateralJoinDefinition.getTable().getName(),lateralJoinDefinition.getLateralVariable()).alias());
            }
            else
                new LateralJoins().create(templateId, encodedVar, variableDefinition, IQueryImpl.Clause.WHERE );

            expanded = variableDefinition.getAlias();
        }

        return expanded;
    }

    private LateralJoinDefinition reconciliateWithAliasedTable(String expanded, I_VariableDefinition variableDefinition, String templateId){

        Set<LateralJoinDefinition> definedLateralJoins = variableDefinition.getLateralJoinDefinitions(templateId);

        for (LateralJoinDefinition lateralJoinDefinition: definedLateralJoins){
            if (lateralJoinDefinition.getSqlExpression().replace("\n", "").replace(" ", "").
                    contains(expanded.substring(0, expanded.length() - 1).substring(1).replace("\n", "").replace(" ", "")))
                return lateralJoinDefinition;
        }

        return null;

    }

    public EhrIdCondition getExplicitEhrIdCondition() {
        return explicitEhrIdCondition;
    }
}
