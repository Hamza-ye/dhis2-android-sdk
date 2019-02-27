/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.program;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ProgramRuleCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<ProgramRule> rules =
                d2.programModule().programRules
                        .get();
        
        assertThat(rules.size(), is(3));
    }

    @Test
    public void filter_by_priority() {
        List<ProgramRule> rules =
                d2.programModule().programRules
                        .byPriority()
                        .eq(2)
                        .get();

        assertThat(rules.size(), is(2));
    }

    @Test
    public void filter_by_condition() {
        List<ProgramRule> rules =
                d2.programModule().programRules
                        .byCondition()
                        .eq("#{hemoglobin} < 9")
                        .get();

        assertThat(rules.size(), is(1));
    }

    @Test
    public void filter_by_program() {
        List<ProgramRule> rules =
                d2.programModule().programRules
                        .byProgramUid()
                        .eq("lxAQ7Zs9VYR")
                        .get();

        assertThat(rules.size(), is(3));
    }

    @Test
    public void filter_by_program_stage() {
        List<ProgramRule> rules =
                d2.programModule().programRules
                        .byProgramStageUid()
                        .eq("dBwrot7S420")
                        .get();

        assertThat(rules.size(), is(1));
    }

    @Test
    public void include_program_rule_actions_as_children() {
        ProgramRule programRule = d2.programModule().programRules
                .one().getWithAllChildren();
        assertThat(programRule.programRuleActions().size(), is(1));
        assertThat(programRule.programRuleActions().get(0).content(), is("The hemoglobin value cannot be above 99"));
    }
}
