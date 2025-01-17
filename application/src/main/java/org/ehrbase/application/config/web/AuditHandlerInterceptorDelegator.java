/*
 * Copyright (c) 2023 vitasystems GmbH and Hannover Medical School.
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
package org.ehrbase.application.config.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ehrbase.api.audit.interceptor.AuditInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuditHandlerInterceptorDelegator implements HandlerInterceptor {
    private final AuditInterceptor interceptor;

    public AuditHandlerInterceptorDelegator(AuditInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return interceptor.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex)
            throws Exception {
        interceptor.afterCompletion(request, response, handler, ex);
    }
}
