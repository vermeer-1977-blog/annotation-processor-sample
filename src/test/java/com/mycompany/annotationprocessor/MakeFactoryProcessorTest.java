/*
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
 *
 *  Copyright © 2017 Yamashita,Takahiro
 */
package com.mycompany.annotationprocessor;

import com.google.common.io.Resources;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.junit.Test;

public class MakeFactoryProcessorTest {

    @Test
    public void test() {
        Truth.assert_().about(JavaSourceSubjectFactory.javaSource())
                .that(JavaFileObjects.forResource(Resources.getResource("Sample.java")))
                .processedWith(new MakeFactoryProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource(Resources.getResource("SampleFactory.java")));
    }
}
