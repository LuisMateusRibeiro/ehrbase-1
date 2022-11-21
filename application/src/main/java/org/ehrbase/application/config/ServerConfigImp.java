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
package org.ehrbase.application.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerConfigImp implements org.ehrbase.api.definitions.ServerConfig {

    @Min(1025)
    @Max(65536)
    private int port;

    private String nodename = "local.ehrbase.org";
    private AqlConfig aqlConfig;
    private boolean disableStrictValidation = false;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    @Override
    public String getAqlIterationSkipList() {
        return aqlConfig.getIgnoreIterativeNodeList();
    }

    @Override
    public Integer getAqlDepth() {
        return aqlConfig.getIterationScanDepth();
    }

    @Override
    public Boolean getUseJsQuery() {
        return aqlConfig.getUseJsQuery();
    }

    @Override
    public void setUseJsQuery(boolean b) {
        aqlConfig.setUseJsQuery(b);
    }

    public AqlConfig getAqlConfig() {
        return aqlConfig;
    }

    public void setAqlConfig(AqlConfig aqlConfig) {
        this.aqlConfig = aqlConfig;
    }

    public static class AqlConfig {

        private Boolean useJsQuery;
        private String ignoreIterativeNodeList;
        private Integer iterationScanDepth = 1;

        public Boolean getUseJsQuery() {
            return useJsQuery;
        }

        public String getIgnoreIterativeNodeList() {
            return ignoreIterativeNodeList;
        }

        public Integer getIterationScanDepth() {
            return iterationScanDepth;
        }

        public void setUseJsQuery(Boolean useJsQuery) {
            this.useJsQuery = useJsQuery;
        }

        public void setIgnoreIterativeNodeList(String ignoreIterativeNodeList) {
            this.ignoreIterativeNodeList = ignoreIterativeNodeList;
        }

        public void setIterationScanDepth(Integer iterationScanDepth) {
            this.iterationScanDepth = iterationScanDepth;
        }
    }

    @Override
    public boolean isDisableStrictValidation() {
        return disableStrictValidation;
    }

    public void setDisableStrictValidation(boolean disableStrictValidation) {
        this.disableStrictValidation = disableStrictValidation;
    }
}
