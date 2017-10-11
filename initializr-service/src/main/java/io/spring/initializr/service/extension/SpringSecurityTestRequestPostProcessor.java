/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;

import org.springframework.stereotype.Component;

/**
 * A {@link ProjectRequestPostProcessor} that automatically adds
 * {@code spring-security-test} when Spring Security is selected.
 *
 * @author Stephane Nicoll
 */
@Component
class SpringSecurityTestRequestPostProcessor implements ProjectRequestPostProcessor {

	private static final Version VERSION_1_3_0 = Version.parse("1.3.0.RELEASE");

	private final Dependency springSecurityTest;

	public SpringSecurityTestRequestPostProcessor() {
		this.springSecurityTest = Dependency.withId("spring-security-test",
				"org.springframework.security", "spring-security-test");
		this.springSecurityTest.setScope(Dependency.SCOPE_TEST);
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		if (hasSpringSecurity(request) && isAtLeastAfter(request, VERSION_1_3_0)) {
			request.getResolvedDependencies().add(this.springSecurityTest);
		}
	}

	private boolean hasSpringSecurity(ProjectRequest request) {
		return request.getResolvedDependencies().stream()
				.anyMatch(d -> "security".equals(d.getId())
						|| "security-reactive".equals(d.getId()));
	}

	private boolean isAtLeastAfter(ProjectRequest request, Version version) {
		Version requestVersion = Version.safeParse(request.getBootVersion());
		return version.compareTo(requestVersion) <= 0;
	}

}
