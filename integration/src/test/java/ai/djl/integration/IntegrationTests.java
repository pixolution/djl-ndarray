/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.integration;

import ai.djl.integration.util.TestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class IntegrationTests {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTests.class);

    @Test
    public void runIntegrationTests() {
        String[] args = {};

        String[] engines;
        String defaultEngine = System.getProperty("ai.djl.default_engine");
        if (defaultEngine == null) {
            engines = new String[] {"TensorFlow"};
        } else {
            if (!"TensorFlow".equalsIgnoreCase(defaultEngine)) {
                throw new UnsupportedOperationException("Only Tensorflow engine supported.");
            }
            engines = new String[] {defaultEngine};
        }

        for (String engine : engines) {
            TestUtils.setEngine(engine);
            logger.info("Testing engine: {} ...", engine);
            Assert.assertTrue(new IntegrationTest(IntegrationTest.class).runTests(args));
        }
    }
}
